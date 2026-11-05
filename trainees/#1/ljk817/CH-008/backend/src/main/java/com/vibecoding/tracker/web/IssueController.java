package com.vibecoding.tracker.web;

import com.vibecoding.tracker.domain.*;
import com.vibecoding.tracker.service.IssueService;
import com.vibecoding.tracker.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/issues")
public class IssueController {
    private final IssueService service;
    public IssueController(IssueService service) { this.service = service; }

    @GetMapping
    public Page<IssueResponse> list(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) IssueStatus status,
                                    @RequestParam(required = false) Severity severity,
                                    @RequestParam(required = false) Long customerId,
                                    @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.list(keyword, status, severity, customerId, pageable);
    }
    @GetMapping("/{id}") public IssueResponse get(@PathVariable Long id) { return service.get(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','AGENT','CUSTOMER')")
    public IssueResponse create(@Valid @RequestBody IssueRequest request) { return service.create(request); }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    public IssueResponse update(@PathVariable Long id,@Valid @RequestBody IssueRequest request){return service.update(id,request);}
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    public IssueResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return service.updateStatus(id, request);
    }
    @GetMapping("/{id}/records") public List<RecordResponse> records(@PathVariable Long id) { return service.records(id); }
    @PostMapping("/{id}/records") @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','AGENT','CUSTOMER')")
    public RecordResponse addRecord(@PathVariable Long id, @Valid @RequestBody RecordRequest request) {
        return service.addRecord(id, request);
    }
}
