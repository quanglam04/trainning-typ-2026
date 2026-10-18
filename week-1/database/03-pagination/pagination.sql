select id, title
from books
order by id
limit 3 offset 0;

select id, title
from books
order by id
limit 3 offset 3;

select id, title
from books
where id > 3
order by id
limit 3;
