package com.java.projetocesar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

@Controller
public class UploadController {

    private final InMemoryUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UploadController(InMemoryUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Certifique-se de que há um arquivo login.html em src/main/resources/templates
    }

    @PostMapping("/uploadLogins")
    public String uploadLogins(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/login";
        }

        try (InputStream inputStream = file.getInputStream()) {
            userDetailsService.loadUsersFromCSV(inputStream);
            redirectAttributes.addFlashAttribute("message", "Logins uploaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload logins");
        }

        return "redirect:/login";
    }

    @PostMapping("/uploadElectives")
    public String uploadElectives(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/login";
        }

        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("elective", "teacher", "spots")
                    .parse(reader);
            for (CSVRecord record : records) {
                String elective = record.get("elective");
                String teacher = record.get("teacher");
                String spots = record.get("spots");
                // Processar as informações conforme necessário
                System.out.println("Elective: " + elective + ", Teacher: " + teacher + ", Spots: " + spots);
            }
            redirectAttributes.addFlashAttribute("message", "Electives uploaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload electives");
        }

        return "redirect:/login";
    }
}