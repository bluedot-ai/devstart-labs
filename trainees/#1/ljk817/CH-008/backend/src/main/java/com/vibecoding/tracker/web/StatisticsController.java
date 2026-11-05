package com.vibecoding.tracker.web;

import com.vibecoding.tracker.service.IssueService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final IssueService service;
    public StatisticsController(IssueService service) { this.service = service; }
    @GetMapping("/overview") @PreAuthorize("hasAnyRole('ADMIN','AGENT','VIEWER')")
    public Map<String, Object> overview() { return service.overview(); }
}
