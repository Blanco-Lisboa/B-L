package com.blancolisboa.painel.persistence;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entidades JPA das tabelas reais (Supabase Postgres). Dormentes por padrão
 * (a auto-config de JPA fica desligada em application.properties); ativam com
 * o perfil "db". Mapeamento fiel às colunas principais do banco legado.
 */
public class Entidades {

    @Entity @Table(name = "empresas")
    public static class Empresa {
        @Id public UUID id;
        public String nome;
        public String slug;
        public String segmento;
        @Column(name="cor_predominante") public String corPredominante;
        @Column(name="logo_url") public String logoUrl;
        public boolean ativa;
    }

    @Entity @Table(name = "usuarios_internos")
    public static class UsuarioInterno {
        @Id public UUID id;
        public String nome;
        public String email;
        public String cpf;
        public String nivel;      // colaborador | assistente | gerente | diretor | ceo
        public boolean ativo;
        @Column(name="foto_url") public String fotoUrl;
    }

    @Entity @Table(name = "setores")
    public static class Setor {
        @Id public UUID id;
        public String nome;
        @Column(name="empresa_id") public UUID empresaId;
        @Column(name="eh_departamento") public boolean ehDepartamento;
    }

    @Entity @Table(name = "cupula_tarefas")
    public static class CupulaTarefa {
        @Id public UUID id;
        public String titulo;
        public String status;      // a_fazer | em_andamento | concluido
        public String prioridade;
        @Column(name="empresa_id") public UUID empresaId;
        @Column(name="setor_id") public UUID setorId;
    }
}
