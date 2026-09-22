package com.blancolisboa.painel.ui.views;

import com.blancolisboa.painel.ui.UI;
import com.blancolisboa.painel.ui.dialogs.Dialogs;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;

/** TEAM's — chat interno "Time": navegação · conversa · contexto, com ferramentas. */
public class TeamsView {

    private static final class Msg {
        String autor, setor, txt, hora; boolean sys;
        Msg(String a,String s,String t,String h){autor=a;setor=s;txt=t;hora=h;}
        Msg sys(){this.sys=true; return this;}
    }
    private final Map<String,List<Msg>> CANAIS = new LinkedHashMap<>();
    private final String[] cores = {"#2F6FED","#5b3f96","#2E9E5B","#B98A2E","#254B74"};
    private static final String EU = "William Ramos";

    private final BorderPane center = new BorderPane();
    private String atual = "# Fiscal";

    public TeamsView() {
        CANAIS.put("# Fiscal", new ArrayList<>(List.of(
            new Msg("Larissa M.","Fiscal","Terminei a apuração da Suemo/Kane. Deu R$ 1.240 de DAS.","08:12"),
            new Msg("Denise E.","Fiscal","Confiro sim, me dá 10 min.","08:20"),
            new Msg("Larissa M.","Fiscal","DONAI XVR — posso declarar 50% de novo? @Lucas","09:40"))));
        CANAIS.put("# Suporte", new ArrayList<>(List.of(
            new Msg("Aline A.","Realizze","O ERP de emissão da Modas Jô Love expirou.","10:02"),
            new Msg("Paulo H.","Suporte","Assumido. Vou renovar e te aviso.","10:05"))));
        CANAIS.put("# Financeiro", new ArrayList<>(List.of(
            new Msg("Carla S.","Financeiro","DONAI XVR pagou a mensalidade hoje.","09:10"))));
        CANAIS.put("# Legal", new ArrayList<>());
        CANAIS.put("Lucas Lisboa", new ArrayList<>(List.of(
            new Msg("Lucas Lisboa","Direção","Fecha a folha da DONAI XVR hoje.","07:31"),
            new Msg("William Ramos","Direção","Fechado. Já subi o pedido pro Fiscal.","07:44"))));
    }

    public Node build() {
        HBox tela = new HBox();
        tela.getChildren().addAll(nav(), center);
        HBox.setHgrow(center, Priority.ALWAYS);
        abrir(atual);
        return tela;
    }

    private Node nav() {
        VBox nav = new VBox(2);
        nav.setPrefWidth(230); nav.setMinWidth(230);
        nav.setStyle("-fx-background-color:#0A2A4C;");
        nav.setPadding(new Insets(12,10,12,10));
        nav.getChildren().add(navGrp("Ferramentas"));
        nav.getChildren().addAll(
            navItem("◈  Central de avisos", this::secAvisos),
            navItem("◫  Pedidos comigo", this::secPedidos),
            navItem("◷  Reuniões", this::secReunioes));
        nav.getChildren().add(navGrp("Setores"));
        for (String c : new String[]{"# Fiscal","# Suporte","# Financeiro","# Legal"}) nav.getChildren().add(navCanal(c));
        nav.getChildren().add(navGrp("Diretas"));
        nav.getChildren().add(navCanal("Lucas Lisboa"));
        return nav;
    }
    private Label navGrp(String t){ Label l=new Label(t.toUpperCase()); l.getStyleClass().add("chat-grp"); return l; }
    private Node navItem(String t, Runnable r){ Label l=new Label(t); l.getStyleClass().add("chat-nav"); l.setOnMouseClicked(e->r.run()); return l; }
    private Node navCanal(String c){ Label l=new Label(c); l.getStyleClass().add("chat-nav"); l.setOnMouseClicked(e->{atual=c; abrir(c);}); return l; }

    // ---- seções (ferramentas) ----
    private void secAvisos() {
        VBox box = new VBox(6); box.setPadding(new Insets(22,24,40,24));
        box.getChildren().addAll(UI.h1("Central de avisos"), UI.sub("Uma caixa só, por prioridade. Alertas, avisos do CEO, menções e do sistema."));
        Button novo = new Button("+ Novo aviso"); novo.getStyleClass().add("btn-cta"); novo.setOnAction(e->Dialogs.aviso(s->secAvisos()));
        HBox tb = new HBox(novo); tb.setPadding(new Insets(12,0,4,0)); box.getChildren().add(tb);
        String[][] AV = {
            {"urgente","Manutenção do ERP hoje 22h–23h","William Ramos · Todos · 11:20"},
            {"visto","Novo cliente: Modas Jô Love","João V. · Fiscal, Legal, Societário · 10:40"},
            {"info","Resumo diário: 14 certificados a vencer","Sistema · Realizze · 08:00"}};
        VBox lista = new VBox(10); lista.setMaxWidth(820);
        for (String[] a : AV) {
            VBox c = UI.card();
            c.setStyle(c.getStyle()+"-fx-border-width:0 0 0 4; -fx-border-color:"+(a[0].equals("urgente")?"#D64545":a[0].equals("visto")?"#E0A62B":"#2F6FED")+";");
            Label t=new Label(a[1]); t.getStyleClass().add("card-tit");
            c.getChildren().addAll(UI.row(8, t, UI.grow(), UI.chip(a[0].equals("urgente")?"Urgente do CEO":a[0].equals("visto")?"Exige Visto":"Informativo", a[0].equals("urgente")?"erro":a[0].equals("visto")?"att":"info")), UI.sub(a[2]));
            if(!a[0].equals("info")){ Button v=new Button("✓ Visto"); v.getStyleClass().add("btn"); c.getChildren().add(v); }
            lista.getChildren().add(c);
        }
        box.getChildren().add(lista);
        center.setCenter(UI.scroll(box)); center.setRight(null);
    }
    private void secPedidos() {
        VBox box = new VBox(6); box.setPadding(new Insets(22,24,40,24));
        box.getChildren().addAll(UI.h1("Pedidos comigo"), UI.sub("Mensagem com estado. Vira tarefa/demanda da Cúpula com 1 clique."));
        Button novo = new Button("+ Novo pedido"); novo.getStyleClass().add("btn-cta"); novo.setOnAction(e->Dialogs.pedido(s->secPedidos()));
        HBox tb = new HBox(novo); tb.setPadding(new Insets(12,0,4,0)); box.getChildren().add(tb);
        String[][] PE = {
            {"P-104","DONAI XVR — pode declarar 50%?","Lucas Lisboa","aguardando"},
            {"P-103","Renovar ERP de emissão — Modas Jô Love","Paulo H.","andamento"},
            {"P-101","Conferir faturamento Shopee x ML","Denise E.","aberto"}};
        VBox lista = new VBox(10); lista.setMaxWidth(820);
        for (String[] p : PE) {
            VBox c = UI.card();
            Label cod=new Label(p[0]); cod.getStyleClass().add("cod");
            Label t=new Label(p[1]); t.getStyleClass().add("card-tit");
            String cls = p[3].equals("aberto")?"att":p[3].equals("andamento")?"info":p[3].equals("aguardando")?"roxo":"ok";
            c.getChildren().addAll(UI.row(8, cod, UI.grow(), UI.chip(p[3], cls)), t, UI.sub("dono: "+p[2]));
            lista.getChildren().add(c);
        }
        box.getChildren().add(lista);
        center.setCenter(UI.scroll(box)); center.setRight(null);
    }
    private void secReunioes() {
        VBox box = new VBox(6); box.setPadding(new Insets(22,24,40,24));
        box.getChildren().addAll(UI.h1("Reuniões, chamada e vídeo"), UI.sub("Chamada de voz/vídeo dentro do ERP (WebRTC) e agenda com pauta."));
        Button novo = new Button("+ Marcar reunião"); novo.getStyleClass().add("btn-cta"); novo.setOnAction(e->Dialogs.reuniao(s->secReunioes()));
        HBox tb = new HBox(novo); tb.setPadding(new Insets(12,0,4,0)); box.getChildren().add(tb);
        String[][] RE = {
            {"Semanal Financeiro × Societário","Hoje 16:00 · 30 min","7 CNPJs aguardando decisão"},
            {"Alinhamento Fiscal","Amanhã 09:00 · 20 min · Sala do Fiscal","Fechamento 07/2026"}};
        VBox lista = new VBox(10); lista.setMaxWidth(820);
        for (String[] r : RE) {
            VBox c = UI.card();
            Label t=new Label(r[0]); t.getStyleClass().add("card-tit");
            Button entrar=new Button("Entrar"); entrar.getStyleClass().add("btn");
            c.getChildren().addAll(UI.row(10, UI.avatar("R","#011D3A"), t, UI.grow(), entrar), UI.sub(r[1]), UI.sub("Pauta: "+r[2]));
            lista.getChildren().add(c);
        }
        box.getChildren().add(lista);
        center.setCenter(UI.scroll(box)); center.setRight(null);
    }

    private void abrir(String canal) {
        atual = canal;
        VBox conv = new VBox();
        HBox cab = new HBox(10);
        cab.getStyleClass().add("chat-cab");
        cab.setAlignment(Pos.CENTER_LEFT);
        Label tit = new Label(canal); tit.getStyleClass().add("card-tit");
        Button chamar = new Button("✆"); chamar.getStyleClass().add("icon-btn");
        Button video = new Button("▣"); video.getStyleClass().add("icon-btn");
        cab.getChildren().addAll(tit, UI.grow(), chamar, video);

        VBox msgs = new VBox(8);
        msgs.setPadding(new Insets(16,18,16,18));
        int i=0;
        for (Msg m : CANAIS.getOrDefault(canal, List.of())) {
            if (m.sys) { Label s=new Label("• "+m.txt); s.getStyleClass().add("sub"); s.setStyle("-fx-text-fill:#B98A2E;-fx-padding:2 0 2 6;"); msgs.getChildren().add(s); continue; }
            HBox row = new HBox(10);
            row.getChildren().add(UI.avatar(m.autor, cores[i++ % cores.length]));
            VBox corpo = new VBox(1);
            HBox l1 = new HBox(8);
            Label a=new Label(m.autor); a.setStyle("-fx-font-weight:700;-fx-font-size:12.5px;");
            Label sub=new Label(m.setor); sub.getStyleClass().add("sub");
            Label h=new Label(m.hora); h.getStyleClass().add("sub");
            l1.getChildren().addAll(a,sub,UI.grow(),h);
            Label txt=new Label(m.txt); txt.setWrapText(true);
            corpo.getChildren().addAll(l1, txt);
            HBox.setHgrow(corpo, Priority.ALWAYS);
            row.getChildren().add(corpo);
            msgs.getChildren().add(row);
        }
        ScrollPane sp = UI.scroll(msgs); VBox.setVgrow(sp, Priority.ALWAYS);

        HBox tools = new HBox(6);
        tools.setPadding(new Insets(8,14,0,14));
        tools.getChildren().addAll(
            tool("◫ Pedido", () -> Dialogs.pedido(this::sysMsg)),
            tool("◈ Aviso",  () -> Dialogs.aviso(this::sysMsg)),
            tool("◷ Reunião",() -> Dialogs.reuniao(this::sysMsg)),
            tool("⊕ Anexo",  () -> sysMsg("Anexo enviado (vai pro cofre, ligado ao cliente).")),
            tool("♪ Áudio",  () -> sysMsg("Áudio gravado e transcrito automaticamente.")));

        HBox comp = new HBox(9);
        comp.setPadding(new Insets(8,14,12,14));
        TextField tf = new TextField(); tf.setPromptText("Mensagem para "+canal+"…"); HBox.setHgrow(tf, Priority.ALWAYS);
        Runnable enviar = () -> { String v=tf.getText()==null?"":tf.getText().trim(); if(v.isEmpty())return;
            CANAIS.computeIfAbsent(canal,k->new ArrayList<>()).add(new Msg(EU,"Direção",v,agora())); tf.clear(); abrir(canal); };
        tf.setOnAction(e -> enviar.run());
        Button env = new Button("➤"); env.getStyleClass().add("chat-enviar"); env.setOnAction(e -> enviar.run());
        comp.getChildren().addAll(tf, env);

        conv.getChildren().addAll(cab, sp, tools, comp);
        center.setCenter(conv);
        center.setRight(ctx());
    }
    private Button tool(String t, Runnable r){ Button b=new Button(t); b.getStyleClass().add("chat-tool"); b.setOnAction(e->r.run()); return b; }
    private void sysMsg(String s){ CANAIS.computeIfAbsent(atual,k->new ArrayList<>()).add(new Msg("Sistema","",s,agora()).sys()); abrir(atual); }
    private static String agora(){ java.time.LocalTime t=java.time.LocalTime.now(); return String.format("%02d:%02d", t.getHour(), t.getMinute()); }

    private Node ctx() {
        VBox ctx = new VBox(8);
        ctx.setPrefWidth(300); ctx.setMinWidth(280);
        ctx.setStyle("-fx-background-color:#FFFFFF; -fx-border-color:#E1E6EB; -fx-border-width:0 0 0 1;");
        ctx.setPadding(new Insets(16));
        ctx.getChildren().add(UI.secT("Cartão do cliente"));
        VBox card = UI.card();
        Label n=new Label("Suemo/Kane · C-0421"); n.getStyleClass().add("card-tit");
        card.getChildren().addAll(n, UI.sub("Simples · São Paulo/SP · dono: Larissa M."));
        ctx.getChildren().add(card);
        ctx.getChildren().add(UI.secT("Participantes"));
        for (String[] p : new String[][]{{"William Ramos","online"},{"Lucas Lisboa","em reunião"},{"Paulo H.","online"},{"Denise E.","online"}}) {
            HBox r = UI.row(8, UI.avatar(p[0],"#254B74"), new Label(p[0]));
            Label st=new Label(p[1]); st.getStyleClass().add("sub");
            r.getChildren().addAll(UI.grow(), st);
            ctx.getChildren().add(r);
        }
        return ctx;
    }
}
