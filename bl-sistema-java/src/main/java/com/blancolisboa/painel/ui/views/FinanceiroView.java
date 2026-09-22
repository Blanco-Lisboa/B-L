package com.blancolisboa.painel.ui.views;

import com.blancolisboa.painel.ui.UI;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;

/** Financeiro: KPIs + card por empresa → entrar → tabela de contas a receber. */
public class FinanceiroView {

    private record Emp(String nome, String cor, String recebido, String vencido, String emDia) {}
    private final List<Emp> EMPS = List.of(
        new Emp("YOU Contabilidade","#F04E23","R$ 529.900","R$ 86.400","92%"),
        new Emp("Realizze","#16A6A6","R$ 212.300","R$ 31.200","88%"),
        new Emp("40%","#1C1C1C","R$ 174.600","R$ 58.900","71%")
    );
    private record Titulo(String cliente, String venc, String valor, String status) {}
    private final List<Titulo> TITS = List.of(
        new Titulo("Suemo/Kane","10/09","R$ 350","recebido"),
        new Titulo("DONAI XVR","10/09","R$ 480","vencido"),
        new Titulo("Modas Jô Love","15/09","R$ 350","em aberto"),
        new Titulo("TikTok Shop","20/09","R$ 620","recebido")
    );

    private final BorderPane root = new BorderPane();

    public Node build() { root.setCenter(UI.scroll(lista())); return root; }

    private Node lista() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(22,24,40,24));
        box.getChildren().addAll(UI.h1("Financeiro"), UI.sub("Contas a receber/pagar do grupo (Conexa). Vencido a receber do grupo: R$ 2,13 mi."));
        TilePane kpis = new TilePane(14,14); kpis.setPrefColumns(4); kpis.setPadding(new Insets(16,0,0,0));
        kpis.getChildren().addAll(kpi("Recebido (mês)","R$ 916.800"), kpi("Vencido a receber","R$ 2,13 mi"), kpi("Títulos em aberto","421"), kpi("Em dia (média)","84%"));
        box.getChildren().add(kpis);
        box.getChildren().add(UI.secT("Por empresa · clique para entrar"));
        TilePane grid = new TilePane(14,14); grid.setPrefColumns(3);
        for (Emp e : EMPS) grid.getChildren().add(card(e));
        box.getChildren().add(grid);
        return box;
    }

    private Node detalhe(Emp e) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(22,24,40,24));
        Button voltar = new Button("‹ Voltar"); voltar.getStyleClass().add("btn-ghost"); voltar.setOnAction(a -> build());
        HBox top = UI.row(12, voltar, UI.avatar(e.nome(), e.cor()), UI.h1(e.nome()));
        box.getChildren().add(top);
        TilePane kpis = new TilePane(12,12); kpis.setPrefColumns(3); kpis.setPadding(new Insets(14,0,0,0));
        kpis.getChildren().addAll(kpi("Recebido (mês)", e.recebido()), kpi("Vencido", e.vencido()), kpi("Em dia", e.emDia()));
        box.getChildren().add(kpis);
        box.getChildren().add(UI.secT("Contas a receber"));
        VBox tbl = new VBox(0); tbl.setMaxWidth(760); tbl.getStyleClass().add("tbl");
        HBox head = new HBox(); head.getStyleClass().add("tbl-head");
        head.getChildren().addAll(col("CLIENTE",300), col("VENCIMENTO",140), col("VALOR",120), col("STATUS",140));
        tbl.getChildren().add(head);
        for (Titulo t : TITS) {
            HBox r = new HBox(); r.getStyleClass().add("tbl-row");
            Label c=new Label(t.cliente()); c.setPrefWidth(300);
            Label v=new Label(t.venc()); v.setPrefWidth(140); v.getStyleClass().add("sub");
            Label val=new Label(t.valor()); val.setPrefWidth(120);
            Label st=new Label(t.status()); st.setPrefWidth(140);
            st.setStyle("-fx-font-weight:600;-fx-text-fill:"+(t.status().equals("vencido")?"#D64545":t.status().equals("recebido")?"#2E9E5B":"#E0A62B")+";");
            r.getChildren().addAll(c,v,val,st); tbl.getChildren().add(r);
        }
        box.getChildren().add(tbl);
        root.setCenter(UI.scroll(box));
        return box;
    }

    private Node card(Emp e) {
        VBox c = UI.card(); c.setPrefWidth(280);
        c.setStyle("-fx-background-color:#fff; -fx-background-radius:12; -fx-border-color:#E1E6EB; -fx-border-radius:12; -fx-border-width:0 0 0 4; -fx-effect: dropshadow(gaussian, rgba(1,29,58,0.08),10,0,0,2); -fx-padding:16; -fx-cursor:hand;");
        HBox h = UI.row(11, UI.avatar(e.nome(), e.cor()), new Label(e.nome()));
        ((Label)h.getChildren().get(1)).getStyleClass().add("card-tit");
        TilePane tiles = new TilePane(8,8); tiles.setPrefColumns(3);
        tiles.getChildren().addAll(tile("Recebido", e.recebido(), null), tile("Vencido", e.vencido(), "#D64545"), tile("Em dia", e.emDia(), "#2E9E5B"));
        c.getChildren().addAll(h, tiles);
        c.setOnMouseClicked(a -> detalhe(e));
        return c;
    }
    private Label col(String t, double w){ Label l=new Label(t); l.setPrefWidth(w); l.getStyleClass().add("tbl-col"); return l; }
    private Node kpi(String lab, String num) { VBox c = UI.card(); c.setPrefWidth(230); Label l=new Label(lab.toUpperCase()); l.getStyleClass().add("kpi-lab"); Label n=new Label(num); n.getStyleClass().add("kpi-num"); c.getChildren().addAll(l,n); return c; }
    private Node tile(String k, String v, String cor) { VBox t = new VBox(3); t.setStyle("-fx-background-color:#EDF2F7; -fx-background-radius:8; -fx-padding:8 9;"); Label kl=new Label(k.toUpperCase()); kl.setStyle("-fx-font-size:9px;-fx-text-fill:#6B7684;-fx-font-weight:700;"); Label vl=new Label(v); vl.setStyle("-fx-font-family:'Archivo';-fx-font-weight:800;-fx-font-size:14px;"+(cor!=null?"-fx-text-fill:"+cor+";":"")); t.getChildren().addAll(kl,vl); return t; }
}
