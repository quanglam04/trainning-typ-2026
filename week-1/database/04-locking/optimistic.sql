update books
set available_copies = 1,
    version = 0
where id = 1;

select id, title, available_copies, version
from books
where id = 1;

-- session a chạy trước
update books
set available_copies = available_copies - 1,
    version = version + 1
where id = 1
  and version = 0
  and available_copies > 0;

-- mong đợi: update 1

-- session b chạy lại cùng câu update với version = 0
-- mong đợi: update 0
