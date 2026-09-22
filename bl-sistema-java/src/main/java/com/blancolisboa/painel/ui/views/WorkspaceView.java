package com.blancolisboa.painel.ui.views;

import com.blancolisboa.painel.ui.UI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;

/** Workspace do CEO (Cúpula): Hoje / Enviadas / Recebidas — demandas e tickets. */
public class WorkspaceView {

    private record Dem(String cod, String titulo, String dono, String empresa, int feitos, int total, String estado) {}
    private final List<Dem> DEMS = List.of(
        new Dem("D-897","Abertura CNPJ — Valquiria Ludy Screen","Lucas Lisboa","YOU",0,2,"aberto"),
        new Dem("D-894","Yure Allen Rocha — alteração","Lucas Lisboa","YOU",0,1,"andamento"),
        new Dem("D-877","Abrir empresa LTDA","Lucas Lisboa","YOU",1,2,"andamento"),
        new Dem("D-860","Regularização fiscal — Modas Jô Love","Denise E.","YOU",2,3,"aguardando"),
        new Dem("D-812","Fila única WhatsApp + Cúpula","William Ramos","IT.IA",4,4,"resolvido")
    );

    private final BorderPane root = new BorderPane();
    private String aba = "Hoje";
    private String modo = "Cúpula";

    public Node build() {
        root.setTop(topoModo());
        if (modo.equals("Vault")) root.setCenter(new VaultView().build());
        else root.setCenter(UI.scroll(conteudo()));
        return root;
    }

    private Node topoModo() {
        HBox h = new HBox(6);
        h.setPadding(new Insets(14,24,0,24));
        for (String s : new String[]{"Cúpula","Vault"}) {
            Button b = new Button(s); b.getStyleClass().add("seg-b");
            if (s.equals(modo)) b.getStyleClass().add("on");
            b.setOnAction(e -> { modo = s; build(); });
            h.getChildren().add(b);
        }
        return h;
    }

    private Node conteudo() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(22,24,40,24));
        box.getChildren().addAll(UI.h1("Workspace · Cúpula"),
            UI.sub("As demandas e tickets do CEO. Cada demanda tem seus tickets, cada ticket com sua empresa."));

        HBox seg = new HBox(6);
        seg.setPadding(new Insets(14,0,0,0));
        for (String s : new String[]{"Hoje","Enviadas","Recebidas"}) {
            Button b = new Button(s);
            b.getStyleClass().add("seg-b");
            if (s.equals(aba)) b.getStyleClass().add("on");
            b.setOnAction(e -> { aba = s; root.setCenter(UI.scroll(conteudo())); });
            seg.getChildren().add(b);
        }
        box.getChildren().add(seg);

        VBox l = new VBox(10);
        l.setPadding(new Insets(16,0,0,0));
        l.setMaxWidth(860);
        for (Dem d : DEMS) l.getChildren().add(card(d));
        box.getChildren().add(l);
        return box;
    }

    private Node card(Dem d) {
        VBox c = UI.card();
        HBox top = UI.row(10);
        Label cod = new Label(d.cod()); cod.getStyleClass().add("cod");
        top.getChildren().addAll(cod, UI.chip(d.empresa(),"info"), UI.grow(), UI.chip(estado(d.estado()), estadoCls(d.estado())));
        Label tit = new Label(d.titulo()); tit.getStyleClass().add("card-tit");
        HBox prog = UI.row(10, new ProgressoBar(d.feitos(), d.total()), new Label(d.feitos()+" de "+d.total()+" tickets"));
        ((Label) prog.getChildren().get(1)).getStyleClass().add("sub");
        Label dono = new Label("dono: "+d.dono()); dono.getStyleClass().add("sub");
        c.getChildren().addAll(top, tit, prog, dono);
        c.setStyle("-fx-cursor:hand;");
        c.setOnMouseClicked(ev -> detalhe(d));
        return c;
    }

    private void detalhe(Dem d) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(22,24,40,24));
        Button voltar = new Button("‹ Voltar"); voltar.getStyleClass().add("btn-ghost"); voltar.setOnAction(a -> root.setCenter(UI.scroll(conteudo())));
        Label cod = new Label(d.cod()); cod.getStyleClass().add("cod");
        box.getChildren().add(UI.row(12, voltar, cod, UI.h1(d.titulo())));
        box.getChildren().add(UI.row(8, UI.chip(d.empresa(),"info"), UI.chip(estado(d.estado()), estadoCls(d.estado())), new Label("dono: "+d.dono())));

        box.getChildren().add(UI.secT("Tickets ("+d.feitos()+"/"+d.total()+")"));
        VBox tks = new VBox(8); tks.setMaxWidth(760);
        String[] ests = {"concluído","em andamento","a fazer","a fazer"};
        for (int i=0;i<d.total();i++) {
            VBox t = UI.card();
            boolean feito = i < d.feitos();
            Label tt = new Label("T-"+(i+1)+" · "+(feito?"entregue":"pendente")); tt.getStyleClass().add("card-tit");
            t.getChildren().addAll(UI.row(8, UI.chip(feito?"concluído":ests[Math.min(i,ests.length-1)], feito?"ok":"att"), tt),
                UI.sub("Responsável do ticket · empresa "+d.empresa()));
            tks.getChildren().add(t);
        }
        box.getChildren().add(tks);

        box.getChildren().add(UI.secT("Ações"));
        HBox acoes = new HBox(8,
            btn("+ Adicionar ticket", true), btn("Virar processo (Societário)", false),
            btn("Pedir revisão geral", false), btn("Cobrar todos", false));
        acoes.setStyle("-fx-flow-pane-wrap-length:700;");
        box.getChildren().add(acoes);
        root.setCenter(UI.scroll(box));
    }
    private Button btn(String t, boolean prim){ Button b=new Button(t); b.getStyleClass().add(prim?"btn-cta":"btn-ghost"); return b; }

    private static String estado(String e){ return switch(e){ case "aberto"->"A fazer"; case "andamento"->"Em andamento"; case "aguardando"->"Aguardando"; default->"Concluído"; }; }
    private static String estadoCls(String e){ return switch(e){ case "aberto"->"att"; case "andamento"->"info"; case "aguardando"->"roxo"; default->"ok"; }; }

    private static class ProgressoBar extends StackPane {
        ProgressoBar(int feitos, int total) {
            setMinSize(130, 8); setMaxSize(130, 8);
            setStyle("-fx-background-color:#E1E6EB; -fx-background-radius:4;");
            Region fill = new Region();
            double pct = total==0?0:(double)feitos/total;
            fill.setStyle("-fx-background-color:#011D3A; -fx-background-radius:4;");
            fill.setMinWidth(130*pct); fill.setMaxWidth(130*pct); fill.setPrefHeight(8);
            setAlignment(Pos.CENTER_LEFT);
            getChildren().add(fill);
        }
    }
}
