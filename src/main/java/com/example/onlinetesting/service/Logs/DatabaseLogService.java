package com.example.onlinetesting.service.Logs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseLogService {

    private DataSource dataSource;
    @Autowired
    public DatabaseLogService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveLog(String level, String message) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO logs (level, message) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, level);
                pstmt.setString(2, message);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, String>> getAllLogs() {
        List<Map<String, String>> logs = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM logs ORDER BY id DESC"; // Сортировка по убыванию id
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setMaxRows(100); // Пример: ограничиваем количество записей 100
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, String> log = new HashMap<>();
                        log.put("id", rs.getString("id"));
                        log.put("level", rs.getString("level"));
                        log.put("message", rs.getString("message"));
                        logs.add(log);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }


    public void setUrl(String url) {
    }

    public void setUsername(String username) {
    }

    public void setPassword(String password) {
    }
}