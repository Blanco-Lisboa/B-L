create table cliente (
  id uuid primary key default gen_random_uuid(),
  cpf text not null unique,
  nome text not null,
  data_nascimento date,
  situacao text not null default 'ativo'
    check (situacao in ('ativo','inativo','prospecto')),
  ativo boolean not null default true,
  observacao text,
  id_origem text,
  sistema_origem text,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now()
);
create unique index uq_cliente_origem
  on cliente (sistema_origem, id_origem)
  where sistema_origem is not null and id_origem is not null;

create table empresa (
  id uuid primary key default gen_random_uuid(),
  cnpj text unique,
  razao_social text not null,
  nome_fantasia text,
  natureza_juridica text,
  atividade_principal text,
  porte text,
  segmento text,
  regime_tributario text,
  inscricao_estadual text,
  inscricao_municipal text,
  cod_conexa text,
  cod_dominio text,
  email_empresarial text,
  telefone text,
  inicio text,
  tipo_item text not null default 'cnpj'
    check (tipo_item in ('cnpj','loja','nenhum')),
  situacao text not null default 'ativo'
    check (situacao in ('ativo','inativo','a_abrir')),
  ativo boolean not null default true,
  id_origem text,
  sistema_origem text,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now()
);
create unique index uq_empresa_origem
  on empresa (sistema_origem, id_origem)
  where sistema_origem is not null and id_origem is not null;

create table cliente_empresa (
  id uuid primary key default gen_random_uuid(),
  cliente_id uuid not null references cliente(id),
  empresa_id uuid not null references empresa(id),
  papel text not null default 'titular'
    check (papel in ('titular','socio','responsavel')),
  principal boolean not null default false,
  ativo boolean not null default true,
  vinculado_em timestamptz not null default now(),
  desvinculado_em timestamptz,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now(),
  unique (cliente_id, empresa_id)
);
create index ix_cliente_empresa_empresa on cliente_empresa (empresa_id);

create table vinculo_companhia (
  id uuid primary key default gen_random_uuid(),
  empresa_id uuid not null references empresa(id),
  companhia_grupo_id uuid not null references companhia_grupo(id),
  situacao text not null default 'ativo'
    check (situacao in ('ativo','inativo','bloqueado','pendente')),
  codigo_cliente text,
  data_inicio date,
  data_fim date,
  ativo boolean not null default true,
  id_origem text,
  sistema_origem text,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now(),
  unique (empresa_id, companhia_grupo_id)
);
create index ix_vinculo_companhia on vinculo_companhia (companhia_grupo_id);
create unique index uq_vinculo_origem
  on vinculo_companhia (sistema_origem, id_origem)
  where sistema_origem is not null and id_origem is not null;

create trigger trg_cliente_upd before update on cliente
  for each row execute function set_atualizado_em();
create trigger trg_empresa_upd before update on empresa
  for each row execute function set_atualizado_em();
create trigger trg_cliente_empresa_upd before update on cliente_empresa
  for each row execute function set_atualizado_em();
create trigger trg_vinculo_companhia_upd before update on vinculo_companhia
  for each row execute function set_atualizado_em();
