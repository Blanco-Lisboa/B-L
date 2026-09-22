package com.blancolisboa.painel.ui.views;

import com.blancolisboa.painel.ui.UI;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;

/** Monitoramento: quem está online, sessões e dispositivos (dado real medido). */
public class MonitoramentoView {

    private record Pessoa(String nome, String setor, String st, String rota) {}
    private final List<Pessoa> ONLINE = List.of(
        new Pessoa("William Ramos","Direção","online","Dashboard"),
        new Pessoa("Paulo H.","Suporte","online","WhatsApp"),
        new Pessoa("Denise E.","Fiscal","online","Tarefas"),
        new Pessoa("Carla S.","Financeiro","online","Financeiro"),
        new Pessoa("Aline A.","Realizze","ausente","—")
    );

    public Node build() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(22,24,40,24));
        box.getChildren().addAll(UI.h1("Monitoramento"), UI.sub("Presença da equipe, sessões e dispositivos."));

        TilePane kpis = new TilePane(14,14); kpis.setPrefColumns(4); kpis.setPadding(new Insets(16,0,0,0));
        kpis.getChildren().addAll(kpi("Online agora","15"), kpi("Sessões ativas","33"), kpi("Dispositivos","72"), kpi("Logins hoje","137"));
        box.getChildren().add(kpis);

        box.getChildren().add(UI.secT("Quem está online"));
        VBox lista = new VBox(0); lista.setMaxWidth(720); lista.getStyleClass().add("tbl");
        for (Pessoa p : ONLINE) {
            HBox r = new HBox(); r.getStyleClass().add("tbl-row");
            HBox nm = UI.row(9, dot(p.st()), UI.avatar(p.nome(),"#254B74"), new Label(p.nome())); nm.setPrefWidth(300);
            Label st = new Label(p.setor()); st.setPrefWidth(160); st.getStyleClass().add("sub");
            Label rt = new Label(p.rota()); rt.setPrefWidth(160); rt.getStyleClass().add("sub");
            r.getChildren().addAll(nm, st, rt);
            lista.getChildren().add(r);
        }
        box.getChildren().add(lista);
        return UI.scroll(box);
    }
    private Node kpi(String lab, String num){ VBox c=UI.card(); c.setPrefWidth(220); Label l=new Label(lab.toUpperCase()); l.getStyleClass().add("kpi-lab"); Label n=new Label(num); n.getStyleClass().add("kpi-num"); c.getChildren().addAll(l,n); return c; }
    private Node dot(String st){ Label d=new Label("●"); d.setStyle("-fx-text-fill:"+(st.equals("online")?"#2E9E5B":"#96A0AC")+";"); return d; }
}
