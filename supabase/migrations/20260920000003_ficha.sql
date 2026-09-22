create table contato (
  id uuid primary key default gen_random_uuid(),
  cliente_id uuid references cliente(id),
  empresa_id uuid references empresa(id),
  tipo text not null check (tipo in ('telefone','email','whatsapp')),
  valor text not null,
  rotulo text,
  principal boolean not null default false,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now(),
  constraint contato_um_dono
    check ((cliente_id is not null)::int + (empresa_id is not null)::int = 1)
);
create index ix_contato_cliente on contato (cliente_id);
create index ix_contato_empresa on contato (empresa_id);

create table endereco (
  id uuid primary key default gen_random_uuid(),
  cliente_id uuid references cliente(id),
  empresa_id uuid references empresa(id),
  logradouro text,
  numero text,
  complemento text,
  bairro text,
  cidade text,
  uf text,
  cep text,
  tipo text,
  principal boolean not null default false,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now(),
  constraint endereco_um_dono
    check ((cliente_id is not null)::int + (empresa_id is not null)::int = 1)
);
create index ix_endereco_empresa on endereco (empresa_id);

create table loja_canal (
  id uuid primary key default gen_random_uuid(),
  empresa_id uuid not null references empresa(id),
  canal text not null,
  nome_loja text,
  codigo_gs text,
  ativo boolean not null default true,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now()
);
create index ix_loja_canal_empresa on loja_canal (empresa_id);

create table acesso_externo (
  id uuid primary key default gen_random_uuid(),
  cliente_id uuid references cliente(id),
  empresa_id uuid references empresa(id),
  sistema text not null,
  categoria text,
  usuario text,
  observacao text,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now(),
  constraint acesso_um_dono
    check ((cliente_id is not null)::int + (empresa_id is not null)::int = 1)
);
comment on table acesso_externo is 'Somente identificador/usuario de acessos externos. Nunca armazenar senha, token ou segredo aqui; segredos ficam em cofre proprio.';
create index ix_acesso_empresa on acesso_externo (empresa_id);

create table atributo_perfil (
  id uuid primary key default gen_random_uuid(),
  cliente_id uuid references cliente(id),
  empresa_id uuid references empresa(id),
  grupo text,
  chave text not null,
  valor text,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now(),
  constraint atributo_um_dono
    check ((cliente_id is not null)::int + (empresa_id is not null)::int = 1)
);
create index ix_atributo_cliente on atributo_perfil (cliente_id);
create index ix_atributo_empresa on atributo_perfil (empresa_id);

create trigger trg_contato_upd before update on contato
  for each row execute function set_atualizado_em();
create trigger trg_endereco_upd before update on endereco
  for each row execute function set_atualizado_em();
create trigger trg_loja_canal_upd before update on loja_canal
  for each row execute function set_atualizado_em();
create trigger trg_acesso_externo_upd before update on acesso_externo
  for each row execute function set_atualizado_em();
create trigger trg_atributo_perfil_upd before update on atributo_perfil
  for each row execute function set_atualizado_em();
