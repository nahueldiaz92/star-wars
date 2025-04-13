package com.conexa.starwars.service;

import com.conexa.starwars.dto.auth.LoginRequest;
import com.conexa.starwars.dto.auth.RegisterRequest;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    public void registerUser(RegisterRequest request);
    public void login (LoginRequest loginRequest, HttpServletRequest request);
}
