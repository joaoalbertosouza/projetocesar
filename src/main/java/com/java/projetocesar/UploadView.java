package com.java.projetocesar;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("")
public class UploadView extends VerticalLayout {

    private final InMemoryUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UploadView(InMemoryUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;

        // Upload para logins de usuários
        MemoryBuffer loginBuffer = new MemoryBuffer();
        Upload loginUpload = new Upload(loginBuffer);
        loginUpload.setAcceptedFileTypes("text/csv");
        loginUpload.setMaxFiles(1);
        loginUpload.setDropLabel(new Label("Upload Login CSV"));

        Button loginButton = new Button("Upload Logins", event -> {
            InputStream inputStream = loginBuffer.getInputStream();
            try {
                userDetailsService.loadUsersFromCSV(inputStream);
                Notification.show("Logins uploaded successfully");
            } catch (Exception e) {
                e.printStackTrace();
                Notification.show("Failed to upload logins");
            }
        });
        loginButton.getStyle().set("margin-top", "10px"); // Adiciona margem superior ao botão

        // Upload para eletivas
        MemoryBuffer electiveBuffer = new MemoryBuffer();
        Upload electiveUpload = new Upload(electiveBuffer);
        electiveUpload.setAcceptedFileTypes("text/csv");
        electiveUpload.setMaxFiles(1);
        electiveUpload.setDropLabel(new Label("Upload Elective CSV"));

        Button electiveButton = new Button("Upload Electives", event -> {
            InputStream inputStream = electiveBuffer.getInputStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
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
                Notification.show("Electives uploaded successfully");
            } catch (Exception e) {
                e.printStackTrace();
                Notification.show("Failed to upload electives");
            }
        });
        electiveButton.getStyle().set("margin-top", "10px"); // Adiciona margem superior ao botão

        // Adicionar componentes ao layout
        add(new Label("Login Upload Section"));
        add(loginUpload);
        add(loginButton);

        add(new Label("Elective Upload Section"));
        add(electiveUpload);
        add(electiveButton);

        add(new Button("Logout", e -> getUI().ifPresent(ui -> ui.getPage().setLocation("/logout"))));
    }
}