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
        String url = "http://localhost:" + port + "/login.html";

        CefAppBuilder builder = new CefAppBuilder();
        builder.setInstallDir(new File("target/jcef-bundle"));
        builder.getCefSettings().windowless_rendering_enabled = false;
        builder.addJcefArgs("--disable-gpu", "--disable-gpu-compositing");

        CefApp cefApp = builder.build();
        CefClient client = cefApp.createClient();
        CefBrowser browser = client.createBrowser(url, false, false);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Blanco & Lisboa — Painel do CEO");
            frame.getContentPane().add(browser.getUIComponent(), BorderLayout.CENTER);
            frame.setSize(1440, 900);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
