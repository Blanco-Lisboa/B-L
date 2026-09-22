package com.blancolisboa.painel.web;

import com.blancolisboa.painel.service.GrupoService;
import com.blancolisboa.painel.service.GrupoService.Empresa;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API Spring (o mesmo dado que a UI JavaFX usa). Sobe em http://localhost:8080.
 * Ex.: GET /api/empresas
 */
@RestController
@RequestMapping("/api")
public class GrupoRestController {

    private final GrupoService grupo;
    public GrupoRestController(GrupoService grupo) { this.grupo = grupo; }

    @GetMapping("/empresas")
    public List<Empresa> empresas() {
        return grupo.empresas();
    }
}
