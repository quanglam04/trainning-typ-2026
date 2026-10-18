# Screenshot - Pagination

## 01-offset-page-1.png

```sql
select id, title
from books
order by id
limit 3 offset 0;
```

## 02-offset-page-2.png

```sql
select id, title
from books
order by id
limit 3 offset 3;
```

## 03-cursor-pagination.png

```sql
select id, title
from books
where id > 3
order by id
limit 3;
```

Mỗi ảnh nên thấy cả query và result.
