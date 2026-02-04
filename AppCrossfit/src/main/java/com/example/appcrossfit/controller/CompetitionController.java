package com.example.appcrossfit.controller;

import com.example.appcrossfit.model.Competicion;
import com.example.appcrossfit.repository.CompeticionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CompetitionController {

    @Autowired
    private CompeticionRepository competicionRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<Competicion> competiciones = competicionRepository.findAll();
        model.addAttribute("competiciones", competiciones);
        return "index";
    }
}


