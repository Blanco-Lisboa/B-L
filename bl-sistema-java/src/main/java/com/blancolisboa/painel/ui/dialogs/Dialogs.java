package com.blancolisboa.painel.ui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

/** Diálogos das ferramentas do chat: Pedido, Aviso e Reunião. Front. */
public final class Dialogs {
    private Dialogs() {}

    private static Dialog<ButtonType> base(String titulo) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle(titulo);
        d.getDialogPane().getStyleClass().add("root");
        try { d.getDialogPane().getStylesheets().add(Dialogs.class.getResource("/css/bl.css").toExternalForm()); } catch (Exception ignored) {}
        d.getDialogPane().getButtonTypes().addAll(new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE), ButtonType.CANCEL);
        return d;
    }
    private static VBox fld(String label, javafx.scene.Node c) {
        Label l = new Label(label.toUpperCase()); l.getStyleClass().add("sec-t");
        VBox v = new VBox(4, l, c); if (c instanceof Region r) r.setMaxWidth(Double.MAX_VALUE);
        return v;
    }

    public static void pedido(Consumer<String> onOk) {
        Dialog<ButtonType> d = base("◫ Novo pedido");
        TextField tit = new TextField(); tit.setPromptText("O que precisa ser feito");
        ComboBox<String> dono = new ComboBox<>(); dono.getItems().addAll("Lucas Lisboa","William Ramos","Paulo H.","Denise E.","Carla S."); dono.setValue("Paulo H.");
        TextField prazo = new TextField(); prazo.setPromptText("ex.: hoje 12:00");
        ComboBox<String> tipo = new ComboBox<>(); tipo.getItems().addAll("tarefa","decisão","assumir atendimento"); tipo.setValue("tarefa");
        CheckBox cup = new CheckBox("Já criar como tarefa/demanda na Cúpula");
        VBox box = new VBox(11, fld("O que precisa ser feito", tit), fld("Dono", dono), fld("Prazo", prazo), fld("Tipo", tipo), cup);
        box.setPadding(new Insets(4)); box.setPrefWidth(460);
        d.getDialogPane().setContent(box);
        d.showAndWait().ifPresent(bt -> { if (bt.getButtonData()==ButtonBar.ButtonData.OK_DONE && onOk!=null)
            onOk.accept("Pedido criado: "+tit.getText()+" (dono "+dono.getValue()+")"+(cup.isSelected()?" · enviado à Cúpula":"")); });
    }

    public static void aviso(Consumer<String> onOk) {
        Dialog<ButtonType> d = base("◈ Novo aviso");
        ToggleGroup g = new ToggleGroup();
        HBox niveis = new HBox(6);
        for (String n : List.of("Informativo","Exige Visto","Urgente do CEO")) {
            ToggleButton b = new ToggleButton(n); b.getStyleClass().add("seg-b"); b.setToggleGroup(g); if(n.equals("Informativo"))b.setSelected(true); niveis.getChildren().add(b);
        }
        ComboBox<String> para = new ComboBox<>(); para.getItems().addAll("Todos","Minha empresa","Setor: Fiscal","Setor: Suporte","Quem cuida de um cliente"); para.setValue("Todos");
        TextField tit = new TextField(); tit.setPromptText("Ex.: Manutenção do ERP hoje 22h");
        TextArea txt = new TextArea(); txt.setPrefRowCount(3);
        VBox box = new VBox(11, fld("Nível", niveis), fld("Para", para), fld("Título", tit), fld("Mensagem", txt));
        box.setPadding(new Insets(4)); box.setPrefWidth(460);
        d.getDialogPane().setContent(box);
        d.showAndWait().ifPresent(bt -> { if (bt.getButtonData()==ButtonBar.ButtonData.OK_DONE && onOk!=null) {
            ToggleButton sel=(ToggleButton)g.getSelectedToggle(); onOk.accept("Aviso enviado ("+(sel!=null?sel.getText():"Informativo")+"): "+tit.getText()); } });
    }

    public static void reuniao(Consumer<String> onOk) {
        Dialog<ButtonType> d = base("◷ Marcar reunião");
        TextField tit = new TextField(); tit.setPromptText("Assunto");
        TextField quando = new TextField(); quando.setPromptText("Hoje 16:00");
        TextField dur = new TextField(); dur.setPromptText("30 min");
        TextArea pauta = new TextArea(); pauta.setPrefRowCount(2); pauta.setPromptText("Pauta (pode nascer de pedidos abertos)");
        TextField part = new TextField(); part.setPromptText("Carla S., João V., Lucas Lisboa");
        VBox box = new VBox(11, fld("Assunto", tit), fld("Quando", quando), fld("Duração", dur), fld("Pauta", pauta), fld("Participantes", part));
        box.setPadding(new Insets(4)); box.setPrefWidth(460);
        d.getDialogPane().setContent(box);
        d.showAndWait().ifPresent(bt -> { if (bt.getButtonData()==ButtonBar.ButtonData.OK_DONE && onOk!=null)
            onOk.accept("Reunião agendada: "+tit.getText()+" · "+quando.getText()); });
    }
}
