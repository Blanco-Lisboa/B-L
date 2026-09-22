create table usuario_empresa (
  usuario_id uuid not null references usuarios_internos(id) on delete cascade,
  empresa_id uuid not null references empresa(id) on delete cascade,
  papel text not null default 'OPERADOR' check (papel in ('OPERADOR','GESTOR','DIRETOR')),
  criado_em timestamptz not null default now(),
  primary key (usuario_id, empresa_id)
);
create index ix_usuario_empresa_empresa on usuario_empresa (empresa_id);
alter table usuario_empresa enable row level security;

create or replace function tem_acesso_empresa(p_empresa_id uuid)
returns boolean language sql stable security definer set search_path to 'public' as $$
  select exists (
    select 1 from public.usuario_empresa
    where usuario_id = auth.uid() and empresa_id = p_empresa_id
  );
$$;

create policy ue_ver_proprio on usuario_empresa
  for select to authenticated using (usuario_id = auth.uid());

create policy integracao_por_empresa on integracao
  for select to authenticated using (tem_acesso_empresa(empresa_id));
create policy integracao_evento_por_empresa on integracao_evento
  for select to authenticated using (tem_acesso_empresa(empresa_id));
create policy integracao_operacao_por_empresa on integracao_operacao
  for select to authenticated using (
    exists (select 1 from public.integracao i where i.id = integracao_id and tem_acesso_empresa(i.empresa_id))
  );

create policy fin_cobranca_por_empresa on fin_cobranca
  for select to authenticated using (tem_acesso_empresa(empresa_id));
create policy fin_contrato_por_empresa on fin_contrato
  for select to authenticated using (tem_acesso_empresa(empresa_id));
create policy fin_pagamento_por_empresa on fin_pagamento
  for select to authenticated using (
    exists (select 1 from public.fin_cobranca c where c.id = cobranca_id and tem_acesso_empresa(c.empresa_id))
  );
create policy fin_status_hist_por_empresa on fin_cobranca_status_hist
  for select to authenticated using (
    exists (select 1 from public.fin_cobranca c where c.id = cobranca_id and tem_acesso_empresa(c.empresa_id))
  );
create policy fin_aviso_por_empresa on fin_aviso
  for select to authenticated using (
    exists (select 1 from public.fin_cobranca c where c.id = cobranca_id and tem_acesso_empresa(c.empresa_id))
  );
