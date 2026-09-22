package com.blancolisboa.painel.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Fonte dos dados do grupo. Hoje devolve dados semeados (mesma base da prévia,
 * medidos no banco legado em 21/09/2026). Quando o banco real for ligado,
 * troque o corpo destes métodos pelas consultas via repositórios JPA
 * (empresas_listar, usuarios_internos_listar_com_nivel, setores...).
 */
@Service
public class GrupoService {

    public record Usuario(String nome, String nivel) {}
    public record Departamento(String nome, List<Usuario> usuarios) {}
    public record Empresa(String nome, String categoria, String cor, String sigla,
                          boolean ativa, List<Departamento> departamentos) {
        public int totalUsuarios() {
            return departamentos.stream().mapToInt(d -> d.usuarios().size()).sum();
        }
    }

    private List<Empresa> cache;

    public List<Empresa> empresas() {
        if (cache == null) cache = semear();
        return cache;
    }

    private static Usuario u(String nome, String nivel) { return new Usuario(nome, nivel); }
    private static Departamento d(String nome, Usuario... us) { return new Departamento(nome, List.of(us)); }

    private List<Empresa> semear() {
        List<Empresa> l = new ArrayList<>();

        List<Departamento> you = List.of(
            d("Direção", u("William Ramos", "ceo"), u("Lucas Lisboa", "ceo"),
                         u("Adrian Blanco", "ceo"), u("Diretor Geral", "diretor")),
            d("Fiscal", u("Denise E.", "assistente"), u("Aaron B.", "colaborador"),
                        u("Arthur B.", "colaborador"), u("Fernando J.", "colaborador"),
                        u("Gustavo V.", "colaborador"), u("Isabella S.", "colaborador"),
                        u("José E.", "colaborador"), u("Larissa M.", "colaborador"),
                        u("Natallia B.", "colaborador"), u("Pedro H.", "colaborador"),
                        u("Usuário T.", "colaborador")),
            d("Suporte", u("Paulo H.", "gerente"), u("Emilly d.", "colaborador"), u("John L.", "colaborador")),
            d("Financeiro", u("Carla S.", "colaborador"), u("Elaine C.", "colaborador"), u("Kechia F.", "colaborador")),
            d("Legal", u("Bruna B.", "colaborador"), u("Sannyha L.", "colaborador")),
            d("Societário", u("João V.", "colaborador")),
            d("Expansão", u("Gutemberg M.", "colaborador")),
            d("Recepção", u("Edilene T.", "colaborador")),
            d("Pessoal"),
            d("Contábil")
        );

        l.add(new Empresa("YOU Contabilidade", "Contabilidade", "#F04E23", "YOU", true, you));
        l.add(new Empresa("Realizze", "Certificados digitais", "#16A6A6", "RE", true, List.of(d("Operação"))));
        l.add(new Empresa("40%", "Consultoria de valores", "#1C1C1C", "40", true, List.of(d("Cobranças"))));
        l.add(new Empresa("BEEC", "Tráfego pago", "#6D28D9", "BE", false, List.of(d("Projetos"))));
        l.add(new Empresa("Gestão de Lojas", "Controle de lojas", "#0891B2", "GL", false, List.of(d("Operação"))));
        l.add(new Empresa("IRPF", "Imposto de renda", "#8A7B4F", "IR", false, List.of(d("Declarações"))));
        l.add(new Empresa("Winny", "ERP", "#F47A2D", "WI", false, List.of(d("Produto"))));
        l.add(new Empresa("IT.IA", "Tecnologia", "#050506", "IT", false, List.of(d("Engenharia"))));
        return l;
    }
}
