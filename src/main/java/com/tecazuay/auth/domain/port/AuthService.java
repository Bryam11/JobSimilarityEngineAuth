package com.tecazuay.auth.domain.port;

import com.tecazuay.auth.domain.model.User;

public interface AuthService {
    User registerUser(User user);
    String authenticateUser(String email, String password);
}
