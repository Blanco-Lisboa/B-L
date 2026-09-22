create or replace function set_atualizado_em()
returns trigger language plpgsql
set search_path = ''
as $$
begin
  new.atualizado_em = now();
  return new;
end;
$$;
