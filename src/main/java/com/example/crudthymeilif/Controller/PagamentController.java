package com.example.crudthymeilif.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pagaments")
public class PagamentController {

    @Value("${stripe.public.key:pk_test_default}")
    private String stripePublicKey;

    @GetMapping
    public String mostrarBotiga(Model model) {
        model.addAttribute("stripePublicKey", stripePublicKey);
        return "pagaments/botiga";
    }

    @GetMapping("/exit")
    public String pagamentExit() {
        return "pagaments/exit";
    }

    @GetMapping("/cancelat")
    public String pagamentCancelat() {
        return "pagaments/cancelat";
    }

    @GetMapping("/historial")
    public String mostrarHistorial(Model model) {
        return "pagaments/historial";
    }
}
