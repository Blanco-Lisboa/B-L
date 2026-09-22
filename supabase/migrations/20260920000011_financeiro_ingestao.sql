create table fin_conexa_evento (
  id uuid primary key default gen_random_uuid(),
  id_conexa text,
  tipo text,
  referencia text,
  carga jsonb not null default '{}'::jsonb,
  recebido_em timestamptz not null default now(),
  processado_em timestamptz,
  erro text
);
create index ix_fin_evento_pendente on fin_conexa_evento (recebido_em) where processado_em is null;
create index ix_fin_evento_tipo on fin_conexa_evento (tipo);

create table fin_cobranca (
  id uuid primary key default gen_random_uuid(),
  id_conexa text,
  cliente_id uuid references cliente(id),
  empresa_id uuid references empresa(id),
  descricao text,
  valor numeric(14,2),
  vencimento date,
  status text not null default 'aberta'
    check (status in ('aberta','paga','cancelada','atrasada')),
  forma text,
  linha_digitavel text,
  boleto_url text,
  pix_copia_cola text,
  pix_qr text,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now()
);
create unique index uq_fin_cobranca_conexa on fin_cobranca (id_conexa) where id_conexa is not null;
create index ix_fin_cobranca_empresa on fin_cobranca (empresa_id);
create index ix_fin_cobranca_cliente on fin_cobranca (cliente_id);
create index ix_fin_cobranca_status on fin_cobranca (status);
create index ix_fin_cobranca_vencimento on fin_cobranca (vencimento);

create table fin_pagamento (
  id uuid primary key default gen_random_uuid(),
  id_conexa text,
  cobranca_id uuid references fin_cobranca(id) on delete set null,
  data date,
  valor numeric(14,2),
  forma text,
  comprovante text,
  criado_em timestamptz not null default now()
);
create unique index uq_fin_pagamento_conexa on fin_pagamento (id_conexa) where id_conexa is not null;
create index ix_fin_pagamento_cobranca on fin_pagamento (cobranca_id);

create table fin_contrato (
  id uuid primary key default gen_random_uuid(),
  id_conexa text,
  cliente_id uuid references cliente(id),
  empresa_id uuid references empresa(id),
  status text,
  inicio date,
  fim date,
  valor numeric(14,2),
  ciclo text,
  criado_em timestamptz not null default now(),
  atualizado_em timestamptz not null default now()
);
create unique index uq_fin_contrato_conexa on fin_contrato (id_conexa) where id_conexa is not null;
create index ix_fin_contrato_empresa on fin_contrato (empresa_id);

create table fin_cobranca_status_hist (
  id uuid primary key default gen_random_uuid(),
  cobranca_id uuid not null references fin_cobranca(id) on delete cascade,
  de text,
  para text,
  quando timestamptz not null default now()
);
create index ix_fin_status_hist_cobranca on fin_cobranca_status_hist (cobranca_id);

create table fin_aviso (
  id uuid primary key default gen_random_uuid(),
  id_conexa text,
  cobranca_id uuid references fin_cobranca(id) on delete set null,
  tipo text,
  enviado_em timestamptz,
  carga jsonb not null default '{}'::jsonb,
  criado_em timestamptz not null default now()
);
create index ix_fin_aviso_cobranca on fin_aviso (cobranca_id);

create trigger trg_fin_cobranca_upd before update on fin_cobranca
  for each row execute function set_atualizado_em();
create trigger trg_fin_contrato_upd before update on fin_contrato
  for each row execute function set_atualizado_em();

alter table fin_conexa_evento enable row level security;
alter table fin_cobranca enable row level security;
alter table fin_pagamento enable row level security;
alter table fin_contrato enable row level security;
alter table fin_cobranca_status_hist enable row level security;
alter table fin_aviso enable row level security;
