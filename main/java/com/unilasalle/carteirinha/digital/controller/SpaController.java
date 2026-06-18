package com.unilasalle.carteirinha.digital.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SpaController {
    @RequestMapping(value = "/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }
}



/*
@Controller
public class SpaController {

    @RequestMapping("/**")
    public String forward(HttpServletRequest request) {
        String uri = request.getRequestURI();
        
        // Se for chamada da API ou arquivo estático (com extensão), deixa o Spring servir
        if (uri.startsWith("/api") || uri.contains(".")) {
            return null;
        }
        
        // Caso contrário, redireciona para o index.html (React Router)
        return "forward:/index.html";
    }
}
*/