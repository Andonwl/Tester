package com.bridgetrack.bridgetrack.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CatalogPageController {

    @GetMapping({"/catalog", "/student/catalog"})
    public String showCatalogPage(HttpServletRequest request, Model model) {
        boolean studentView = request.getRequestURI().startsWith("/student/");
        model.addAttribute("studentView", studentView);
        return "course-catalog";
    }
}