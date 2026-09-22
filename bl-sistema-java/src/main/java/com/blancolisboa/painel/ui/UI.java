package com.blancolisboa.painel.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

/** Fábricas de UI reutilizáveis, na identidade B&L. */
public final class UI {
    private UI() {}

    public static Label h1(String t){ Label l=new Label(t); l.getStyleClass().add("h1"); return l; }
    public static Label sub(String t){ Label l=new Label(t); l.getStyleClass().add("sub"); return l; }
    public static Label secT(String t){ Label l=new Label(t.toUpperCase()); l.getStyleClass().add("sec-t"); l.setPadding(new Insets(18,0,10,0)); return l; }

    public static VBox card(){ VBox c=new VBox(8); c.getStyleClass().add("card"); return c; }

    public static Label chip(String t, String tipo){ Label l=new Label(t); l.getStyleClass().addAll("chip", tipo); return l; }
    public static Label pill(String t, boolean on){ Label l=new Label(t); l.getStyleClass().addAll("pill", on?"on":"off"); return l; }

    public static Label avatar(String nome, String cor){
        String[] p = nome.trim().split("\\s+");
        String s = p.length>1 ? (""+p[0].charAt(0)+p[p.length-1].charAt(0)) : nome.substring(0,Math.min(2,nome.length()));
        Label l=new Label(s.toUpperCase()); l.getStyleClass().add("avatar");
        if(cor!=null) l.setStyle("-fx-background-color:"+cor+";");
        return l;
    }

    public static Region grow(){ Region r=new Region(); HBox.setHgrow(r, Priority.ALWAYS); return r; }

    public static ScrollPane scroll(javafx.scene.Node content){
        ScrollPane sp=new ScrollPane(content); sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent;");
        return sp;
    }

    public static VBox page(String titulo, String subtitulo, javafx.scene.Node... corpo){
        VBox box=new VBox(6);
        box.getChildren().addAll(h1(titulo), sub(subtitulo));
        VBox wrap=new VBox(0, corpo); wrap.setPadding(new Insets(14,0,0,0));
        box.getChildren().add(wrap);
        return box;
    }

    public static HBox row(double gap, javafx.scene.Node... nodes){
        HBox h=new HBox(gap, nodes); h.setAlignment(Pos.CENTER_LEFT); return h;
    }
}
