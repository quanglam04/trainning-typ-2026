# Screenshot - Locking

## 01-pessimistic-session-a.png

Session A:

```sql
begin;

select id, title, available_copies
from books
where id = 1
for update;
```

Không `commit`.

## 02-pessimistic-session-b-waiting.png

Trong khi A vẫn giữ lock, Session B chạy cùng `select ... for update`.

Chụp lúc Session B đang chạy nhưng chưa trả kết quả.

Đây là ảnh quan trọng nhất để chứng minh 2 transaction đồng thời và lock hoạt động.

Sau khi chụp:
- Session A: `commit`;
- Session B: `rollback`.

## 03-optimistic-conflict.png

Cả A và B cùng đọc `version = 0`.

Session A update trước -> `update 1`.

Session B update với version cũ -> `update 0`.

Chụp Session B thấy rõ `update 0`.
