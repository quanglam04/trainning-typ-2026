drop table if exists index_demo;

create table index_demo (
    id bigserial primary key,
    search_code varchar(40) not null,
    payload text not null
);

insert into index_demo(search_code, payload)
select
    'BOOK-' || lpad(g::text, 6, '0'),
    md5(g::text) || md5((g * 7)::text)
from generate_series(1, 200000) as g;

analyze index_demo;

explain (analyze, buffers)
select id, search_code, payload
from index_demo
where search_code = 'BOOK-199999';

create index idx_index_demo_search_code
on index_demo(search_code);

analyze index_demo;

explain (analyze, buffers)
select id, search_code, payload
from index_demo
where search_code = 'BOOK-199999';
