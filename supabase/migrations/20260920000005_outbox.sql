create table outbox_evento (
  id uuid primary key default gen_random_uuid(),
  agregado text not null,
  agregado_id uuid not null,
  tipo_evento text not null,
  payload jsonb not null default '{}'::jsonb,
  ocorrido_em timestamptz not null default now(),
  publicado_em timestamptz,
  tentativas int not null default 0,
  ultimo_erro text
);
create index ix_outbox_pendente on outbox_evento (ocorrido_em)
  where publicado_em is null;
create index ix_outbox_agregado on outbox_evento (agregado, agregado_id);
