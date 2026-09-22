create table tarefas_acompanhamento_etapas (
  id uuid not null default gen_random_uuid(),
  subtarefa_id uuid not null,
  titulo text not null,
  concluida boolean not null default false,
  concluida_em timestamp with time zone,
  ordem integer not null default 0,
  criado_por uuid,
  delegada_subtarefa_id uuid,
  created_at timestamp with time zone not null default now(),
  status text not null default 'a_fazer'::text
);

create table tarefas_alerta_clientes (
  id uuid not null default gen_random_uuid(),
  alerta_id uuid not null,
  cliente_id uuid not null
);

create table tarefas_alerta_destinatarios (
  id uuid not null default gen_random_uuid(),
  alerta_id uuid not null,
  usuario_id uuid not null,
  setor_id uuid,
  aberto_em timestamp with time zone,
  visto_em timestamp with time zone,
  created_at timestamp with time zone not null default now(),
  visto_sem_frase boolean not null default false
);

create table tarefas_alertas_gatilho (
  id uuid not null default gen_random_uuid(),
  gatilho_id uuid,
  alvo_tipo text not null,
  alvo_id uuid not null,
  tarefa_id uuid not null,
  destinatario_usuario_id uuid not null,
  texto text not null,
  criado_em timestamp with time zone not null default now(),
  confirmado_em timestamp with time zone,
  status text not null default 'pendente'::text,
  cliente_id uuid
);

create table tarefas_alertas_informativos (
  id uuid not null default gen_random_uuid(),
  titulo text not null,
  descricao text,
  titulo_formatado jsonb,
  descricao_formatado jsonb,
  escopo text not null default 'pessoas'::text,
  criado_por uuid not null,
  created_at timestamp with time zone not null default now(),
  concluido_em timestamp with time zone,
  origem text not null default 'manual'::text,
  exige_frase boolean not null default false,
  subtarefa_id uuid,
  tarefa_id uuid
);

create table tarefas_anexos (
  id uuid not null default gen_random_uuid(),
  tarefa_id uuid not null,
  usuario_id uuid not null,
  nome_arquivo text not null,
  storage_path text not null,
  tamanho_bytes bigint,
  tipo_mime text,
  created_at timestamp with time zone default now(),
  subtarefa_id uuid
);

create table tarefas_anotacoes (
  id uuid not null default gen_random_uuid(),
  tarefa_id uuid not null,
  usuario_id uuid not null,
  texto text not null,
  created_at timestamp with time zone default now(),
  subtarefa_id uuid,
  texto_formatado jsonb
);

create table tarefas_categorias (
  id uuid not null default gen_random_uuid(),
  slug text not null,
  nome text not null,
  cor text not null default '#64748b'::text,
  icone text not null default 'Tag'::text,
  ordem integer not null default 0,
  ativo boolean not null default true,
  created_at timestamp with time zone not null default now(),
  created_by uuid
);

create table tarefas_cobrancas (
  id uuid not null default gen_random_uuid(),
  tarefa_id uuid not null,
  subtarefa_id uuid,
  alvo_usuario_id uuid not null,
  cobrado_por uuid not null,
  cobrado_em timestamp with time zone not null default now(),
  status text not null default 'pendente'::text,
  nova_data_prometida timestamp with time zone,
  justificativa text,
  respondido_em timestamp with time zone
);

create table tarefas_cobrancas_fluxo (
  id uuid not null default gen_random_uuid(),
  subtarefa_id uuid not null,
  cobrado_por uuid not null,
  status text not null default 'pendente'::text,
  criado_em timestamp with time zone not null default now(),
  prazo_solicitado_em timestamp with time zone,
  decidido_em timestamp with time zone,
  decidido_por uuid,
  novo_prazo date,
  resolvido_em timestamp with time zone
);

create table tarefas_delegados_criacao (
  id uuid not null default gen_random_uuid(),
  ceo_id uuid not null,
  usuario_id uuid not null,
  created_at timestamp with time zone not null default now()
);

create table tarefas_demanda_alertas (
  id uuid not null default gen_random_uuid(),
  rascunho_id uuid not null,
  tarefa_id uuid,
  subtarefa_id uuid,
  codigo text not null,
  nivel text not null,
  titulo text not null,
  texto text,
  acoes jsonb not null default '[]'::jsonb,
  dados jsonb not null default '{}'::jsonb,
  origem text not null default 'sistema'::text,
  criado_por uuid,
  criado_em timestamp with time zone not null default now(),
  resolvido_em timestamp with time zone,
  resolvido_por uuid,
  acao_escolhida text,
  justificativa text,
  alertado_em timestamp with time zone
);

create table tarefas_demanda_pagamentos (
  id uuid not null default gen_random_uuid(),
  rascunho_id uuid not null,
  subtarefa_id uuid,
  descricao text not null,
  valor numeric(12,2),
  vencimento date,
  arquivo_path text,
  arquivo_nome text,
  pago_em timestamp with time zone,
  conferido_por uuid,
  conferido_em timestamp with time zone,
  observacao text,
  criado_por uuid,
  criado_em timestamp with time zone not null default now(),
  substituido_em timestamp with time zone,
  substituido_por uuid
);

create table tarefas_demanda_rascunhos (
  id uuid not null default gen_random_uuid(),
  modelo_id uuid not null,
  cliente_id uuid,
  situacao_cliente text not null,
  criado_por uuid not null,
  tarefa_id uuid,
  enviado_em timestamp with time zone,
  created_at timestamp with time zone not null default now(),
  atualizado_em timestamp with time zone not null default now(),
  via text,
  acompanhamento_horas integer
);

create table tarefas_gatilho_execucoes (
  gatilho_id uuid not null,
  ultimo_disparo_em timestamp with time zone not null default now(),
  alvo_resolvido boolean not null default false
);

create table tarefas_gatilhos (
  id uuid not null default gen_random_uuid(),
  alvo_tipo text not null,
  alvo_id uuid not null,
  momento text not null,
  acao text not null,
  texto text,
  incluir_cabecalho boolean not null default true,
  destino_setor_id uuid,
  destino_usuario_id uuid,
  destino_niveis text[],
  repetir boolean not null default false,
  repetir_horas integer,
  ativo boolean not null default true,
  criado_por uuid not null,
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now(),
  cliente_id uuid
);

create table tarefas_historico (
  id uuid not null default gen_random_uuid(),
  tarefa_id uuid not null,
  usuario_id uuid not null,
  acao text not null,
  valor_anterior jsonb,
  valor_novo jsonb,
  created_at timestamp with time zone default now(),
  ip_endereco text
);

create table tarefas_modelo_anexos (
  id uuid not null default gen_random_uuid(),
  modelo_id uuid not null,
  nome_arquivo text not null,
  tipo_mime text,
  caminho text not null,
  tamanho_bytes bigint,
  criado_por uuid,
  created_at timestamp with time zone not null default now()
);

create table tarefas_modelo_compartilhamentos (
  id uuid not null default gen_random_uuid(),
  modelo_id uuid not null,
  usuario_id uuid not null,
  pode_editar boolean not null default false,
  compartilhado_por uuid not null,
  created_at timestamp with time zone not null default now()
);

create table tarefas_modelo_destinatarios (
  modelo_id uuid not null,
  usuario_id uuid not null
);

create table tarefas_modelo_gatilhos (
  id uuid not null default gen_random_uuid(),
  modelo_id uuid not null,
  alvo_tipo text not null,
  modelo_subtarefa_id uuid,
  momento text not null,
  acao text not null,
  texto text,
  incluir_cabecalho boolean not null default true,
  destino_setor_id uuid,
  destino_usuario_id uuid,
  destino_niveis text[],
  repetir boolean not null default false,
  repetir_horas integer
);

create table tarefas_modelo_subtarefas (
  id uuid not null default gen_random_uuid(),
  modelo_id uuid not null,
  titulo text not null,
  descricao text,
  atribuido_a_id uuid,
  acesso_restrito boolean not null default false,
  oculta_de_outros boolean not null default false,
  requer_retorno boolean not null default true,
  prazo_dias_relativo integer,
  prazo_hora time without time zone,
  ordem integer not null default 0,
  created_at timestamp with time zone not null default now(),
  pecas jsonb not null default '[]'::jsonb
);

create table tarefas_modelos (
  id uuid not null default gen_random_uuid(),
  dono_id uuid not null,
  titulo text not null,
  descricao text,
  categoria text,
  confidencialidade text,
  prioridade text not null default 'normal'::text,
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now(),
  tipo text not null default 'padrao'::text,
  estrutura jsonb
);

create table tarefas_permissao_empresa_extra (
  usuario_id uuid not null,
  empresa_id uuid not null,
  criado_por uuid,
  created_at timestamp with time zone not null default now()
);

create table tarefas_setores (
  id uuid not null default gen_random_uuid(),
  nome text not null,
  descricao text,
  cor text default '#6366f1'::text,
  ativo boolean not null default true,
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now()
);

create table tarefas_subtarefa_clientes (
  id uuid not null default gen_random_uuid(),
  subtarefa_id uuid not null,
  cliente_id uuid not null,
  criado_por uuid,
  created_at timestamp with time zone not null default now(),
  pessoa_id uuid
);

create table tarefas_subtarefa_info_ack (
  id uuid not null default gen_random_uuid(),
  subtarefa_id uuid not null,
  usuario_id uuid not null,
  setor_id uuid,
  revisado_em timestamp with time zone,
  created_at timestamp with time zone not null default now()
);

create table tarefas_subtarefa_info_setores (
  id uuid not null default gen_random_uuid(),
  subtarefa_id uuid not null,
  setor_id uuid not null,
  created_at timestamp with time zone not null default now()
);

create table tarefas_subtarefa_participantes_extra (
  id uuid not null default gen_random_uuid(),
  subtarefa_id uuid not null,
  usuario_id uuid not null,
  ve_desde_inicio boolean not null default false,
  adicionado_por uuid,
  adicionado_em timestamp with time zone not null default now()
);

create table tarefas_subtarefa_progresso_pessoal (
  id uuid not null default gen_random_uuid(),
  subtarefa_id uuid not null,
  usuario_id uuid not null,
  status text not null default 'a_fazer'::text,
  iniciado_em timestamp with time zone,
  iniciado_por uuid,
  concluida_em timestamp with time zone,
  concluida_por uuid,
  updated_at timestamp with time zone not null default now()
);

create table tarefas_subtarefas (
  id uuid not null default gen_random_uuid(),
  tarefa_id uuid not null,
  titulo text not null,
  concluida boolean not null default false,
  prazo date,
  prazo_hora time without time zone,
  ordem integer not null default 0,
  criado_por uuid not null,
  concluida_em timestamp with time zone,
  concluida_por uuid,
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now(),
  descricao text,
  status text not null default 'a_fazer'::text,
  iniciado_em timestamp with time zone,
  iniciado_por uuid,
  atribuido_a_id uuid,
  acesso_restrito boolean not null default false,
  informativa boolean not null default false,
  revisado_em timestamp with time zone,
  revisado_por uuid,
  requer_retorno boolean,
  oculta_de_outros boolean not null default false,
  cadeia_id uuid,
  subtarefa_origem_id uuid,
  validacao_solicitada boolean not null default false,
  validacao_alvo_id uuid,
  cliente_id uuid,
  categoria text,
  confidencialidade text not null default 'baixa'::text,
  prioridade text not null default 'normal'::text,
  eh_acompanhamento boolean not null default false,
  subtarefa_pai_id uuid,
  setor_id uuid,
  notificacao_vista_em timestamp with time zone,
  titulo_formatado jsonb,
  descricao_formatado jsonb,
  depende_de_id uuid,
  mensagem_validacao text,
  eh_retrabalho boolean not null default false,
  empresa_id uuid,
  eh_duvida boolean not null default false,
  projeto_no_id uuid,
  data_inicio date,
  numero_ticket integer,
  numero_retrabalho integer,
  numero_duvida integer,
  numero_ticket_relacionado integer,
  depende_de_gatilho text not null default 'concluido'::text,
  depende_de_dias_apos integer not null default 0,
  excluido_em timestamp with time zone,
  excluido_por uuid,
  depende_de_tarefa_id uuid,
  relato_exigido_escrito boolean not null default false,
  relato_exigido_audio boolean not null default false,
  relato_exigido_presencial boolean not null default false,
  relato_texto text,
  relato_audio_path text,
  relato_audio_duracao_seg integer,
  relato_presencial_confirmado boolean not null default false,
  relato_registrado_em timestamp with time zone,
  relato_registrado_por uuid,
  oculto_exceto_ids uuid[] not null default '{}'::uuid[],
  restrito_exceto_ids uuid[] not null default '{}'::uuid[],
  alerta_confirmado boolean not null default false,
  vou_iniciar_em timestamp with time zone,
  alerta_adiado_ate timestamp with time zone,
  critica_conflito_com_id uuid,
  critica_conflito_decisao text,
  critica_conflito_ceo_id uuid,
  prazo_por_fluxo boolean not null default false,
  prazo_perdido_avisado boolean not null default false,
  planilha_valores jsonb,
  planilha_secreta boolean not null default false,
  reaberto_em timestamp with time zone,
  reaberto_por uuid,
  pecas jsonb not null default '[]'::jsonb,
  pecas_estado jsonb not null default '{}'::jsonb,
  cancelado_em timestamp with time zone,
  cancelado_por uuid,
  motivo_cancelamento text
);

create table tarefas_tarefa_cliente_rascunho (
  tarefa_id uuid not null,
  dados jsonb not null default '{}'::jsonb,
  finalizado boolean not null default false,
  cliente_id uuid,
  atualizado_por uuid,
  atualizado_em timestamp with time zone not null default now(),
  created_at timestamp with time zone not null default now()
);

create table tarefas_tarefa_clientes (
  id uuid not null default gen_random_uuid(),
  tarefa_id uuid not null,
  cliente_id uuid not null,
  criado_por uuid,
  created_at timestamp with time zone not null default now()
);

create table tarefas_tarefa_conversa (
  id uuid not null default gen_random_uuid(),
  tarefa_id uuid not null,
  subtarefa_id uuid,
  conversa_id uuid,
  telefone text,
  nome_contato text,
  mensagens jsonb not null default '[]'::jsonb,
  resumo text,
  capturado_por uuid,
  capturado_em timestamp with time zone not null default now()
);

create table tarefas_tarefa_destinatarios (
  id uuid not null default gen_random_uuid(),
  tarefa_id uuid not null,
  usuario_id uuid not null,
  created_at timestamp with time zone not null default now(),
  data_inicio_visto_em timestamp with time zone
);

create table tarefas_tarefa_visto_inicio (
  tarefa_id uuid not null,
  usuario_id uuid not null,
  visto_em timestamp with time zone not null default now()
);

create table tarefas (
  id uuid not null default gen_random_uuid(),
  titulo text not null,
  descricao text,
  remetente_id uuid not null,
  status text not null default 'a_fazer'::text,
  prioridade text not null default 'normal'::text,
  prazo date,
  prazo_hora time without time zone,
  iniciado_em timestamp with time zone,
  concluido_em timestamp with time zone,
  alerta_confirmado boolean not null default false,
  vou_iniciar_em timestamp with time zone,
  categoria text,
  confidencialidade text not null default 'alta'::text,
  created_at timestamp with time zone default now(),
  updated_at timestamp with time zone default now(),
  setor_id uuid,
  triagem_id uuid,
  triagem_fase text,
  alerta_adiado_ate timestamp with time zone,
  alvo_setor_id uuid,
  assumido_por uuid,
  assumido_em timestamp with time zone,
  cliente_id uuid,
  wa_telefone text,
  revisado_em timestamp with time zone,
  revisado_por uuid,
  registrar_no_historico boolean not null default false,
  aguardando_revisao boolean not null default false,
  mensagem_revisao text,
  modelo_novo boolean not null default false,
  titulo_formatado jsonb,
  descricao_formatado jsonb,
  recorrente_id uuid,
  aguardando_revisao_em timestamp with time zone,
  tipo text not null default 'padrao'::text,
  estrutura jsonb,
  empresa_id uuid,
  projeto_no_id uuid,
  data_inicio date,
  wa_conversa_id uuid,
  numero_demanda integer,
  proximo_numero_ticket integer not null default 1,
  excluido_em timestamp with time zone,
  excluido_por uuid,
  eh_tarefa_solta boolean not null default false,
  numero_tarefa_solta integer,
  criado_de_fato_por uuid,
  cancelado_em timestamp with time zone,
  cancelado_por uuid,
  motivo_cancelamento text
);

create table tarefas_recorrentes (
  id uuid not null default gen_random_uuid(),
  titulo text not null,
  descricao text,
  categoria text,
  prioridade text not null default 'normal'::text,
  confidencialidade text not null default 'baixa'::text,
  requer_retorno boolean not null default true,
  prazo_dias integer,
  destino text not null,
  atribuido_a_id uuid,
  setor_id uuid,
  frequencia text not null,
  dia_semana integer,
  dia_mes integer,
  intervalo_dias integer,
  ativo boolean not null default true,
  proxima_execucao date not null,
  ultima_execucao date,
  total_criadas integer not null default 0,
  criado_por uuid not null,
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now(),
  atribuido_a_ids uuid[],
  proximo_indice_destinatario integer not null default 0
);
