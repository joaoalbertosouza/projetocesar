package com.java.projetocesar;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;

public class InMemoryUserDetailsService implements UserDetailsService {

    private InMemoryUserDetailsManager userDetailsManager;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public InMemoryUserDetailsService(PasswordEncoder passwordEncoder) {
        this.userDetailsManager = new InMemoryUserDetailsManager();
        this.passwordEncoder = passwordEncoder;
    }

    public void loadUsersFromCSV(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("tipo", "nome", "email", "senha")
                    .parse(reader);
            for (CSVRecord record : records) {
                String tipo = record.get("tipo");
                String nome = record.get("nome");
                String email = record.get("email");
                String senha = record.get("senha");

                UserDetails user = User.withUsername(email)
                        .password(passwordEncoder.encode(senha))
                        .roles(tipo.toUpperCase())
                        .build();
                userDetailsManager.createUser(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsManager.loadUserByUsername(username);
    }
}