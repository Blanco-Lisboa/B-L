create or replace function set_atualizado_em()
returns trigger language plpgsql as $$
begin
  new.atualizado_em = now();
  return new;
end;
$$;

create table holding (
  id uuid primary key default gen_random_uuid(),
  nome text not null,
  razao_social text,
  cnpj text unique,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now()
);

create table companhia_grupo (
  id uuid primary key default gen_random_uuid(),
  holding_id uuid not null references holding(id),
  slug text not null unique,
  sigla text not null,
  nome text not null,
  razao_social text,
  cnpj text unique,
  setor text,
  cor text,
  ordem int,
  situacao text not null default 'operante'
    check (situacao in ('operante','pre_lancamento','nova','sazonal','inativa')),
  ativo boolean not null default true,
  id_origem text,
  sistema_origem text,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now()
);

create unique index uq_companhia_origem
  on companhia_grupo (sistema_origem, id_origem)
  where sistema_origem is not null and id_origem is not null;

create trigger trg_holding_upd before update on holding
  for each row execute function set_atualizado_em();
create trigger trg_companhia_upd before update on companhia_grupo
  for each row execute function set_atualizado_em();
