package com.fastcampus.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Operation(summary = "go to main page")
    @GetMapping("/")
    public String root() {
        return "forward:/articles";
    }
}
