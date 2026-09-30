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

    /* Identidade fixa deste app — nenhum agente pode trocar o alvo de atualizacao. */
    public static final String VERSAO_ATUAL = "0.1.0";
    private static final String MANIFESTO = BASE + "versao.json";
    /* chave publica (anon) so para baixar o instalador pela nossa funcao */
    private static final String ANON =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndmcWNvb2NmYXN0Z3NmZ2VncGNtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODk0OTI2MTcsImV4cCI6MjEwNTA2ODYxN30.31bgQ0VWxDzck3G6BZJrQZbf8OAv4AOKQZPfjaroPmk";

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

    /* Troca do proprio programa: le versao.json; se ha versao nova que exige instalador,
       baixa o instalador, confere o SHA-256, dispara e encerra este app para ele substituir. */
    public static boolean trocarProgramaSePreciso() {
        try {
            HttpClient c = HttpClient.newBuilder().connectTimeout(ESPERA)
                    .followRedirects(HttpClient.Redirect.NORMAL).build();
            HttpRequest p = HttpRequest.newBuilder()
                    .uri(URI.create(MANIFESTO + "?t=" + System.currentTimeMillis()))
                    .timeout(ESPERA).GET().build();
            HttpResponse<String> r = c.send(p, HttpResponse.BodyHandlers.ofString());
            if (r.statusCode() != 200 || r.body() == null) return false;

            String j = r.body();
            String versao = campo(j, "versao");
            boolean precisa = "true".equalsIgnoreCase(campo(j, "precisa_instalador"));
            String url = campo(j, "url_instalador");
            String sha = campo(j, "sha256_instalador");
            if (!precisa || versao == null || url == null) return false;
            if (compararVersao(versao, VERSAO_ATUAL) <= 0) return false;

            HttpResponse<byte[]> ri = c.send(HttpRequest.newBuilder()
                    .uri(URI.create(url + (url.contains("?") ? "&" : "?") + "t=" + System.currentTimeMillis()))
                    .timeout(Duration.ofMinutes(5))
                    .header("apikey", ANON).header("Authorization", "Bearer " + ANON)
                    .GET().build(),
                    HttpResponse.BodyHandlers.ofByteArray());
            if (ri.statusCode() != 200 || ri.body() == null || ri.body().length < 100000) return false;
            if (sha != null && !sha.isBlank() && !resumo(ri.body()).equalsIgnoreCase(sha.trim())) {
                System.out.println("[atualizacao] instalador com assinatura diferente, ignorado");
                return false;
            }

            String nome = url.toLowerCase().endsWith(".msi") ? "BL-CEO-setup.msi" : "BL-CEO-setup.exe";
            Path destino = pastaLocal().getParent().resolve(nome);
            Files.write(destino, ri.body());

            ProcessBuilder pb;
            if (nome.endsWith(".msi")) {
                pb = new ProcessBuilder("msiexec", "/i", destino.toString(), "/qb");
            } else {
                pb = new ProcessBuilder(destino.toString(), "/S");
            }
            pb.start();
            System.out.println("[atualizacao] instalador novo disparado (" + versao + "), encerrando app");
            Runtime.getRuntime().exit(0);
            return true;
        } catch (Exception e) {
            System.out.println("[atualizacao] troca de programa nao feita: " + e.getMessage());
            return false;
        }
    }

    /* Le um valor simples de string/booleano do JSON, sem dependencia externa. */
    private static String campo(String json, String chave) {
        try {
            int i = json.indexOf("\"" + chave + "\"");
            if (i < 0) return null;
            int dp = json.indexOf(':', i);
            if (dp < 0) return null;
            int k = dp + 1;
            while (k < json.length() && Character.isWhitespace(json.charAt(k))) k++;
            if (k >= json.length()) return null;
            if (json.charAt(k) == '"') {
                int fim = json.indexOf('"', k + 1);
                return fim < 0 ? null : json.substring(k + 1, fim);
            }
            int fim = k;
            while (fim < json.length() && ",}\n\r ".indexOf(json.charAt(fim)) < 0) fim++;
            return json.substring(k, fim).trim();
        } catch (Exception e) {
            return null;
        }
    }

    /* Compara "0.2.0" vs "0.1.0": >0 se a primeira e maior. */
    private static int compararVersao(String a, String b) {
        String[] pa = a.trim().split("\\."), pb = b.trim().split("\\.");
        int n = Math.max(pa.length, pb.length);
        for (int i = 0; i < n; i++) {
            int va = i < pa.length ? parseInt(pa[i]) : 0;
            int vb = i < pb.length ? parseInt(pb[i]) : 0;
            if (va != vb) return Integer.compare(va, vb);
        }
        return 0;
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s.replaceAll("[^0-9]", "")); } catch (Exception e) { return 0; }
    }
}
