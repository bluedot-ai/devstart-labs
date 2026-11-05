package com.vibecoding.tracker.service;

import com.vibecoding.tracker.domain.IssueCategory;
import com.vibecoding.tracker.repository.IssueCategoryRepository;
import com.vibecoding.tracker.web.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CategoryService {
    private final IssueCategoryRepository categories;
    private final com.vibecoding.tracker.repository.CustomerIssueRepository issues;
    public CategoryService(IssueCategoryRepository categories,com.vibecoding.tracker.repository.CustomerIssueRepository issues){this.categories=categories;this.issues=issues;}
    public List<IssueCategory> list(){return categories.findAllByOrderByNameAsc();}
    @Transactional public IssueCategory create(CategoryRequest request){
        if(categories.findByName(request.name().trim()).isPresent()) throw new IllegalArgumentException("分类名称已存在");
        IssueCategory category=new IssueCategory();category.setName(request.name().trim());category.setDescription(request.description());
        return categories.save(category);
    }
    @Transactional public IssueCategory update(Long id,CategoryRequest request){
        IssueCategory category=get(id);
        if(!category.getName().equals(request.name().trim()) && issues.countByCategory(category.getName())>0)
            throw new IllegalArgumentException("已有问题使用该分类，不能修改分类名称");
        categories.findByName(request.name().trim()).filter(found->!found.getId().equals(id))
            .ifPresent(found->{throw new IllegalArgumentException("分类名称已存在");});
        category.setName(request.name().trim());category.setDescription(request.description());return category;
    }
    @Transactional public IssueCategory setActive(Long id,boolean active){IssueCategory category=get(id);category.setActive(active);return category;}
    private IssueCategory get(Long id){return categories.findById(id).orElseThrow(()->new ResourceNotFoundException("分类不存在"));}
}
