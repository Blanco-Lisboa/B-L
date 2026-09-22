package com.blancolisboa.painel.ui.views;

import com.blancolisboa.painel.ui.UI;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/** Vault: mural por área (notas/canvas) → abrir nota (editor) e Visão estratégica (pipeline). */
public class VaultView {

    private record Nota(String titulo, String prev, String autor, String tipo) {}
    private record Col(String area, String modo, List<Nota> notas) {}
    private final List<Col> MURAL = List.of(
        new Col("Ideias","só você", List.of(
            new Nota("Painel único de cobrança","E se a 40% e a YOU dividissem a fila…","William Ramos","Nota"),
            new Nota("Rascunho discurso fim de ano","Agradecer o time, as 5 empresas…","William Ramos","Nota"))),
        new Col("Projetos Pessoais","só você", List.of(
            new Nota("Curso de gestão","Módulos 1 a 4 feitos, falta o TCC","William Ramos","Nota"),
            new Nota("Casa de praia · reforma","Orçamentos: pedreiro, elétrica…","William Ramos","Canvas"))),
        new Col("Espaço Compartilhado","time todo", List.of(
            new Nota("Plano de expansão 2027","Sede nova, duas lojas e tráfego","William Ramos","Nota"),
            new Nota("Reunião de sócios · pauta","1. Caixa 2. Contratações 3. Sede","Lucas Lisboa","Nota"))),
        new Col("Projetos Empresa","por empresa", List.of(
            new Nota("Mapa de processos · Fiscal","Fechamento mensal, gargalos","William Ramos","Canvas"),
            new Nota("Contrato padrão Gestão de Lojas","","Lucas Lisboa","Nota")))
    );
    private record Ideia(String cod, String titulo, int etapa, int pct, String tag) {}
    private final List<Ideia> IDEIAS = List.of(
        new Ideia("IP-0004","Painel único de cobrança",0,20,"Cobrança"),
        new Ideia("IP-0003","Plano de expansão 2027",1,33,"Expansão"),
        new Ideia("IP-0002","Curso interno de atendimento",3,66,"Pessoas"),
        new Ideia("IP-0001","Fila única WhatsApp + Cúpula",5,100,"Processo"));

    private final BorderPane root = new BorderPane();

    public Node build() { mural(); return root; }

    private void mural() {
        VBox box = new VBox(6); box.setPadding(new Insets(18,20,30,20));
        HBox topo = UI.row(10, UI.h1("Vault"));
        Button estr = new Button("Visão estratégica"); estr.getStyleClass().add("btn-ghost"); estr.setOnAction(e->estrategica());
        Button nova = new Button("+ Nova nota"); nova.getStyleClass().add("btn-cta");
        topo.getChildren().addAll(UI.grow(), estr, nova);
        box.getChildren().addAll(topo, UI.sub("Notas, canvas e pastas — por área. Compartilhado é visível ao time; Ideias/Pessoais só você."));

        HBox mural = new HBox(12); mural.setPadding(new Insets(14,0,0,0));
        for (Col c : MURAL) { VBox col = coluna(c); HBox.setHgrow(col, Priority.ALWAYS); mural.getChildren().add(col); }
        box.getChildren().add(mural);
        root.setCenter(UI.scroll(box));
    }

    private VBox coluna(Col c) {
        VBox col = new VBox(9);
        col.setStyle("-fx-background-color:#EDF2F7; -fx-background-radius:12; -fx-padding:12;");
        Label t = new Label(c.area()); t.getStyleClass().add("card-tit");
        col.getChildren().addAll(t, new Label(c.modo()){{ getStyleClass().add("sub"); }});
        for (Nota n : c.notas()) col.getChildren().add(notaCard(n, c.area()));
        return col;
    }

    private Node notaCard(Nota n, String area) {
        VBox v = UI.card(); v.setStyle(v.getStyle()+"-fx-cursor:hand; -fx-padding:12;");
        Label tag = UI.chip(n.tipo(), n.tipo().equals("Canvas")?"roxo":"info");
        Label t = new Label(n.titulo()); t.getStyleClass().add("card-tit"); t.setWrapText(true);
        v.getChildren().addAll(UI.row(6, tag), t);
        if(!n.prev().isEmpty()){ Label p=new Label(n.prev()); p.getStyleClass().add("sub"); p.setWrapText(true); v.getChildren().add(p); }
        v.getChildren().add(UI.row(6, UI.avatar(n.autor(),"#254B74"), new Label(n.autor()){{ getStyleClass().add("sub"); }}));
        v.setOnMouseClicked(e -> { if(n.tipo().equals("Canvas")) canvas(n); else editor(n); });
        return v;
    }

    private void editor(Nota n) {
        BorderPane p = new BorderPane();
        HBox tb = new HBox(4); tb.setPadding(new Insets(10,14,10,14)); tb.setStyle("-fx-background-color:#EDF2F7;");
        Button voltar = new Button("‹ Vault"); voltar.getStyleClass().add("btn-ghost"); voltar.setOnAction(a->mural());
        tb.getChildren().add(voltar);
        for (String b : new String[]{"B","I","U","H","≡","🔗","1.","⚑","♪","▶","▧","⊕"}) { Button x=new Button(b); x.getStyleClass().add("icon-btn"); tb.getChildren().add(x); }
        VBox body = new VBox(10); body.setPadding(new Insets(16,20,20,20));
        TextField tit = new TextField(n.titulo()); tit.setStyle("-fx-font-size:22px;-fx-font-family:'Archivo';-fx-font-weight:800;-fx-background-color:transparent;-fx-border-width:0;");
        TextArea ta = new TextArea(n.prev().isEmpty()?"":n.prev()+"\n\n1. …\n2. …"); ta.setWrapText(true); VBox.setVgrow(ta, Priority.ALWAYS);
        body.getChildren().addAll(tit, ta);
        p.setTop(tb); p.setCenter(body);
        root.setCenter(p);
    }

    private void canvas(Nota n) {
        BorderPane p = new BorderPane();
        HBox tb = new HBox(4); tb.setPadding(new Insets(10,14,10,14)); tb.setStyle("-fx-background-color:#EDF2F7;");
        Button voltar = new Button("‹ Vault"); voltar.getStyleClass().add("btn-ghost"); voltar.setOnAction(a->mural());
        tb.getChildren().add(voltar);
        for (String b : new String[]{"➕ Cartão","▦ Nota","⧉ Página","▢ Grupo","◐ Cor"}) { Button x=new Button(b); x.getStyleClass().add("chat-tool"); tb.getChildren().add(x); }
        Pane board = new Pane(); board.setStyle("-fx-background-color:#F6F8FA;");
        for (int i=0;i<3;i++){ VBox card=UI.card(); card.getChildren().add(new Label("Cartão "+(i+1))); card.setLayoutX(40+i*180); card.setLayoutY(40+i*90); card.setPrefWidth(150); board.getChildren().add(card); }
        p.setTop(tb); p.setCenter(board);
        root.setCenter(p);
    }

    private void estrategica() {
        VBox box = new VBox(10); box.setPadding(new Insets(18,20,30,20));
        Button voltar = new Button("‹ Vault"); voltar.getStyleClass().add("btn-ghost"); voltar.setOnAction(a->mural());
        box.getChildren().add(UI.row(10, voltar, UI.h1("Visão estratégica")));
        box.getChildren().add(UI.sub("Ideias e projetos do Vault, por etapa (I → VI)."));
        String[] etapas = {"Nova ideia","Em avaliação","Aprovado","Em desenvolvimento","Testes","Concluído"};
        HBox cols = new HBox(12); cols.setPadding(new Insets(14,0,0,0));
        for (int i=0;i<etapas.length;i++) {
            VBox col = new VBox(9); col.setStyle("-fx-background-color:#EDF2F7;-fx-background-radius:12;-fx-padding:10;"); col.setPrefWidth(220);
            Label t=new Label((i+1)+". "+etapas[i]); t.getStyleClass().add("sub"); t.setStyle("-fx-font-weight:700;");
            col.getChildren().add(t);
            final int et=i;
            IDEIAS.stream().filter(x->x.etapa()==et).forEach(x->{ VBox c=UI.card();
                c.getChildren().addAll(new Label(x.cod()){{ getStyleClass().add("cod"); }}, new Label(x.titulo()){{ getStyleClass().add("card-tit"); setWrapText(true); }},
                    UI.row(6, UI.chip(x.tag(),"roxo"), UI.chip(x.pct()+"%","ok")));
                col.getChildren().add(c); });
            HBox.setHgrow(col, Priority.ALWAYS);
            cols.getChildren().add(col);
        }
        ScrollPane sp = new ScrollPane(cols); sp.setFitToHeight(true); sp.setStyle("-fx-background-color:transparent;");
        box.getChildren().add(sp);
        root.setCenter(UI.scroll(box));
    }
}
