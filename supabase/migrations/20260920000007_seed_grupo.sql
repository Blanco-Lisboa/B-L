with h as (
  insert into holding (nome, razao_social)
  values ('Blanco & Lisboa', 'Blanco & Lisboa Holding')
  returning id
)
insert into companhia_grupo (holding_id, slug, sigla, nome, setor, cor, ordem, situacao)
select h.id, v.slug, v.sigla, v.nome, v.setor, v.cor, v.ordem, v.situacao
from h, (values
  ('you',         'YOU', 'YOU Contabilidade', 'Contabilidade',       '#F04E23', 1, 'operante'),
  ('realizze',    'RE',  'Realizze',          'Certificados digitais','#16A6A6', 2, 'operante'),
  ('40',          '40',  '40%',               'Consultoria de valores','#1C1C1C',3, 'operante'),
  ('beec',        'BE',  'BEEC',              'Tráfego pago',        '#6D28D9', 4, 'pre_lancamento'),
  ('gestao-lojas','GL',  'Gestão de Lojas',   'Controle de lojas',   '#0891B2', 5, 'pre_lancamento'),
  ('irpf',        'IR',  'IRPF',              'Imposto de renda',    '#8A7B4F', 6, 'sazonal'),
  ('winny',       'WI',  'Winny',             'ERP',                 '#F47A2D', 7, 'nova'),
  ('itia',        'IT',  'IT.IA',             'Tecnologia',          '#FF0000', 8, 'nova')
) as v(slug, sigla, nome, setor, cor, ordem, situacao);
