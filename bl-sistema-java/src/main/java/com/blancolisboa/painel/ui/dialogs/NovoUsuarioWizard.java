package com.blancolisboa.painel.ui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Assistente "Novo usuário" em passos: empresa → nível → dados → setor → módulos → senha → revisão. Front. */
public class NovoUsuarioWizard {

    private static final List<String> NIVEIS = List.of("Colaborador","Assistente","Gerente","Diretor");
    private static final List<String> MODULOS = List.of("Fiscal","Legal","Pessoal","Financeiro","Societário","WhatsApp","Clientes","Aberturas");

    private final String empresaInicial;
    private final Consumer<String> onCriar; // recebe o nome criado
    private int step = 0;
    private final List<String> steps = new ArrayList<>();

    // dados
    private String empresa, nivel="Colaborador", nome="", ident="", dep="", senha="";

    public NovoUsuarioWizard(String empresa, Consumer<String> onCriar){ this.empresaInicial=empresa; this.empresa=empresa; this.onCriar=onCriar; }

    public void show() {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Novo usuário");
        dlg.getDialogPane().getStyleClass().add("root");
        try { dlg.getDialogPane().getStylesheets().add(getClass().getResource("/css/bl.css").toExternalForm()); } catch (Exception ignored) {}
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        BorderPane pane = new BorderPane();
        pane.setPrefSize(560, 460);
        dlg.getDialogPane().setContent(pane);
        render(pane, dlg);
        dlg.showAndWait();
    }

    private void calcSteps(){
        steps.clear();
        steps.add("Empresa"); steps.add("Nível"); steps.add("Dados");
        if (empresa!=null && empresa.startsWith("YOU") && !nivel.equals("Diretor")) steps.add("Setor");
        if (!nivel.equals("Diretor")) steps.add("Módulos");
        steps.add("Senha"); steps.add("Revisão");
    }

    private void render(BorderPane pane, Dialog<Void> dlg){
        calcSteps();
        if (step >= steps.size()) step = steps.size()-1;
        String cur = steps.get(step);

        HBox top = new HBox(6); top.setPadding(new Insets(4,4,12,4));
        for (int i=0;i<steps.size();i++){ Label s=new Label(steps.get(i)); s.getStyleClass().add("step"); if(i==step)s.getStyleClass().add("on"); else if(i<step)s.getStyleClass().add("done"); top.getChildren().add(s); }
        pane.setTop(top);

        VBox body = new VBox(11); body.setPadding(new Insets(4));
        switch (cur) {
            case "Empresa" -> {
                body.getChildren().add(lab("De qual empresa?"));
                FlowPane f=new FlowPane(8,8);
                for (String e: List.of("YOU Contabilidade","Realizze","40%","BEEC")) { ToggleButton b=new ToggleButton(e); b.getStyleClass().add("seg-b"); if(e.equals(empresa))b.getStyleClass().add("on"); b.setOnAction(ev->{empresa=e; render(pane,dlg);}); f.getChildren().add(b);} body.getChildren().add(f);
            }
            case "Nível" -> {
                body.getChildren().add(lab("Qual o nível?"));
                for (String n: NIVEIS){ RadioButton r=new RadioButton(n); r.setSelected(n.equals(nivel)); r.setOnAction(ev->{nivel=n; render(pane,dlg);}); body.getChildren().add(r);}
            }
            case "Dados" -> {
                boolean email = nivel.equals("Diretor");
                TextField n=new TextField(nome); n.setPromptText("Nome completo"); n.textProperty().addListener((o,a,b)->nome=b);
                TextField id=new TextField(ident); id.setPromptText(email?"E-mail (login)":"CPF (login)"); id.textProperty().addListener((o,a,b)->ident=b);
                body.getChildren().addAll(lab("Dados"), new Label("Nome"), n, new Label(email?"E-mail":"CPF"), id);
            }
            case "Setor" -> {
                body.getChildren().add(lab("Departamento(s)"));
                FlowPane f=new FlowPane(6,6);
                for (String d: List.of("Fiscal","Suporte","Financeiro","Legal","Societário","Pessoal")){ ToggleButton b=new ToggleButton(d); b.getStyleClass().add("seg-b"); if(d.equals(dep))b.getStyleClass().add("on"); b.setOnAction(ev->{dep=d;}); f.getChildren().add(b);} body.getChildren().add(f);
            }
            case "Módulos" -> {
                body.getChildren().addAll(lab("Módulos liberados"), new Label("Fixos: Dashboard, Tarefas"));
                FlowPane f=new FlowPane(10,6); for(String m:MODULOS) f.getChildren().add(new CheckBox(m)); body.getChildren().add(f);
            }
            case "Senha" -> {
                PasswordField p=new PasswordField(); p.setPromptText("Senha de acesso (mín. 6)"); p.textProperty().addListener((o,a,b)->senha=b);
                PasswordField c=new PasswordField(); c.setPromptText("Confirmar senha");
                body.getChildren().addAll(lab("Senha de acesso"), p, c);
            }
            case "Revisão" -> {
                VBox r=new VBox(6); r.setStyle("-fx-background-color:#EDF2F7;-fx-background-radius:10;-fx-padding:14;");
                r.getChildren().addAll(lab("Confira antes de criar"), kv("Nome", nome.isEmpty()?"—":nome), kv("Empresa", empresa), kv("Nível", nivel), kv("Login", ident.isEmpty()?"—":ident));
                if(!dep.isEmpty()) r.getChildren().add(kv("Departamento", dep));
                body.getChildren().add(r);
            }
        }
        pane.setCenter(body);

        HBox foot = new HBox(8); foot.setPadding(new Insets(12,4,4,4));
        Button voltar = new Button(step==0?"Cancelar":"Voltar"); voltar.getStyleClass().add("btn-ghost");
        voltar.setOnAction(ev->{ if(step==0) dlg.setResult(null); else { step--; render(pane,dlg);} if(step==0 && voltar.getText().equals("Cancelar")) dlg.close(); });
        Region gap=new Region(); HBox.setHgrow(gap, Priority.ALWAYS);
        boolean fim = cur.equals("Revisão");
        Button avancar = new Button(fim?"Criar usuário":"Avançar"); avancar.getStyleClass().add("btn-cta");
        avancar.setOnAction(ev->{ if(fim){ if(onCriar!=null) onCriar.accept(nome.isEmpty()?"Novo usuário":nome); dlg.close(); } else { step++; render(pane,dlg);} });
        foot.getChildren().addAll(voltar, gap, avancar);
        pane.setBottom(foot);
    }

    private Label lab(String t){ Label l=new Label(t.toUpperCase()); l.getStyleClass().add("sec-t"); return l; }
    private HBox kv(String k, String v){ Label a=new Label(k); a.getStyleClass().add("sub"); Label b=new Label(v); b.setStyle("-fx-font-weight:600;"); Region g=new Region(); HBox.setHgrow(g,Priority.ALWAYS); HBox h=new HBox(a,g,b); return h; }
}
