update books
set available_copies = 1
where id = 1;

-- session a
begin;

select id, title, available_copies
from books
where id = 1
for update;

-- giữ session a chưa commit

-- session b:
--
-- begin;
--
-- select id, title, available_copies
-- from books
-- where id = 1
-- for update;
--
-- session b sẽ chờ

-- quay lại session a
update books
set available_copies = available_copies - 1
where id = 1
  and available_copies > 0;

commit;

-- session b tiếp tục
-- rollback;
