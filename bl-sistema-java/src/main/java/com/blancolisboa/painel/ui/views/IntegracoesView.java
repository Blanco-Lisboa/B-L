package com.blancolisboa.painel.ui.views;

import com.blancolisboa.painel.ui.UI;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/** Integrações: conexões por empresa → entrar → abas Conexão / Operações / Eventos. */
public class IntegracoesView {

    private record Intg(String nome, String prov, String amb, String tipo, String aut, boolean ativa, boolean ok, String cor) {}
    private final List<Intg> INTG = List.of(
        new Intg("Conexa · YOU","Conexa","Produção","API REST","Token fixo",true,true,"#2F6FED"),
        new Intg("Itaú · cobrança YOU","Banco Itaú","Teste","API REST","Certificado (mTLS)",false,false,"#EC7000"),
        new Intg("WhatsApp · Realizze","WhatsApp Cloud API","Produção","API REST","Token fixo",true,true,"#25D366")
    );
    private final String[][] CONECTORES = {
        {"Conexa","#2F6FED"},{"Domínio","#5b3f96"},{"Itaú","#EC7000"},{"Banco do Brasil","#1C3F94"},
        {"Sicoob","#003641"},{"WhatsApp","#25D366"},{"SEFAZ","#0B7A3B"},{"Gov.br","#1351B4"}
    };
    private record Op(String verbo, String caminho, String nome, boolean ativa) {}
    private record Ev(String dir, String tipo, String status, String quando) {}

    private final BorderPane root = new BorderPane();

    public Node build() { root.setCenter(UI.scroll(lista())); return root; }

    private Node lista() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(22,24,40,24));
        box.getChildren().addAll(UI.h1("Integrações"), UI.sub("Conecte bancos, Conexa, WhatsApp, SEFAZ e afins — por empresa. Credenciais cifradas no Cofre."));
        box.getChildren().add(UI.secT("Integrações configuradas · clique para entrar"));
        TilePane grid = new TilePane(13,13); grid.setPrefColumns(3);
        for (Intg i : INTG) grid.getChildren().add(card(i));
        box.getChildren().add(grid);
        box.getChildren().add(UI.secT("Conectores disponíveis"));
        FlowPane conn = new FlowPane(10,10);
        for (String[] c : CONECTORES) {
            HBox chip = UI.row(9, UI.avatar(c[0], c[1]), new Label(c[0]));
            chip.setStyle("-fx-background-color:#fff;-fx-border-color:#E1E6EB;-fx-border-radius:10;-fx-background-radius:10;-fx-padding:9 12;-fx-cursor:hand;");
            conn.getChildren().add(chip);
        }
        box.getChildren().add(conn);
        return box;
    }

    private void detalhe(Intg it) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(22,24,40,24));
        Button voltar = new Button("‹ Voltar"); voltar.getStyleClass().add("btn-ghost"); voltar.setOnAction(a -> build());
        HBox top = UI.row(12, voltar, UI.avatar(it.nome(), it.cor()), UI.h1(it.nome()), UI.grow(), UI.pill(it.ativa()?"Ativa":"Inativa", it.ativa()));
        box.getChildren().add(top);

        TabPane tabs = new TabPane();
        tabs.getTabs().addAll(
            tab("Conexão", conexao(it)),
            tab("Operações", operacoes()),
            tab("Eventos", eventos()));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabs, Priority.ALWAYS);
        box.getChildren().add(tabs);
        root.setCenter(box);
    }
    private Tab tab(String t, Node c){ Tab tb=new Tab(t, c); return tb; }

    private Node conexao(Intg it) {
        VBox v = new VBox(11); v.setPadding(new Insets(16,4,4,4)); v.setMaxWidth(640);
        v.getChildren().addAll(
            fld("Nome", it.nome()), fld("Ambiente", it.amb()), fld("Tipo", it.tipo()),
            fld("URL base", "https://api.exemplo.com.br/v1"));
        VBox cofre = new VBox(8); cofre.setStyle("-fx-background-color:#EDF2F7;-fx-background-radius:8;-fx-padding:12;");
        Label ct=new Label("CREDENCIAIS — CIFRADAS PELO COFRE"); ct.getStyleClass().add("sec-t");
        cofre.getChildren().addAll(ct, fld("Método de autenticação", it.aut()), fld("Token / API Key", "••••••••••"));
        v.getChildren().add(cofre);
        Label seg=new Label("Segredo do webhook: whk_9f2c7a10bd44e6h1k3m8q0"); seg.setStyle("-fx-font-family:monospace;-fx-font-size:12px;");
        v.getChildren().add(seg);
        HBox acoes = new HBox(8, botao("Testar conexão", false), botao("Salvar", true));
        v.getChildren().add(acoes);
        return UI.scroll(v);
    }
    private Node operacoes() {
        VBox tbl = new VBox(0); tbl.setPadding(new Insets(16,4,4,4)); tbl.setMaxWidth(760); tbl.getStyleClass().add("tbl");
        HBox head=new HBox(); head.getStyleClass().add("tbl-head"); head.getChildren().addAll(col("VERBO",80), col("CAMINHO",240), col("NOME",240), col("ESTADO",120)); tbl.getChildren().add(head);
        for (Op o : List.of(new Op("GET","/clientes","Listar clientes",true), new Op("GET","/financeiro/receber","Contas a receber",true), new Op("POST","/financeiro/baixa","Baixar título",false))) {
            HBox r=new HBox(); r.getStyleClass().add("tbl-row");
            Label vb=new Label(o.verbo()); vb.setPrefWidth(80); vb.setStyle("-fx-font-family:monospace;-fx-font-weight:700;");
            Label cm=new Label(o.caminho()); cm.setPrefWidth(240); cm.setStyle("-fx-font-family:monospace;");
            Label nm=new Label(o.nome()); nm.setPrefWidth(240);
            Label st=new Label(o.ativa()?"ativa":"inativa"); st.setPrefWidth(120); st.getStyleClass().add("sub");
            r.getChildren().addAll(vb,cm,nm,st); tbl.getChildren().add(r);
        }
        return UI.scroll(tbl);
    }
    private Node eventos() {
        VBox tbl = new VBox(0); tbl.setPadding(new Insets(16,4,4,4)); tbl.setMaxWidth(760); tbl.getStyleClass().add("tbl");
        HBox head=new HBox(); head.getStyleClass().add("tbl-head"); head.getChildren().addAll(col("DIREÇÃO",120), col("TIPO",280), col("STATUS",140), col("QUANDO",140)); tbl.getChildren().add(head);
        for (Ev e : List.of(new Ev("↓ entrada","webhook.pagamento","PROCESSADO","20/09 16:40"), new Ev("↑ saída","sync.clientes","PROCESSADO","21/09 08:12"), new Ev("↓ entrada","webhook.pagamento","ERRO","20/09 11:02"))) {
            HBox r=new HBox(); r.getStyleClass().add("tbl-row");
            Label d=new Label(e.dir()); d.setPrefWidth(120); d.setStyle("-fx-font-weight:700;-fx-text-fill:"+(e.dir().startsWith("↓")?"#2E9E5B":"#2F6FED")+";");
            Label t=new Label(e.tipo()); t.setPrefWidth(280);
            Label st=new Label(e.status()); st.setPrefWidth(140); st.setStyle("-fx-font-weight:600;-fx-text-fill:"+(e.status().equals("ERRO")?"#D64545":"#2E9E5B")+";");
            Label q=new Label(e.quando()); q.setPrefWidth(140); q.getStyleClass().add("sub");
            r.getChildren().addAll(d,t,st,q); tbl.getChildren().add(r);
        }
        return UI.scroll(tbl);
    }

    private Node card(Intg i) {
        VBox c = UI.card(); c.setPrefWidth(300); c.setStyle(c.getStyle()+"-fx-cursor:hand;");
        HBox h = UI.row(11, UI.avatar(i.nome(), i.cor()), new VBox(2, tit(i.nome()), UI.sub(i.prov())), UI.grow(), dot(i));
        c.getChildren().add(h);
        c.getChildren().add(UI.row(6, UI.chip(i.amb(), i.amb().equals("Produção")?"info":"att"), UI.pill(i.ativa()?"Ativa":"Inativa", i.ativa())));
        c.setOnMouseClicked(a -> detalhe(i));
        return c;
    }
    private Label tit(String t){ Label l=new Label(t); l.getStyleClass().add("card-tit"); return l; }
    private Node dot(Intg i){ Label d=new Label("●"); d.setStyle("-fx-text-fill:"+(i.ativa()?(i.ok()?"#2E9E5B":"#D64545"):"#96A0AC")+";-fx-font-size:16px;"); return d; }
    private Label col(String t, double w){ Label l=new Label(t); l.setPrefWidth(w); l.getStyleClass().add("tbl-col"); return l; }
    private VBox fld(String k, String v){ Label l=new Label(k.toUpperCase()); l.getStyleClass().add("kpi-lab"); TextField t=new TextField(v); return new VBox(4,l,t); }
    private Button botao(String t, boolean prim){ Button b=new Button(t); b.getStyleClass().add(prim?"btn-cta":"btn-ghost"); return b; }
}
