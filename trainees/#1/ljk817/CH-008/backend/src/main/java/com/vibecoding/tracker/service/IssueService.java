package com.vibecoding.tracker.service;

import com.vibecoding.tracker.domain.*;
import com.vibecoding.tracker.repository.*;
import com.vibecoding.tracker.web.dto.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class IssueService {
    private final CustomerIssueRepository issueRepository;
    private final CustomerRepository customerRepository;
    private final IssueRecordRepository recordRepository;
    private final IssueCategoryRepository categoryRepository;
    private final SystemUserRepository userRepository;
    private final CurrentUserService currentUsers;

    public IssueService(CustomerIssueRepository issueRepository, CustomerRepository customerRepository,
                        IssueRecordRepository recordRepository, IssueCategoryRepository categoryRepository,
                        SystemUserRepository userRepository, CurrentUserService currentUsers) {
        this.issueRepository = issueRepository;
        this.customerRepository = customerRepository;
        this.recordRepository = recordRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.currentUsers = currentUsers;
    }

    @Transactional(readOnly = true)
    public Page<IssueResponse> list(String keyword, IssueStatus status, Severity severity, Long customerId, Pageable pageable) {
        Specification<CustomerIssue> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("issueNo")), like)));
            }
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (severity != null) predicates.add(cb.equal(root.get("severity"), severity));
            if (customerId != null) predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            if (currentUsers.isCustomer()) {
                predicates.add(cb.equal(root.get("customer").get("id"), currentUsers.customerId()));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return issueRepository.findAll(spec, pageable).map(IssueResponse::from);
    }

    @Transactional(readOnly = true)
    public IssueResponse get(Long id) { return IssueResponse.from(findAccessible(id)); }

    @Transactional
    public IssueResponse create(IssueRequest request) {
        Long requestedCustomerId = currentUsers.isCustomer() ? currentUsers.customerId() : request.customerId();
        if (requestedCustomerId == null) throw new IllegalArgumentException("请选择客户");
        Customer customer = customerRepository.findById(requestedCustomerId)
            .orElseThrow(() -> new ResourceNotFoundException("客户不存在"));
        if (!customer.isActive()) throw new IllegalArgumentException("已停用客户不能创建新问题");
        validateCategory(request.category());
        if (!currentUsers.isCustomer()) validateAssignee(request.assignee());
        CustomerIssue issue = new CustomerIssue();
        issue.setIssueNo("ISSUE-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
            + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        issue.setCustomer(customer);
        issue.setTitle(request.title().trim());
        issue.setDescription(request.description().trim());
        issue.setCategory(request.category().trim());
        issue.setSeverity(request.severity());
        issue.setStatus(IssueStatus.OPEN);
        issue.setAssignee(currentUsers.isCustomer() ? null : request.assignee());
        return IssueResponse.from(issueRepository.save(issue));
    }

    @Transactional
    public IssueResponse update(Long id, IssueRequest request) {
        CustomerIssue issue=find(id);
        Customer customer=customerRepository.findById(request.customerId()).orElseThrow(()->new ResourceNotFoundException("客户不存在"));
        validateCategory(request.category());validateAssignee(request.assignee());
        issue.setCustomer(customer);issue.setTitle(request.title().trim());issue.setDescription(request.description().trim());
        issue.setCategory(request.category().trim());issue.setSeverity(request.severity());issue.setAssignee(request.assignee());
        return IssueResponse.from(issue);
    }

    @Transactional
    public IssueResponse updateStatus(Long id, StatusUpdateRequest request) {
        CustomerIssue issue = find(id);
        IssueStatus oldStatus = issue.getStatus();
        if (oldStatus == request.status()) throw new IllegalArgumentException("问题已经处于该状态");
        if (!allowedTransitions(oldStatus).contains(request.status())) {
            throw new IllegalArgumentException("不允许从 " + oldStatus + " 变更为 " + request.status());
        }
        issue.setStatus(request.status());
        issue.setClosedAt(request.status() == IssueStatus.CLOSED ? LocalDateTime.now() : null);
        IssueRecord record = new IssueRecord();
        record.setIssue(issue);
        record.setOperatorName(currentOperator());
        record.setAuthorRole(currentUsers.get().getRole());
        record.setCustomerVisible(false);
        record.setContent(request.note() == null || request.note().isBlank()
            ? "状态由 " + oldStatus + " 变更为 " + request.status() : request.note().trim());
        record.setOldStatus(oldStatus);
        record.setNewStatus(request.status());
        recordRepository.save(record);
        return IssueResponse.from(issue);
    }

    @Transactional
    public RecordResponse addRecord(Long id, RecordRequest request) {
        SystemUser author = currentUsers.get();
        IssueRecord record = new IssueRecord();
        record.setIssue(findAccessible(id));
        record.setContent(request.content().trim());
        record.setOperatorName(author.getDisplayName());
        record.setAuthorRole(author.getRole());
        record.setCustomerVisible(author.getRole() == UserRole.CUSTOMER || request.customerVisible());
        return RecordResponse.from(recordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<RecordResponse> records(Long id) {
        findAccessible(id);
        List<IssueRecord> records = currentUsers.isCustomer()
            ? recordRepository.findByIssueIdAndCustomerVisibleTrueOrderByCreatedAtDesc(id)
            : recordRepository.findByIssueIdOrderByCreatedAtDesc(id);
        return records.stream().map(RecordResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> overview() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", issueRepository.count());
        result.put("open", issueRepository.countByStatus(IssueStatus.OPEN));
        result.put("processing", issueRepository.countByStatus(IssueStatus.PROCESSING));
        result.put("critical", issueRepository.countBySeverity(Severity.CRITICAL));
        result.put("closed", issueRepository.countByStatus(IssueStatus.CLOSED));
        return result;
    }

    private CustomerIssue find(Long id) {
        return issueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("问题不存在"));
    }

    private CustomerIssue findAccessible(Long id) {
        CustomerIssue issue = find(id);
        if (currentUsers.isCustomer() && !issue.getCustomer().getId().equals(currentUsers.customerId())) {
            throw new ResourceNotFoundException("问题不存在");
        }
        return issue;
    }

    private String currentOperator() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private void validateCategory(String name) {
        IssueCategory category=categoryRepository.findByName(name.trim()).orElseThrow(()->new IllegalArgumentException("请选择有效的问题分类"));
        if(!category.isActive()) throw new IllegalArgumentException("该问题分类已停用");
    }

    private void validateAssignee(String username) {
        if(username==null||username.isBlank()) return;
        SystemUser user=userRepository.findByUsername(username).orElseThrow(()->new IllegalArgumentException("负责人账号不存在"));
        if(!user.isEnabled() || (user.getRole()!=UserRole.ADMIN && user.getRole()!=UserRole.AGENT))
            throw new IllegalArgumentException("负责人必须是已启用的管理员或处理人员");
    }

    private Set<IssueStatus> allowedTransitions(IssueStatus status) {
        return switch(status){
            case OPEN -> Set.of(IssueStatus.PROCESSING);
            case PROCESSING -> Set.of(IssueStatus.OPEN,IssueStatus.RESOLVED);
            case RESOLVED -> Set.of(IssueStatus.PROCESSING,IssueStatus.CLOSED);
            case CLOSED -> Set.of(IssueStatus.PROCESSING);
        };
    }
}
