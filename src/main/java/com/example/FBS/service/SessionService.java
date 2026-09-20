package com.example.FBS.service;

import com.example.FBS.entity.User;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Lightweight server-side login session.  The browser never chooses its own role. */
@Service
public class SessionService {
    private final ConcurrentHashMap<String, User> sessions = new ConcurrentHashMap<>();
    public String create(User user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return token;
    }
    public User get(String token) { return token == null ? null : sessions.get(token); }
    public boolean hasRole(String token, String... roles) {
        User user = get(token);
        if (user == null) return false;
        for (String role : roles) if (role.equals(user.getRole())) return true;
        return false;
    }
}
