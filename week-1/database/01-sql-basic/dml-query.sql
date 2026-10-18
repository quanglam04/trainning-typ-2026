insert into members(full_name, email)
values ('Demo User', 'demo@example.com')
on conflict (email) do nothing;

select *
from members
where email = 'demo@example.com';

update members
set full_name = 'Demo User Updated'
where email = 'demo@example.com';

select *
from members
where email = 'demo@example.com';

delete from members
where email = 'demo@example.com';

select *
from members
where email = 'demo@example.com';

select id, title, category
from books
where category = 'Technology'
order by id;

select b.id, b.title, a.full_name as author_name
from books b
inner join authors a on b.author_id = a.id
order by b.id;

select b.id, b.title, a.full_name as author_name
from books b
left join authors a on b.author_id = a.id
order by b.id;

select b.title, a.full_name as author_name
from books b
right join authors a on b.author_id = a.id
order by a.id, b.id;
