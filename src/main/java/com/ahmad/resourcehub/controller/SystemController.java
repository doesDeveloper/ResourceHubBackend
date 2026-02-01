package com.ahmad.resourcehub.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class SystemController {
    private final DataSource dataSource;

    @GetMapping("/api/public/health")
    public ResponseEntity<Map<String, Boolean>> getHealth() {
        boolean database_up = false;
        try (Connection con = dataSource.getConnection()) {
            database_up = true;
        } catch (SQLException ignored) {
            database_up =false;
        }
       Map<String, Boolean> status = new HashMap<>();
        status.put("Backend", true);
        status.put("Database", database_up);
        return ResponseEntity.ok(status);
    }

}
