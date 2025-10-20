package com.lostandfound.service;

import com.lostandfound.dto.LoginRequest;
import com.lostandfound.dto.LoginResponse;
import com.lostandfound.model.Admin;
import com.lostandfound.repository.AdminRepository;
import com.lostandfound.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(request.getUsername());
        
        if (adminOpt.isEmpty()) {
            return new LoginResponse(null, null, null, "Invalid username or password");
        }

        Admin admin = adminOpt.get();
        
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            return new LoginResponse(null, null, null, "Invalid username or password");
        }

        String token = jwtUtil.generateToken(admin.getUsername());
        return new LoginResponse(token, admin.getUsername(), admin.getFullName(), "Login successful");
    }

    public Admin createAdmin(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    public boolean validateToken(String token, String username) {
        return jwtUtil.validateToken(token, username);
    }
}
