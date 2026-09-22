package com.blancolisboa.painel.ui.views;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** Tela base dos módulos ainda não implementados neste stack (como na prévia). */
public class PlaceholderView {

    private final String titulo;
    public PlaceholderView(String titulo) { this.titulo = titulo; }

    public Node build() {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        Label h = new Label(titulo);
        h.getStyleClass().add("h1");
        Label s = new Label("Módulo em construção neste projeto Java — já existe na prévia HTML.");
        s.getStyleClass().add("sub");
        box.getChildren().addAll(h, s);
        return box;
    }
}
