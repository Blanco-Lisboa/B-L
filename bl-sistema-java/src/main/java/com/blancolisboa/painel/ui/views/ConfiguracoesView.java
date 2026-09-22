package com.blancolisboa.painel.ui.views;

import com.blancolisboa.painel.ui.UI;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

/** Configurações do CEO. Usuários/Empresas moram no módulo Empresas (consolidado). */
public class ConfiguracoesView {
    public Node build() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(22,24,40,24));
        box.getChildren().addAll(UI.h1("Configurações"), UI.sub("Preferências do painel, liberações de acesso e histórico de login."));
        box.getChildren().add(UI.secT("Áreas"));
        for (String[] a : List.of(
                new String[]{"Usuários & Empresas","Agora consolidado dentro do módulo Empresas (cards → entrar → Gerenciar acesso)."},
                new String[]{"Liberações de acesso","Aprovar/reprovar pedidos de criação de acesso feitos pelos gerentes."},
                new String[]{"Histórico de login","Últimos acessos por usuário."},
                new String[]{"Notificações","Catálogo por categoria e desligar por pessoa."})) {
            VBox c = UI.card(); c.setMaxWidth(700);
            Label t=new Label(a[0]); t.getStyleClass().add("card-tit");
            c.getChildren().addAll(t, UI.sub(a[1]));
            box.getChildren().add(c);
            VBox.setMargin(c, new Insets(0,0,10,0));
        }
        return UI.scroll(box);
    }
}
