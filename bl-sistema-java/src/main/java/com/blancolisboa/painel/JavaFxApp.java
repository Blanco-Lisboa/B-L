package com.blancolisboa.painel;

import com.blancolisboa.painel.ui.MainShell;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Application do JavaFX que inicializa o contexto Spring em init() e
 * pega os beans (telas/serviços) do container.
 */
public class JavaFxApp extends Application {

    private ConfigurableApplicationContext ctx;

    @Override
    public void init() {
        ctx = new SpringApplicationBuilder(PainelApplication.class)
                .headless(false)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage stage) {
        MainShell shell = ctx.getBean(MainShell.class);
        Scene scene = new Scene(shell.build(), 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/css/bl.css").toExternalForm());
        stage.setTitle("Blanco & Lisboa — Painel do CEO");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        ctx.close();
        Platform.exit();
    }
}
