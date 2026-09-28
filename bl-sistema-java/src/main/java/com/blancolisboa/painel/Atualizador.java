package com.blancolisboa.painel;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;

/* Atualizacao da tela sem instalador novo */
public final class Atualizador {

    private static final String BASE =
            "https://wfqcoocfastgsfgegpcm.supabase.co/storage/v1/object/public/app-bl/";
    private static final String[] ARQUIVOS = { "painel.html", "login.html", "nova-senha.html" };
    private static final Duration ESPERA = Duration.ofSeconds(12);

    private Atualizador() { }

    public static Path pastaLocal() {
        String base = System.getenv("LOCALAPPDATA");
        if (base == null || base.isBlank()) base = System.getProperty("user.home");
        Path p = Path.of(base, "BL CEO", "web");
        try { Files.createDirectories(p); } catch (Exception ignorado) { }
        return p;
    }

    public static void buscarNovidades() {
        Path pasta = pastaLocal();
        HttpClient cliente = HttpClient.newBuilder()
                .connectTimeout(ESPERA)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        for (String nome : ARQUIVOS) {
            try {
                HttpRequest pedido = HttpRequest.newBuilder()
                        .uri(URI.create(BASE + nome + "?t=" + System.currentTimeMillis()))
                        .timeout(ESPERA)
                        .header("Accept", "text/html")
                        .GET().build();

                HttpResponse<byte[]> r = cliente.send(pedido, HttpResponse.BodyHandlers.ofByteArray());
                if (r.statusCode() != 200 || r.body() == null || r.body().length < 2048) continue;

                Path destino = pasta.resolve(nome);
                if (Files.exists(destino) && resumo(Files.readAllBytes(destino)).equals(resumo(r.body()))) continue;

                Path temporario = pasta.resolve(nome + ".baixando");
                Files.write(temporario, r.body());
                Files.move(temporario, destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[atualizacao] " + nome + " atualizado (" + r.body().length + " bytes)");
            } catch (Exception e) {
                System.out.println("[atualizacao] " + nome + " ficou como estava: " + e.getMessage());
            }
        }
    }

    public static void prepararLocaisDoFront() {
        Path pasta = pastaLocal();
        if (System.getProperty("spring.web.resources.static-locations") != null) return;
        String uri = pasta.toUri().toString();
        System.setProperty("spring.web.resources.static-locations",
                uri + "," + "classpath:/static/,classpath:/web/");
    }

    public static void semearSeVazio() {
        Path pasta = pastaLocal();
        for (String nome : ARQUIVOS) {
            Path destino = pasta.resolve(nome);
            if (Files.exists(destino)) continue;
            try (var in = Atualizador.class.getClassLoader().getResourceAsStream("static/" + nome)) {
                if (in == null) continue;
                Files.write(destino, in.readAllBytes());
            } catch (Exception ignorado) { }
        }
    }

    private static String resumo(byte[] dados) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();
            for (byte b : md.digest(dados)) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return new String(dados, StandardCharsets.UTF_8).length() + "";
        }
    }

    public static File pastaComoArquivo() {
        return pastaLocal().toFile();
    }
}
