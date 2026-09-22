package com.blancolisboa.painel.ui.dialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Painel único "Gerenciar acesso" (consolida identificação, nível, empresas,
 * permissões entre empresas, departamento & módulos e ações). Front.
 */
public class GerenciarAcessoDialog {

    private static final List<String> NIVEIS = List.of("Colaborador","Assistente","Gerente","Diretor","CEO");
    private static final List<String> MODULOS = List.of("Fiscal","Legal","Pessoal","Financeiro","Societário","WhatsApp","Clientes","Aberturas","Novos clientes");
    private static final List<String> EMPRESAS = List.of("YOU Contabilidade","Realizze","40%","BEEC","Gestão de Lojas","IRPF","Winny","IT.IA");

    private final String nome, nivel;
    public GerenciarAcessoDialog(String nome, String nivel){ this.nome=nome; this.nivel=nivel; }

    public void show() {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Gerenciar acesso");
        dlg.getDialogPane().getStyleClass().add("root");
        try { dlg.getDialogPane().getStylesheets().add(getClass().getResource("/css/bl.css").toExternalForm()); } catch (Exception ignored) {}
        dlg.setHeaderText(null);

        VBox box = new VBox(0);
        box.setPadding(new Insets(4,4,4,4));
        box.setPrefWidth(560);

        // Identificação
        box.getChildren().add(sec("Identificação", ident()));
        boolean ehCeo = nivel.equalsIgnoreCase("ceo");
        boolean ehDir = nivel.equalsIgnoreCase("diretor");
        // Empresas vinculadas
        box.getChildren().add(sec("Empresas vinculadas", ehCeo ? new Label("★ Todas (CEO)") : empresasVinc()));
        // Permissões entre empresas
        if (!ehCeo) box.getChildren().add(sec("Permissões entre empresas (falar/ser marcado)", permsEmpresas()));
        // Departamento & módulos
        box.getChildren().add(sec("Departamento & módulos", (ehCeo||ehDir) ? new Label(ehCeo?"CEO tem acesso total.":"Diretor acessa todos os módulos.") : deptModulos()));
        // Ações
        box.getChildren().add(sec("Ações", acoes(dlg)));

        ScrollPane sp = new ScrollPane(box); sp.setFitToWidth(true); sp.setPrefViewportHeight(520);
        sp.setStyle("-fx-background-color:transparent;");
        dlg.getDialogPane().setContent(sp);
        dlg.getDialogPane().getButtonTypes().addAll(new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE), ButtonType.CLOSE);
        dlg.showAndWait();
    }

    private VBox sec(String titulo, javafx.scene.Node corpo){
        VBox v = new VBox(9); v.setPadding(new Insets(14,0,14,0));
        v.setStyle("-fx-border-color:#EEF1F4; -fx-border-width:1 0 0 0;");
        Label t = new Label(titulo.toUpperCase()); t.getStyleClass().add("sec-t");
        v.getChildren().addAll(t, corpo);
        return v;
    }

    private javafx.scene.Node ident(){
        VBox v = new VBox(10);
        HBox g = new HBox(10);
        v.getChildren().add(g);
        TextField n = new TextField(nome); n.setPromptText("Nome"); HBox.setHgrow(n, Priority.ALWAYS);
        TextField id = new TextField(); id.setPromptText("CPF ou e-mail (login)"); HBox.setHgrow(id, Priority.ALWAYS);
        g.getChildren().addAll(n, id);
        ComboBox<String> nv = new ComboBox<>(); nv.getItems().addAll(NIVEIS); nv.setValue(cap(nivel)); nv.setMaxWidth(Double.MAX_VALUE);
        v.getChildren().addAll(new Label("Nível"), nv);
        return v;
    }
    private javafx.scene.Node empresasVinc(){
        FlowPane f = new FlowPane(8,8);
        Label chip = new Label("★ YOU Contabilidade  ⚙  ✕"); chip.setStyle("-fx-border-color:#E1E6EB;-fx-border-radius:100;-fx-padding:5 10;");
        ComboBox<String> add = new ComboBox<>(); add.getItems().addAll(EMPRESAS); add.setPromptText("Adicionar empresa…");
        Button vinc = new Button("Vincular"); vinc.getStyleClass().add("btn-ghost");
        f.getChildren().addAll(chip, add, vinc);
        return f;
    }
    private javafx.scene.Node permsEmpresas(){
        FlowPane f = new FlowPane(10,6);
        for (String e : EMPRESAS) if(!e.startsWith("YOU")) { CheckBox c = new CheckBox(e); f.getChildren().add(c); }
        return f;
    }
    private javafx.scene.Node deptModulos(){
        VBox v = new VBox(10);
        FlowPane deps = new FlowPane(6,6);
        for (String d : List.of("Fiscal","Suporte","Financeiro","Legal","Societário","Pessoal")) {
            ToggleButton t = new ToggleButton(d); t.getStyleClass().add("seg-b"); deps.getChildren().add(t);
        }
        FlowPane mods = new FlowPane(10,6);
        for (String m : MODULOS) mods.getChildren().add(new CheckBox(m));
        v.getChildren().addAll(new Label("Departamentos"), deps, new Label("Módulos"), mods);
        return v;
    }
    private javafx.scene.Node acoes(Dialog<?> dlg){
        HBox h = new HBox(8);
        Button senha = new Button("Trocar senha"); senha.getStyleClass().add("btn-ghost");
        Button inat = new Button("Inativar"); inat.getStyleClass().add("btn-ghost");
        Button excl = new Button("Excluir usuário"); excl.setStyle("-fx-background-color:#D64545;-fx-text-fill:white;-fx-background-radius:6;-fx-padding:8 14;");
        h.getChildren().addAll(senha, inat, excl);
        return h;
    }
    private static String cap(String s){ return s==null||s.isEmpty()?s: Character.toUpperCase(s.charAt(0))+s.substring(1); }
}
