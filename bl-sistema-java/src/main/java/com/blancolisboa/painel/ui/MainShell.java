package com.blancolisboa.painel.ui;

import com.blancolisboa.painel.service.GrupoService;
import com.blancolisboa.painel.ui.views.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/** Casca da aplicação: barra lateral navy + área de conteúdo que troca por módulo. */
@Component
public class MainShell {

    private final GrupoService grupo;
    private final Map<Modulo, Button> nav = new EnumMap<>(Modulo.class);
    private StackPane content;

    public MainShell(GrupoService grupo) { this.grupo = grupo; }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setLeft(sidebar());
        content = new StackPane();
        content.getStyleClass().add("content");
        root.setCenter(content);
        selecionar(Modulo.DASHBOARD);
        return root;
    }

    private VBox sidebar() {
        VBox side = new VBox();
        side.getStyleClass().add("sidebar");

        VBox brand = new VBox(2);
        Label b = new Label("BLANCO & LISBOA");
        b.getStyleClass().add("brand-title");
        Label s = new Label("HOLDING");
        s.getStyleClass().add("brand-sub");
        brand.getChildren().addAll(b, s);
        brand.setStyle("-fx-padding: 6 6 14 6;");
        side.getChildren().add(brand);

        for (Modulo m : Modulo.values()) {
            Button btn = new Button(m.label);
            btn.getStyleClass().add("nav-item");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setOnAction(e -> selecionar(m));
            nav.put(m, btn);
            side.getChildren().add(btn);
        }
        return side;
    }

    private void selecionar(Modulo m) {
        nav.forEach((mod, btn) -> {
            btn.getStyleClass().remove("nav-item-active");
            if (mod == m) btn.getStyleClass().add("nav-item-active");
        });
        content.getChildren().setAll(view(m));
    }

    private Node view(Modulo m) {
        return switch (m) {
            case DASHBOARD     -> new DashboardView().build();
            case WORKSPACE     -> new WorkspaceView().build();
            case TEAMS         -> new TeamsView().build();
            case CLIENTES      -> new ClientesView().build();
            case EMPRESAS      -> new EmpresasView(grupo).build();
            case FINANCEIRO    -> new FinanceiroView().build();
            case INTEGRACOES   -> new IntegracoesView().build();
            case MONITORAMENTO -> new MonitoramentoView().build();
            case CONFIGURACOES -> new ConfiguracoesView().build();
        };
    }
}
