create table lead_tipo (
  id uuid primary key default gen_random_uuid(),
  nome text not null unique,
  is_fixo boolean not null default false,
  criado_em timestamptz not null default now()
);

create table lead_categoria (
  id uuid primary key default gen_random_uuid(),
  chave text not null unique,
  nome text not null,
  is_fixo boolean not null default false,
  criado_em timestamptz not null default now()
);

create table lead (
  id uuid primary key default gen_random_uuid(),
  companhia_grupo_id uuid references companhia_grupo(id),
  cliente_id uuid references cliente(id),
  situacao text not null default 'em_tratativa'
    check (situacao in ('em_tratativa','fechou','nao_fechou')),
  modo text not null default 'pessoa'
    check (modo in ('pessoa','empresa')),
  objetivo text
    check (objetivo in ('abrir_novo','trazer_existentes','trazer_e_abrir')),
  pessoa_nome text,
  pessoa_cpf text,
  pessoa_whatsapp text,
  pessoa_email text,
  origem text,
  canal_trafego text,
  indicado_por_nome text,
  indicado_por_cpf text,
  novo_cnpj_nome text,
  anotacao text,
  perda_motivo text,
  perda_etapa text,
  perda_em timestamptz,
  fechado_em timestamptz,
  id_origem text,
  sistema_origem text,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now()
);
create index ix_lead_situacao on lead (situacao);
create index ix_lead_companhia on lead (companhia_grupo_id);
create unique index uq_lead_origem
  on lead (sistema_origem, id_origem)
  where sistema_origem is not null and id_origem is not null;

create table lead_cnpj (
  id uuid primary key default gen_random_uuid(),
  lead_id uuid not null references lead(id) on delete cascade,
  cnpj text,
  razao text,
  criado_em timestamptz not null default now()
);
create index ix_lead_cnpj_lead on lead_cnpj (lead_id);

create table lead_item (
  id uuid primary key default gen_random_uuid(),
  lead_id uuid not null references lead(id) on delete cascade,
  doc text,
  loja text,
  marketplace text,
  codigo_gs text,
  criado_em timestamptz not null default now()
);
create index ix_lead_item_lead on lead_item (lead_id);

create table lead_registro (
  id uuid primary key default gen_random_uuid(),
  lead_id uuid not null references lead(id) on delete cascade,
  tipo text,
  titulo text,
  data_evento date,
  texto text,
  categoria text,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now()
);
create index ix_lead_registro_lead on lead_registro (lead_id);

create table lead_marco (
  id uuid primary key default gen_random_uuid(),
  lead_id uuid not null references lead(id) on delete cascade,
  chave text not null,
  atingido_em timestamptz not null default now(),
  unique (lead_id, chave)
);

create table lead_anexo (
  id uuid primary key default gen_random_uuid(),
  registro_id uuid not null references lead_registro(id) on delete cascade,
  tipo text,
  nome text,
  url text,
  criado_em timestamptz not null default now()
);
create index ix_lead_anexo_registro on lead_anexo (registro_id);

create trigger trg_lead_upd before update on lead
  for each row execute function set_atualizado_em();
create trigger trg_lead_registro_upd before update on lead_registro
  for each row execute function set_atualizado_em();

insert into lead_tipo (nome, is_fixo) values
  ('Reunião', true), ('Conversa pessoalmente', true), ('Ligação', true);
insert into lead_categoria (chave, nome, is_fixo) values
  ('ligacao', 'Ligação', true), ('whatsapp', 'WhatsApp', true), ('financeiro', 'Financeiro', true);
