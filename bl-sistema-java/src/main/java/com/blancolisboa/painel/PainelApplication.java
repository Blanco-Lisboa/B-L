package com.blancolisboa.painel;

import me.friwi.jcefmaven.CefAppBuilder;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Sobe o Spring (que serve o front em /login.html e /painel.html) e abre a
 * janela do app com o Chromium embutido (JCEF) carregando a tela de login.
 * App Java desktop; nao usa Electron/Tauri.
 * Rodar: mvn spring-boot:run   (com SERVER_PORT definido)
 */
@SpringBootApplication
public class PainelApplication {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "false");
        ConfigurableApplicationContext ctx = SpringApplication.run(PainelApplication.class, args);

        String port = ctx.getEnvironment().getProperty("local.server.port",
                ctx.getEnvironment().getProperty("server.port", "8474"));
        String url = "http://localhost:" + port + "/login.html?boot=" + System.currentTimeMillis();

        // Pasta de perfil ESTAVEL (sempre a mesma, entre execucoes) - guarda
        // localStorage/cookies/sessao de verdade, pra "Salvar senha" do login
        // funcionar (achado 25/09/2026: a pasta carimbada com timestamp abaixo
        // criava um perfil novo a cada abertura, apagando o login lembrado
        // toda vez). O problema de tela mostrando HTML/JS antigos (23/09/2026)
        // continua resolvido pelos mesmos flags de desabilitar CACHE DE REDE
        // (--disable-http-cache, --disk-cache-size=1 etc.) logo abaixo - isso
        // e' um mecanismo DIFERENTE de localStorage/cookies, entao desligar um
        // nao exige apagar o outro.
        File pastaPerfil = new File(System.getProperty("java.io.tmpdir"), "bl-jcef-perfil");
        pastaPerfil.mkdirs();

        CefAppBuilder builder = new CefAppBuilder();
        builder.setInstallDir(new File("target/jcef-bundle"));
        builder.getCefSettings().windowless_rendering_enabled = false;
        builder.getCefSettings().cache_path = pastaPerfil.getAbsolutePath();
        builder.getCefSettings().persist_session_cookies = true;
        builder.addJcefArgs("--disable-gpu", "--disable-gpu-compositing", "--disable-http-cache",
                "--disk-cache-size=1", "--media-cache-size=1", "--disable-application-cache",
                "--aggressive-cache-discard", "--user-data-dir=" + pastaPerfil.getAbsolutePath());

        CefApp cefApp = builder.build();
        CefClient client = cefApp.createClient();
        CefBrowser browser = client.createBrowser(url, false, false);

        Image icone = carregarIconeApp();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Blanco & Lisboa — Painel do CEO");
            if (icone != null) frame.setIconImage(icone);
            frame.getContentPane().add(browser.getUIComponent(), BorderLayout.CENTER);
            frame.setSize(1440, 900);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });

        if (icone != null && Taskbar.isTaskbarSupported()) {
            try {
                Taskbar taskbar = Taskbar.getTaskbar();
                if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) taskbar.setIconImage(icone);
            } catch (UnsupportedOperationException ignored) {
                // Alguns Windows/JDK nao suportam Taskbar.setIconImage; o icone da janela (setIconImage) ja cobre a barra de tarefas.
            }
        }
    }

    private static Image carregarIconeApp() {
        try {
            URL recurso = PainelApplication.class.getClassLoader().getResource("icons/app-icon.png");
            if (recurso == null) return null;
            try (InputStream in = recurso.openStream()) {
                return javax.imageio.ImageIO.read(in);
            }
        } catch (Exception e) {
            System.out.println("[icone] nao deu pra carregar o icone do app: " + e.getMessage());
            return null;
        }
    }
}
