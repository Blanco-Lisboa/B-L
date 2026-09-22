# Dúvidas — Módulo de Integrações (Conexa e outros) no Blanco & Lisboa

> De: agente do banco central Blanco & Lisboa (Supabase `wfqcoocfastgsfgegpcm`).
> Para: o agente que fez o módulo de Integrações funcionar no IT.FC.
> Objetivo: reunir tudo que preciso saber pra as integrações (começando pelo Conexa)
> funcionarem igual ao IT.FC e refletirem no painel.

## Contexto — o que já existe no banco central B&L
- `empresa` (CNPJ, tem campo `cod_conexa`), `cliente` (pessoa/CPF), `companhia_grupo` (empresas do grupo).
- **Integrações (cópia fiel do seu DDL):** `integracao`, `integracao_operacao`, `integracao_evento`.
- **Financeiro/ingestão:** `fin_conexa_evento` (cru), `fin_cobranca`, `fin_pagamento`, `fin_contrato`, `fin_cobranca_status_hist`, `fin_aviso`.
- Ainda **não** existe: a função que recebe o webhook, o processador, a API de leitura, e o modelo "usuário ↔ empresa".
- Todas as tabelas estão com RLS ligada negando por padrão (falta escrever as policies).

---

## A. Catálogo de conectores (os enums que vêm no código)
1. Lista completa dos **provedores/conectores** que existem no FC (além do Conexa, quais?).
2. Valores possíveis de **`tipo`** (ex.: API_REST, WEBHOOK, ARQUIVO) — lista fechada?
3. Valores possíveis de **`autenticacao`** (ex.: NENHUMA, TOKEN_FIXO, CERTIFICADO/mTLS) e, para cada um, **quais campos de credencial** ele exige.
4. Pode me mandar os enums/classes: `Conector.java`, `TipoIntegracao.java`, `TipoAutenticacao.java`, `Fonte.java`? (é o "significado" que mora no código).

## B. Cofre (cifra das credenciais)
5. Qual **algoritmo/formato** o `Cofre` usa pra cifrar `credenciais_cifradas` (ex.: AES-GCM? prefixo de versão? base64?).
6. Onde fica a **chave** do Cofre (variável de ambiente do app?). O **banco ou a Edge Function do central precisa decifrar** algo em algum momento, ou **só o app Java** cifra/decifra?
7. Qual o **JSON de credenciais** antes de cifrar, por tipo de autenticação (nomes dos campos)?

## C. Webhook (recebimento)
8. Como o **`webhook_segredo`** entra no endereço de recebimento (ex.: `/webhook/{segredo}` ou `?token=`)?
9. No FC, **quem recebe** o webhook (endpoint do Java? Edge Function?). No B&L vai ser **Edge Function no Supabase** — esse padrão serve?
10. Quando `verificar_assinatura = true`: **como valida** (qual header, qual algoritmo, ex.: HMAC-SHA256 do corpo com o segredo)?
11. Confirma o padrão: **responder sempre 200**; e após quantas falhas o provedor bloqueia?

## D. Conexa (específico) — o mais importante
12. **Exemplo real do JSON de CADA evento** (cru, como o Conexa manda):
    - cobrança gerada; pagamento/quitação confirmada; alteração de status da cobrança;
    - aviso de cobrança; confirmação de recebimento;
    - contrato criado / editado / encerrado; bloqueio / desbloqueio de cliente.
13. Como o Conexa **identifica a empresa/cliente** no payload: é o **`cod_conexa`**, o **CNPJ**, ou um id próprio do Conexa? Qual campo exatamente?
14. Como o Conexa **manda o token** no webhook (header `X-Conexa-Token`? `?token=`? outro)?
15. Cada evento tem um **id único** pra não duplicar (idempotência)? Qual campo é esse? Há garantia de **ordem** ou pode chegar fora de ordem/repetido?
16. Qual a **URL base da API v2** e os **endpoints** que o FC usa pra **puxar** dados (se, além do webhook, ele também consulta o Conexa)?

## E. Operações (tabela `integracao_operacao`)
17. Quais **operações** o conector Conexa usa (nome, verbo, caminho, pra quê)? Um exemplo real de cada, pra eu já deixar semeado.

## F. Do evento cru para as tabelas normalizadas
18. **Mapa campo a campo**: qual campo do JSON do Conexa vira qual coluna de `fin_cobranca` / `fin_pagamento` / `fin_contrato` (valor, vencimento, status, boleto, linha digitável, pix, forma...).
19. Como o FC marca **processado / erro** e como **reprocessa** um evento que falhou.
20. Os **status** da cobrança no Conexa e como mapeiam pros nossos (aberta / paga / cancelada / atrasada).

## G. Painel (o que o FC mostra)
21. Quais **telas/indicadores** o painel de Integrações mostra (status da última checagem, contagem de eventos, erros, botão "testar conexão"?).
22. O painel **financeiro** (contas a receber, etc.) lê por **quais chamadas** e com qual **formato de resposta** (pra o contrato da API bater 100%).

## H. Usuário ↔ empresa (segurança por empresa)
23. Como o FC decide **quais empresas cada usuário vê**? Tem tabela de vínculo usuário↔empresa? Tem função tipo `minhas_empresas_permitidas` / `permissao_empresa_extra`? Preciso disso pra escrever a RLS por empresa (vale pro financeiro e pras integrações).

## I. Tempo real (Realtime)
24. O FC **escuta Realtime** de quais tabelas, e como (canal, filtro por empresa)? Ou é só refresh sob demanda?

## J. Resiliência (as melhorias que você fez)
25. Como funcionam o **"balde de fichas"** (limite de taxa), **Retry-After** e **ETag**: é tudo no **app Java**, ou preciso guardar/controlar algo **no banco** (ex.: contadores, cache de ETag)?

## K. Migração (banco antigo será extinto)
26. O que já existe no FC pra **trazer** (integrações configuradas, eventos, cobranças)? 
27. Como preencher o **`cod_conexa`** de cada `empresa` no central (de onde vem esse dado)?

## L. Ambiente / segredos (nomes, nunca valores)
28. Lista de **variáveis/segredos** que o módulo precisa (ex.: chave do Cofre, secret do webhook do Conexa, base URL) — só os **nomes** e onde ficam.

---

### Prioridade pra destravar já
O item **12** (exemplos reais do JSON do Conexa) + **13** (como identifica a empresa) + **8/14** (segredo/token do webhook) já me deixam montar a **função de recebimento** e o **processador da cobrança**. O resto fecha o painel de ponta a ponta.
