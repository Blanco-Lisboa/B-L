package com.blancolisboa.painel.ui.views;

import com.blancolisboa.painel.service.GrupoService;
import com.blancolisboa.painel.service.GrupoService.Departamento;
import com.blancolisboa.painel.service.GrupoService.Empresa;
import com.blancolisboa.painel.service.GrupoService.Usuario;
import com.blancolisboa.painel.ui.dialogs.GerenciarAcessoDialog;
import com.blancolisboa.painel.ui.dialogs.NovoUsuarioWizard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/** Módulo Empresas: grade de cards (com baixar app) -> entrar -> departamentos e usuários. */
public class EmpresasView {

    private final GrupoService grupo;
    private final StackPane raiz = new StackPane();

    public EmpresasView(GrupoService grupo) { this.grupo = grupo; }

    public Node build() {
        mostrarCards();
        return raiz;
    }

    // ---------- nível 1: cards ----------
    private void mostrarCards() {
        VBox box = new VBox(6);
        Label h = new Label("Empresas");
        h.getStyleClass().add("h1");
        Label sub = new Label("O grupo, empresa por empresa. Baixe o app de cada uma e entre para ver departamentos e usuários.");
        sub.getStyleClass().add("sub");
        box.getChildren().addAll(h, sub);

        TilePane grid = new TilePane(14, 14);
        grid.setPadding(new Insets(20, 0, 0, 0));
        grid.setPrefColumns(4);
        for (Empresa e : grupo.empresas()) grid.getChildren().add(card(e));
        box.getChildren().add(grid);

        ScrollPane sp = new ScrollPane(box);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent;");
        raiz.getChildren().setAll(sp);
    }

    private Node card(Empresa e) {
        VBox c = new VBox(12);
        c.getStyleClass().add("card");
        c.setPrefWidth(250);
        if (!e.ativa()) c.setOpacity(0.62);

        Button baixar = new Button("⬇");
        baixar.getStyleClass().add("icon-btn");
        baixar.setTooltip(new Tooltip("Baixar o app (Java) da " + e.nome()));
        baixar.setOnAction(ev -> {
            ev.consume();
            info("Baixar app", "Instalador (Java) da " + e.nome() + " — bucket desktop-updates.");
        });

        HBox top = new HBox(12);
        top.setAlignment(Pos.CENTER_LEFT);
        Label logo = logo(e, 46);
        VBox nm = new VBox(2);
        Label n = new Label(e.nome()); n.getStyleClass().add("emp-nome");
        Label cat = new Label(e.categoria().toUpperCase()); cat.getStyleClass().add("emp-cat");
        nm.getChildren().addAll(n, cat);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        top.getChildren().addAll(logo, nm, sp, baixar);

        HBox foot = new HBox(8);
        foot.setAlignment(Pos.CENTER_LEFT);
        Label pill = new Label(e.ativa() ? "Ativa" : "Inativa");
        pill.getStyleClass().addAll("pill", e.ativa() ? "on" : "off");
        Label us = new Label(e.totalUsuarios() + " usuário(s)");
        us.getStyleClass().add("sub");
        foot.getChildren().addAll(pill, us);

        c.getChildren().addAll(top, foot);
        c.setStyle("-fx-cursor: hand; -fx-border-color:" + e.cor() + "; -fx-border-width:0 0 0 4; -fx-border-radius:12;");
        c.setOnMouseClicked(ev -> mostrarDetalhe(e));
        return c;
    }

    // ---------- nível 2: entrar na empresa ----------
    private void mostrarDetalhe(Empresa e) {
        VBox box = new VBox(6);

        HBox top = new HBox(14);
        top.setAlignment(Pos.CENTER_LEFT);
        Button voltar = new Button("‹");
        voltar.getStyleClass().add("icon-btn");
        voltar.setOnAction(ev -> mostrarCards());
        Label logo = logo(e, 52);
        VBox nm = new VBox(2);
        Label n = new Label(e.nome()); n.getStyleClass().add("h1");
        Label sub = new Label(e.categoria() + " · " + e.totalUsuarios() + " usuários · " + e.departamentos().size() + " departamentos");
        sub.getStyleClass().add("sub");
        nm.getChildren().addAll(n, sub);
        Region gap = new Region(); HBox.setHgrow(gap, Priority.ALWAYS);
        Button baixar = new Button("⬇ Baixar app (Java)");
        baixar.getStyleClass().add("btn-ghost");
        baixar.setOnAction(ev -> info("Baixar app", "Instalador (Java) da " + e.nome() + "."));
        Button novo = new Button("+ Novo usuário");
        novo.getStyleClass().add("btn-cta");
        novo.setOnAction(ev -> new NovoUsuarioWizard(e.nome(), res -> mostrarDetalhe(e)).show());
        top.getChildren().addAll(voltar, logo, nm, gap, baixar, novo);
        box.getChildren().add(top);

        Label depsT = new Label("DEPARTAMENTOS · " + e.departamentos().size());
        depsT.getStyleClass().add("sec-t");
        depsT.setPadding(new Insets(18, 0, 10, 0));
        box.getChildren().add(depsT);

        TilePane deps = new TilePane(10, 10);
        deps.setPrefColumns(3);
        for (Departamento d : e.departamentos()) deps.getChildren().add(depCard(d));
        box.getChildren().add(deps);

        ScrollPane sp = new ScrollPane(box);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent;");
        raiz.getChildren().setAll(sp);
    }

    private Node depCard(Departamento d) {
        VBox c = new VBox(9);
        c.getStyleClass().add("card");
        c.setPrefWidth(250);
        HBox h = new HBox(8);
        h.setAlignment(Pos.CENTER_LEFT);
        Label nm = new Label(d.nome()); nm.getStyleClass().add("emp-nome");
        Region gap = new Region(); HBox.setHgrow(gap, Priority.ALWAYS);
        Label q = new Label(String.valueOf(d.usuarios().size())); q.getStyleClass().add("pill");
        h.getChildren().addAll(nm, gap, q);
        c.getChildren().add(h);

        if (d.usuarios().isEmpty()) {
            Label vazio = new Label("sem usuários");
            vazio.getStyleClass().add("sub");
            c.getChildren().add(vazio);
        } else {
            for (Usuario u : d.usuarios()) c.getChildren().add(userRow(u));
        }
        return c;
    }

    private Node userRow(Usuario u) {
        HBox r = new HBox(9);
        r.setAlignment(Pos.CENTER_LEFT);
        r.setPadding(new Insets(4, 4, 4, 4));
        Label av = new Label(iniciais(u.nome()));
        av.getStyleClass().add("avatar");
        Label nm = new Label(u.nome());
        nm.getStyleClass().add("u-nome");
        Region gap = new Region(); HBox.setHgrow(gap, Priority.ALWAYS);
        Label nv = new Label(u.nivel());
        nv.getStyleClass().addAll("nivel", u.nivel());
        Button gear = new Button("⚙");
        gear.getStyleClass().add("icon-btn");
        gear.setTooltip(new Tooltip("Gerenciar acesso"));
        gear.setOnAction(ev -> new GerenciarAcessoDialog(u.nome(), u.nivel()).show());
        r.getChildren().addAll(av, nm, gap, nv, gear);
        return r;
    }

    // ---------- helpers ----------
    private Label logo(Empresa e, int size) {
        Label l = new Label(e.sigla());
        l.getStyleClass().add("emp-logo");
        l.setMinSize(size, size);
        l.setPrefSize(size, size);
        l.setAlignment(Pos.CENTER);
        l.setStyle("-fx-background-color:" + e.cor() + "; -fx-background-radius:12;");
        return l;
    }

    private static String iniciais(String nome) {
        String[] p = nome.trim().split("\\s+");
        String s = p.length > 1 ? ("" + p[0].charAt(0) + p[p.length - 1].charAt(0)) : nome.substring(0, Math.min(2, nome.length()));
        return s.toUpperCase();
    }

    private void info(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(titulo);
        a.setTitle(titulo);
        a.showAndWait();
    }
}
