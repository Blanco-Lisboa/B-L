# Painel Blanco & Lisboa — Java + JavaFX + CSS + Spring

Produto real (desktop) a partir da prévia HTML do painel do CEO. Stack: **Java 21 + JavaFX 21 + CSS (JavaFX) + Spring Boot 3**.

## Como rodar
Pré-requisitos: **JDK 21** e **Maven 3.9+**.

```bash
cd bl-sistema-java
mvn javafx:run
```
Abre a janela do painel (barra lateral navy/gold + módulos). A API Spring sobe junto em `http://localhost:8080` (ex.: `GET /api/empresas`).

> Roda **sem banco** por padrão — os dados vêm semeados de `GrupoService` (a mesma base da prévia).

## Módulos (todos portados, navegáveis)
- **Casca (shell)** + **Design System B&L** em `src/main/resources/css/bl.css` (navy #011D3A, gold #EDB449).
- **Dashboard**: KPIs macro + gráficos (barras de faturamento + pizza por empresa).
- **Workspace**: alternância **Cúpula** (Hoje/Enviadas/Recebidas, abrir demanda → tickets/ações) e **Vault** (mural por área → abrir nota=editor / canvas=quadro; Visão estratégica com pipeline de 6 etapas).
- **TEAM's**: chat interno "Time" (navegação · conversa · contexto); enviar mensagem; ferramentas **Pedido / Aviso / Reunião** como diálogos; seções Central de avisos, Pedidos e Reuniões.
- **Clientes**: busca + tabela → **ficha** (abas Informações/Contatos/Financeiro/Histórico).
- **Empresas**: cards (baixar app/Java) → entrar → departamentos + usuários → **Gerenciar acesso** (painel único) e **Novo usuário** (wizard de 7 passos).
- **Financeiro**: KPIs + card por empresa → entrar → tabela de contas a receber.
- **Integrações**: conexões por empresa → entrar → abas **Conexão / Operações / Eventos** + catálogo de conectores.
- **Monitoramento**: online agora, sessões, dispositivos.
- **Configurações**: áreas (usuários/empresas consolidados em Empresas, liberações, login, notificações).
- **Spring**: `GrupoService` (dados) + `GrupoRestController` (`/api/empresas`) + **camada JPA** (`persistence/`: entidades `empresas`, `usuarios_internos`, `setores`, `cupula_tarefas` + repositórios) ligável pelo perfil `db`.

> **Validação:** o projeto foi escrito e revisado com cuidado, mas **precisa de um `mvn javafx:run` com JDK 21 + Maven** para confirmar a compilação (não foi possível compilar no ambiente de origem). Se aparecer qualquer erro, me mande a mensagem que eu corrijo na hora.
> **JavaFX ≠ HTML:** fica muito parecido com a prévia, não idêntico ao pixel; interações finas (ex.: arrastar cartões no canvas, drag-and-drop) evoluem sob demanda.

## Estrutura
```
src/main/java/com/blancolisboa/painel/
  PainelApplication.java        # main (Spring Boot + launch JavaFX)
  JavaFxApp.java                # sobe o Spring em init() e monta a Scene
  ui/
    Modulo.java                 # enum dos módulos da sidebar
    MainShell.java              # sidebar + área de conteúdo (@Component)
    views/
      DashboardView.java
      EmpresasView.java         # cards -> entrar -> departamentos/usuários
      PlaceholderView.java
  service/GrupoService.java     # dados do grupo (semeados; trocar por JPA)
  web/GrupoRestController.java  # API REST
src/main/resources/
  css/bl.css                    # design system B&L (JavaFX CSS)
  application.properties        # roda sem banco
  application-db.properties     # perfil db (Supabase)
sql/integracoes.sql            # DDL do módulo Integrações (já pronto no banco)
```

## Ligar no banco real (Supabase Postgres)
1. Preencha a senha em `src/main/resources/application-db.properties`.
2. Rode com o perfil `db`:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=db
   ```
3. Crie os `@Entity`/repositórios JPA das tabelas (`empresas`, `usuarios_internos`, `setores`, `integracao`, `cupula_tarefas`…) e troque o corpo de `GrupoService` pelas consultas (RPCs: `empresas_listar`, `usuarios_internos_listar_com_nivel`, etc.).

## Próximos módulos a portar (da prévia)
Workspace/Cúpula, Vault, Clientes, Financeiro, Integrações, Monitoramento, war-room de tempo real. Cada um vira uma `View` em `ui/views/` + serviço/consulta Spring.
