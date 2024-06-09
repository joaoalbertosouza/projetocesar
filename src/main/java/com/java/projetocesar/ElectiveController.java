package com.java.projetocesar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ElectiveController {

    @Autowired
    private InMemoryUserDetailsService userDetailsService;

    @GetMapping("/electives")
    public String viewElectives(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        Student student = userDetailsService.getStudentByUsername(username);

        if (student != null) {
            List<Elective> availableElectives = userDetailsService.getElectives().stream()
                    .filter(e -> e.getGradeYear() == student.getGradeYear() && e.getSpots() > 0)
                    .collect(Collectors.toList());
            model.addAttribute("electives", availableElectives);
        }

        return "electives";
    }
}