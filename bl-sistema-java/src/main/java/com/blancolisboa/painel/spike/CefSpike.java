package com.blancolisboa.painel.spike;

import me.friwi.jcefmaven.CefAppBuilder;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CefSpike {
    public static void main(String[] args) throws Exception {
        CefAppBuilder builder = new CefAppBuilder();
        builder.setInstallDir(new File("target/jcef-bundle"));
        builder.getCefSettings().windowless_rendering_enabled = false;
        // Corrige retangulos brancos (glitch de composicao GPU no Chromium embutido): renderiza por software.
        builder.addJcefArgs("--disable-gpu", "--disable-gpu-compositing");
        builder.setProgressHandler((state, percent) ->
                System.out.println("[JCEF] " + state + " " + (percent < 0 ? "" : (int) percent + "%")));

        CefApp cefApp = builder.build();
        CefClient client = cefApp.createClient();

        String url = new File(System.getProperty("user.dir"),
                "src/main/resources/web/painel.html").toURI().toString();
        System.out.println("[JCEF] carregando: " + url);
        CefBrowser browser = client.createBrowser(url, false, false);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Blanco & Lisboa — Painel (Chromium/JCEF)");
            frame.getContentPane().add(browser.getUIComponent(), BorderLayout.CENTER);
            frame.setSize(1440, 900);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
