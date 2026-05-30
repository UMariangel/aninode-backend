package com.aninode.backend.controllers;

import com.aninode.backend.models.User;
import com.aninode.backend.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // encriptador

    // Endpoint para registrar un usuario nuevo
    @PostMapping("/register")
    public User registerUser(@Valid @RequestBody User newUser) {
        // Ciframos la contraseña antes de guardarla en Clever Cloud
        String passwordCifrada = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(passwordCifrada);

        return userRepository.save(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        // 1. Buscamos al usuario por su nombre
        return userRepository.findByUsername(loginRequest.getUsername())
                .map(user -> {
                    // 4.  Usamos '.matches()' para comparar la clave abierta con la cifrada de la BD
                    if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                        return ResponseEntity.ok(user); // Login correcto
                    } else {
                        return ResponseEntity.status(401).body("Contraseña incorrecta");
                    }
                })
                .orElse(ResponseEntity.status(404).body("Usuario no encontrado"));
    }
}