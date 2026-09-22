package com.blancolisboa.painel.ui.views;

import com.blancolisboa.painel.ui.UI;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/** Clientes: busca + tabela → abrir ficha (informações, contatos, empresas, financeiro). */
public class ClientesView {

    private record Cli(String nome, String empresas, String status, String cor, String cnpj, String regime, String dono, String mun) {}
    private final List<Cli> CLIS = List.of(
        new Cli("João Victor Sabino","Suemo/Kane · +2","Ativo","#2E9E5B","44.155.000/0001-90","Simples","Larissa M. (Fiscal)","São Paulo/SP"),
        new Cli("Elaine Cristina Leitão","Ludy Screen","Em abertura","#E0A62B","—","—","João V. (Societário)","Guarulhos/SP"),
        new Cli("Anderson Rodrigues","TikTok Shop","Ativo","#2E9E5B","66.125.135/0001-90","Simples","Denise E. (Fiscal)","Uberlândia/MG"),
        new Cli("Modas Jô Love LTDA","Jô Love","2 em atraso","#D64545","51.220.114/0001-33","Simples","Carla S. (Financeiro)","São Paulo/SP"),
        new Cli("DONAI XVR","DONAI XVR","Ativo · regra 40%","#2E9E5B","33.900.221/0001-05","Simples","Carla S. (Financeiro)","São Paulo/SP")
    );

    private final BorderPane root = new BorderPane();

    public Node build() { root.setCenter(UI.scroll(lista())); return root; }

    private Node lista() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(22,24,40,24));
        box.getChildren().addAll(UI.h1("Clientes"), UI.sub("1.514 clientes do grupo. Busque por nome, CNPJ ou empresa."));
        TextField busca = new TextField(); busca.setPromptText("Buscar cliente…"); busca.getStyleClass().add("busca-field"); busca.setMaxWidth(360);
        HBox tools = new HBox(busca); tools.setPadding(new Insets(14,0,0,0)); box.getChildren().add(tools);

        VBox tbl = new VBox(0); tbl.setMaxWidth(860); tbl.setPadding(new Insets(14,0,0,0)); tbl.getStyleClass().add("tbl");
        HBox head = new HBox(); head.getStyleClass().add("tbl-head");
        head.getChildren().addAll(col("CLIENTE",320), col("EMPRESAS",260), col("STATUS",200));
        tbl.getChildren().add(head);
        for (Cli c : CLIS) tbl.getChildren().add(linha(c));
        box.getChildren().add(tbl);
        return box;
    }

    private Node linha(Cli c) {
        HBox r = new HBox(); r.getStyleClass().add("tbl-row"); r.setStyle("-fx-cursor:hand;");
        HBox nome = UI.row(9, UI.avatar(c.nome(), "#254B74"), new Label(c.nome())); nome.setPrefWidth(320);
        Label emp = new Label(c.empresas()); emp.setPrefWidth(260); emp.getStyleClass().add("sub");
        Label st = new Label("● "+c.status()); st.setPrefWidth(200); st.setStyle("-fx-text-fill:"+c.cor()+"; -fx-font-weight:600;");
        r.getChildren().addAll(nome, emp, st);
        r.setOnMouseClicked(e -> ficha(c));
        return r;
    }

    private void ficha(Cli c) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(22,24,40,24));
        Button voltar = new Button("‹ Voltar"); voltar.getStyleClass().add("btn-ghost"); voltar.setOnAction(a -> build());
        box.getChildren().add(UI.row(12, voltar, UI.avatar(c.nome(),"#254B74"), UI.h1(c.nome()), UI.grow(),
            new Label("● "+c.status()){{ setStyle("-fx-text-fill:"+c.cor()+";-fx-font-weight:700;"); }}));

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(new Tab("Informações", info(c)), new Tab("Contatos", simples("Telefones, e-mails e WhatsApp do cliente.")),
            new Tab("Financeiro", simples("Cobrado, pago e em aberto por empresa.")), new Tab("Histórico", simples("Linha do tempo: cliente desde, atendimentos, certificados.")));
        VBox.setVgrow(tabs, Priority.ALWAYS);
        box.getChildren().add(tabs);
        root.setCenter(box);
    }
    private Node info(Cli c) {
        VBox v = new VBox(10); v.setPadding(new Insets(16,4,4,4)); v.setMaxWidth(680);
        v.getChildren().addAll(kv("CNPJ", c.cnpj()), kv("Regime", c.regime()), kv("Município", c.mun()), kv("Dono da carteira", c.dono()), kv("Empresas", c.empresas()));
        HBox acoes = new HBox(8, botao("Abrir tarefa", true), botao("Registrar decisão do CEO", false), botao("Ver credencial (cofre)", false));
        v.getChildren().add(acoes);
        return UI.scroll(v);
    }
    private Node simples(String t){ VBox v=new VBox(new Label(t){{ getStyleClass().add("sub"); }}); v.setPadding(new Insets(18,4,4,4)); return v; }
    private HBox kv(String k, String val){ Label a=new Label(k); a.getStyleClass().add("kpi-lab"); a.setPrefWidth(160); Label b=new Label(val); HBox h=UI.row(10,a,b); h.setPadding(new Insets(6,0,6,0)); h.setStyle("-fx-border-color:#EEF1F4;-fx-border-width:1 0 0 0;"); return h; }
    private Label col(String t, double w){ Label l=new Label(t); l.setPrefWidth(w); l.getStyleClass().add("tbl-col"); return l; }
    private Button botao(String t, boolean prim){ Button b=new Button(t); b.getStyleClass().add(prim?"btn-cta":"btn-ghost"); return b; }
}
