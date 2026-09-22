create table usuarios_internos (
  id uuid primary key references auth.users(id) on delete cascade,
  nome text not null,
  email text not null unique,
  nivel text not null,
  ativo boolean default true,
  created_at timestamptz default now(),
  cpf text unique,
  whatsapp text,
  avatar_config jsonb,
  personalidade jsonb,
  onboarding_concluido boolean not null default false,
  personagem_atualizado_em timestamptz,
  foto_url text
);

create table setores (
  id uuid primary key default gen_random_uuid(),
  nome text not null,
  dia_virada int default 1,
  created_at timestamptz default now(),
  fluxo_suporte boolean default false,
  empresa_id uuid references empresa(id),
  eh_departamento boolean not null default true,
  acesso_por_empresa boolean not null default false,
  sigla_atendimento text,
  caixa_compartilhada boolean not null default false,
  unique (empresa_id, nome)
);

create table usuario_setores (
  usuario_id uuid not null references usuarios_internos(id) on delete cascade,
  setor_id uuid not null references setores(id) on delete cascade,
  principal boolean default false,
  created_at timestamptz default now(),
  ultima_atribuicao_em timestamptz,
  recebe_fila boolean not null default true,
  instancia_id uuid,
  primary key (usuario_id, setor_id)
);
create index ix_usuario_setores_setor on usuario_setores (setor_id);

create table notificacoes (
  id uuid primary key default gen_random_uuid(),
  usuario_id uuid not null references usuarios_internos(id) on delete cascade,
  tipo text not null,
  subtipo text,
  titulo text not null,
  mensagem text not null,
  link text,
  entidade_tipo text,
  entidade_id uuid,
  lida boolean not null default false,
  lida_em timestamptz,
  criada_em timestamptz not null default now(),
  empresa_id uuid references empresa(id)
);
create index ix_notificacoes_usuario on notificacoes (usuario_id, lida);

create or replace function is_interno()
returns boolean language sql stable security definer set search_path to 'public'
as $$
  select exists (
    select 1 from public.usuarios_internos
    where id = auth.uid() and coalesce(ativo, true) = true
  );
$$;

create or replace function get_user_level()
returns text language sql stable security definer set search_path to 'public','pg_temp'
as $$
  select case when nivel = 'ceo' then 'diretor' else nivel end
  from usuarios_internos where id = auth.uid();
$$;

create or replace function notificar_usuario(
  p_usuario_id uuid, p_tipo text, p_subtipo text, p_titulo text, p_mensagem text,
  p_link text default null, p_entidade_tipo text default null, p_entidade_id uuid default null
) returns uuid language plpgsql security definer set search_path to 'public','pg_temp'
as $$
declare v_id uuid;
begin
  if p_usuario_id is null then return null; end if;
  insert into notificacoes (usuario_id, tipo, subtipo, titulo, mensagem, link, entidade_tipo, entidade_id)
  values (p_usuario_id, p_tipo, p_subtipo, p_titulo, p_mensagem, p_link, p_entidade_tipo, p_entidade_id)
  returning id into v_id;
  return v_id;
end;
$$;
comment on function notificar_usuario is 'Versao central: grava a notificacao. Push/preferencias (notificacoes_preferencias, push_subscriptions, pg_net, vault) ficam pendentes de infra propria.';

alter table usuarios_internos enable row level security;
alter table setores enable row level security;
alter table usuario_setores enable row level security;
alter table notificacoes enable row level security;
