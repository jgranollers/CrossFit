package com.example.crudthymeilif.Controller;

import com.example.crudthymeilif.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "Credencials incorrectes");
        }
        if (logout != null) {
            model.addAttribute("message", "Has tancat sessió correctament");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String dni,
                              @RequestParam String nom,
                              @RequestParam String correu,
                              @RequestParam String password,
                              @RequestParam(required = false) Integer telefon,
                              Model model) {
        try {
            authService.registerUser(dni, nom, correu, password, telefon);
            model.addAttribute("success", "Registre completat! Ara pots iniciar sessió.");
            return "auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
}
