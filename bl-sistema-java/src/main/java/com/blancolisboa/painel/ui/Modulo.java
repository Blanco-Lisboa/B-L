package com.blancolisboa.painel.ui;

/** Os módulos da barra lateral (mesma ordem da prévia). */
public enum Modulo {
    DASHBOARD("Dashboard"),
    WORKSPACE("Workspace"),
    TEAMS("TEAM's"),
    CLIENTES("Clientes"),
    EMPRESAS("Empresas"),
    FINANCEIRO("Financeiro"),
    INTEGRACOES("Integrações"),
    MONITORAMENTO("Monitoramento"),
    CONFIGURACOES("Configurações");

    public final String label;
    Modulo(String label) { this.label = label; }
}
