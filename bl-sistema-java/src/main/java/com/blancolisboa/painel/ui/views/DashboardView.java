package com.blancolisboa.painel.ui.views;

import com.blancolisboa.painel.ui.UI;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/** Visão do grupo: KPIs macro + gráficos (faturamento por mês, mix por empresa). */
public class DashboardView {

    public Node build() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(22,24,40,24));
        box.getChildren().addAll(UI.h1("Visão do grupo"), UI.sub("Indicadores macro da holding Blanco & Lisboa · dados de exemplo"));

        TilePane kpis = new TilePane(14, 14);
        kpis.setPadding(new Insets(20, 0, 0, 0)); kpis.setPrefColumns(4);
        kpis.getChildren().addAll(
            kpi("Faturamento (mês)", "R$ 916.800", "YOU + Realizze + 40%"),
            kpi("Vencido a receber", "R$ 2,13 mi", "2.808 títulos"),
            kpi("Clientes ativos", "1.176", "495 pendentes de revisão"),
            kpi("Usuários online", "15", "de 33 sessões ativas"),
            kpi("Demandas travadas", "79", "há mais de 3 dias"),
            kpi("Atendimento parado", "2.169", "conversas paradas +2h"),
            kpi("Chamados abertos", "358", "97 de alta prioridade"),
            kpi("Erros do sistema", "45", "12 nas últimas 24h"));
        box.getChildren().add(kpis);

        box.getChildren().add(UI.secT("Faturamento & mix"));
        HBox charts = new HBox(14);
        charts.getChildren().addAll(barras(), pizza());
        box.getChildren().add(charts);
        return UI.scroll(box);
    }

    private Node barras() {
        CategoryAxis x = new CategoryAxis(); NumberAxis y = new NumberAxis();
        BarChart<String,Number> bc = new BarChart<>(x, y);
        bc.setTitle("Faturamento do grupo (R$ mil)"); bc.setLegendVisible(false); bc.setPrefSize(520, 300);
        XYChart.Series<String,Number> s = new XYChart.Series<>();
        String[] meses = {"Abr","Mai","Jun","Jul","Ago","Set"};
        int[] vals = {742, 768, 803, 861, 889, 917};
        for (int i=0;i<meses.length;i++) s.getData().add(new XYChart.Data<>(meses[i], vals[i]));
        bc.getData().add(s);
        VBox c = UI.card(); HBox.setHgrow(c, Priority.ALWAYS); c.getChildren().add(bc); return c;
    }
    private Node pizza() {
        PieChart pc = new PieChart(FXCollections.observableArrayList(
            new PieChart.Data("YOU", 530), new PieChart.Data("Realizze", 212), new PieChart.Data("40%", 175)));
        pc.setTitle("Por empresa"); pc.setPrefSize(360, 300); pc.setLabelsVisible(true);
        VBox c = UI.card(); c.getChildren().add(pc); return c;
    }

    private Node kpi(String lab, String num, String hint) {
        VBox c = UI.card(); c.setPrefWidth(240);
        Label l = new Label(lab.toUpperCase()); l.getStyleClass().add("kpi-lab");
        Label n = new Label(num); n.getStyleClass().add("kpi-num");
        Label h = new Label(hint); h.getStyleClass().add("sub");
        c.getChildren().addAll(l, n, h);
        return c;
    }
}
