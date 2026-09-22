-- Módulo Integrações (já existe no banco). 3 tabelas: integracao / integracao_operacao / integracao_evento.
-- Pré-requisito: tabela empresa.

create table if not exists integracao (
    id                          uuid primary key,
    empresa_id                  uuid        not null references empresa (id),
    provedor                    text        not null,
    nome                        text        not null,
    ambiente                    text        not null default 'PRODUCAO'
                                check (ambiente in ('PRODUCAO', 'TESTE')),
    base_url                    text,
    credenciais_cifradas        text,          -- SEMPRE cifrado pelo app (Cofre)
    webhook_segredo             text        not null,
    ativa                       boolean     not null default false,
    tipo                        text        not null,   -- API_REST, WEBHOOK, ARQUIVO
    autenticacao                text        not null,   -- NENHUMA, TOKEN_FIXO, CERTIFICADO...
    tempo_limite_segundos       int         default 20,
    tentativas                  int         default 3,
    espera_entre_tentativas_ms  int         default 2000,
    verificar_assinatura        boolean     default false,
    ultima_checagem_em          timestamptz,
    ultima_checagem_ok          boolean,
    ultima_checagem_erro        text,
    observacao                  text,
    criada_em                   timestamptz not null default now(),
    criada_por                  text,
    atualizada_em               timestamptz not null default now(),
    constraint uq_integracao_nome unique (empresa_id, nome)
);
create index if not exists idx_integracao_empresa on integracao (empresa_id);

create table if not exists integracao_operacao (
    id            uuid primary key,
    integracao_id uuid        not null references integracao (id),
    nome          text        not null,
    verbo         text        not null default 'GET',
    caminho       text        not null,
    para_que      text,
    corpo_modelo  text,
    ativa         boolean     not null default true,
    criada_em     timestamptz not null default now()
);
create index if not exists idx_operacao_integracao on integracao_operacao (integracao_id);

create table if not exists integracao_evento (
    id            bigserial primary key,
    integracao_id uuid        not null references integracao (id) on delete cascade,
    empresa_id    uuid        not null references empresa (id),
    direcao       text        not null check (direcao in ('ENTRADA', 'SAIDA')),
    tipo          text        not null,
    referencia    text,
    carga         text,
    status        text        not null default 'RECEBIDO'
                  check (status in ('RECEBIDO', 'PROCESSADO', 'ERRO', 'IGNORADO')),
    erro          text,
    ocorrido_em   timestamptz not null default now(),
    processado_em timestamptz
);
create index if not exists idx_evento_integracao on integracao_evento (integracao_id, ocorrido_em desc);
create index if not exists idx_evento_pendente on integracao_evento (ocorrido_em) where status = 'RECEBIDO';
