# Banco matriz B&L — módulo Empresas + Clientes

Banco: Supabase `wfqcoocfastgsfgegpcm` (holding Blanco & Lisboa).
Migrations em `supabase/migrations/`. Todas as tabelas com RLS ligada negando por
padrão (sem política = ninguém lê pela API até definirmos identidade/login).
Campos de integração em cada tabela de negócio: `id_origem` + `sistema_origem`
(unicidade por par, para importar de outro banco sem duplicar), `criado_em`,
`atualizado_em`.

## Grupo
- `holding` — Blanco & Lisboa (dona do grupo).
- `companhia_grupo` — as 8 companhias (YOU, Realizze, 40%, BEEC, Gestão de Lojas,
  IRPF, Winny, IT.IA). Campos: slug, sigla, nome, setor, cor, ordem, situacao.

## Cliente (central da holding)
- `cliente` — pessoa (CPF único). Cadastro mestre único.
- `empresa` — CNPJ (razão social, regime, CNAE, porte, cod_conexa, cod_dominio…).
- `cliente_empresa` — liga pessoa ↔ empresa. Permite trocar/desvincular/reativar
  (papel, principal, ativo, vinculado_em, desvinculado_em).
- `vinculo_companhia` — liga empresa ↔ companhia do grupo (situacao ativo/inativo/
  bloqueado/pendente, codigo_cliente). Inativar aqui desliga a empresa de uma
  companhia sem apagar histórico.

## Ficha
- `contato` — telefone/email/whatsapp (da pessoa OU da empresa).
- `endereco` — endereços (da pessoa OU da empresa).
- `loja_canal` — lojas/marketplaces (Shopee, Mercado Livre, código GS).
- `acesso_externo` — logins externos: SÓ o usuário; nunca senha/token.
- `atributo_perfil` — campos flexíveis (chave/valor): ERP, outras contabilidades…

## CRM de Leads
- `lead` — situacao (em_tratativa/fechou/nao_fechou), modo, objetivo, origem,
  indicado_por, perda; liga a `companhia_grupo` e, quando converte, a `cliente`.
- `lead_cnpj` / `lead_item` — CNPJs que a pessoa já tem / lojas do lead.
- `lead_registro` (+ `lead_marco`, `lead_anexo`) — linha do tempo (reuniões,
  ligações, marcos do funil, anexos).
- `lead_tipo` / `lead_categoria` — fixos + personalizados.

## Matriz / API
- `outbox_evento` — fila de mudanças para publicar aos outros Javas via API.
  Estrutura pronta; o disparo (gatilhos/worker) entra quando definirmos os
  contratos de API.

## Pendências conhecidas
- Regras de RLS por consumidor (quem-vê-o-quê) — dependem do modelo de login.
- `rls_auto_enable()` (função pré-existente no banco, não criada aqui) é executável
  por anon/authenticated — revisar com William.
