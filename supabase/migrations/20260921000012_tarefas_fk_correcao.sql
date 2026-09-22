do $$
declare r record; c record;
begin
  for r in (values
    ('tarefas_alerta_clientes','cliente_id','empresa',''),
    ('tarefas_alertas_gatilho','cliente_id','empresa',''),
    ('tarefas_demanda_rascunhos','cliente_id','empresa',' on delete set null'),
    ('tarefas_gatilhos','cliente_id','empresa',''),
    ('tarefas_subtarefa_clientes','cliente_id','empresa',''),
    ('tarefas_subtarefas','cliente_id','empresa',''),
    ('tarefas_tarefa_clientes','cliente_id','empresa',''),
    ('tarefas','cliente_id','empresa',''),
    ('tarefas_permissao_empresa_extra','empresa_id','companhia_grupo',' on delete cascade'),
    ('tarefas_subtarefas','empresa_id','companhia_grupo',''),
    ('tarefas','empresa_id','companhia_grupo','')
  ) as x(tbl,col,ref,ondel)
  loop
    for c in
      select con.conname
      from pg_constraint con
      join pg_class cl on cl.oid=con.conrelid
      join pg_namespace n on n.oid=cl.relnamespace
      where n.nspname='public' and cl.relname=r.tbl and con.contype='f'
        and con.conkey = (select array_agg(a.attnum order by a.attnum)
                          from pg_attribute a
                          where a.attrelid=con.conrelid and a.attname=r.col and a.attnum>0)
    loop
      execute format('alter table public.%I drop constraint %I', r.tbl, c.conname);
    end loop;
    execute format('alter table public.%I add foreign key (%I) references public.%I(id)%s',
                   r.tbl, r.col, r.ref, r.ondel);
  end loop;
end$$;
