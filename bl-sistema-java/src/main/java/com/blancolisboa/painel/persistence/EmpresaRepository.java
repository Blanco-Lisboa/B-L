package com.blancolisboa.painel.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

/** Repositório das empresas (ativo só no perfil "db"). RPC equivalente: empresas_listar. */
public interface EmpresaRepository extends JpaRepository<Entidades.Empresa, UUID> {
}
