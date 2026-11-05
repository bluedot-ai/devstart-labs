package com.vibecoding.tracker.web;

import com.vibecoding.tracker.domain.IssueCategory;
import com.vibecoding.tracker.service.CategoryService;
import com.vibecoding.tracker.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService service;
    public CategoryController(CategoryService service){this.service=service;}
    @GetMapping public List<IssueCategory> list(){return service.list();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasRole('ADMIN')")
    public IssueCategory create(@Valid @RequestBody CategoryRequest request){return service.create(request);}
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public IssueCategory update(@PathVariable Long id,@Valid @RequestBody CategoryRequest request){return service.update(id,request);}
    @PatchMapping("/{id}/active") @PreAuthorize("hasRole('ADMIN')")
    public IssueCategory setActive(@PathVariable Long id,@Valid @RequestBody ActiveUpdateRequest request){return service.setActive(id,request.active());}
}
