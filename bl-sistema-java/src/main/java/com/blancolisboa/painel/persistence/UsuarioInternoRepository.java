package com.blancolisboa.painel.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

/** Repositório dos usuários internos (ativo só no perfil "db"). */
public interface UsuarioInternoRepository extends JpaRepository<Entidades.UsuarioInterno, UUID> {
    List<Entidades.UsuarioInterno> findByAtivoTrue();
}
