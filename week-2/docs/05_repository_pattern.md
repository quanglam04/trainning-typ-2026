# 05 — Repository, QueryBuilder và N+1

## Repository là gì trong bài này?

Repository che cách truy cập một nhóm dữ liệu khỏi code gọi nó. Trong server,
TemplatesService nhận port TemplateStore qua DI token; adapter PostgreSQL dùng
TypeORM Repository/QueryBuilder, adapter memory dùng Map. Cách tách đó cho phép
chạy cùng HTTP contract với hai loại lưu trữ.

Đây là catalog port trả HTTP projection có pagination/category. Interface domain
ITemplateRepository tuần 1 vẫn tồn tại và làm việc với domain entity riêng.

## Các thao tác ORM cần phân biệt

| Thao tác | Ý nghĩa |
|---|---|
| create(dto) | Tạo entity object trong memory, chưa INSERT |
| save(entity) | Persist entity; có thể INSERT hoặc UPDATE tùy trạng thái |
| find/QueryBuilder.getOne | Đọc entity; không bắt buộc tăng counter |
| update/QueryBuilder.update | UPDATE theo điều kiện; kiểm tra affected |
| softDelete | Đặt deleted_at, giữ row và unique constraints |
| remove/delete | Xóa vật lý; không dùng cho DELETE HTTP ở bài này |

`save` không đơn giản là cứ có ID thì chắc chắn UPDATE. Adapter dùng partial UPDATE
với `id=:id AND deleted_at IS NULL`, tránh lưu object cũ thành thao tác insert hoặc
sửa row đã soft-delete. contentJson được bind thành cả JSON document.

## QueryBuilder thực tế

```typescript
const query = repo.createQueryBuilder("t")
  .leftJoinAndSelect("t.category", "category")
  .andWhere("t.authorId = :author", { author: authorId })
  .orderBy("t.createdAt", "DESC")
  .addOrderBy("t.id", "DESC")
  .skip((page - 1) * size)
  .take(size);
const [rows, total] = await query.getManyAndCount();
```

Đây là đoạn rút gọn; adapter chỉ thêm author filter khi được gửi lên. Giá trị
người dùng dùng bind parameters. Tên cột sort/order không bind như value: phải
whitelist rồi ánh xạ tên cột cố định. Search dùng ILIKE, escape `%`, `_`, `\` để tìm
substring literal; **ILIKE không phải PostgreSQL full-text search**.
Tags dùng array containment với text[]. Dùng EXPLAIN trên dữ liệu và điều kiện
cụ thể để xác định index được chọn và chi phí thực thi.

## N+1 và cách kiểm chứng

Lấy N templates bằng một query rồi vòng lặp query category cho mỗi template có thể
tạo 1+N query. Query join category của adapter tránh lookup riêng cho từng row.
Điều này không đồng nghĩa tổng request chỉ có một query: pagination/COUNT/ORM join
strategy có thể tạo thêm query số lượng cố định. Muốn báo số query phải đo SQL log
của phiên bản đang chạy, phân biệt SELECT metadata lúc bootstrap với query request.

N+1 tuần tự không đồng nghĩa giữ 21 connection đồng thời: pool có thể tái sử dụng
cùng connection. Thời gian còn phụ thuộc latency, dữ liệu, cache, index và execution
plan. Adapter hiện không có vòng lặp lookup category; **chưa có benchmark N+1**.

## Unique constraint và race condition

Kiểm tra slug trước INSERT không ngăn được hai request đồng thời cùng vượt pre-check.
Database UNIQUE là lớp quyết định. API bắt lỗi PostgreSQL 23505 và trả 409; tests
gửi hai POST cùng slug, kiểm tra một 201 và một 409. Soft-delete không giải phóng slug.
Reference author/category được kiểm tra để báo lỗi dễ hiểu; FK vẫn bảo vệ ràng buộc
ở thời điểm ghi nếu dữ liệu đổi sau pre-check.

## Transaction: đã dùng tới đâu?

Một SQL statement có tính atomic của PostgreSQL. Nhiều bước read/check/write không
tự thành một transaction chung chỉ vì chạy trong async service. Lần này CRUD đơn
resource chưa thêm transaction liên quan nhiều bảng hoặc optimistic locking.
Khi cần, dùng DataSource.transaction và **manager được truyền vào callback** cho
mọi query trong transaction; không dùng repository ngoài callback rồi tưởng đã bao
phủ. Transaction cũng không tự giải quyết mọi race; phải chọn constraint/lock và
isolation phù hợp nghiệp vụ.

[Mốc nguồn](../SOURCE_SNAPSHOT.md) · [ORM](04_typeorm_entities.md).
Nguồn: [TypeORM QueryBuilder](https://typeorm.io/docs/query-builder/select-query-builder/),
[transactions](https://typeorm.io/docs/advanced-topics/transactions/).

[Trước](04_typeorm_entities.md) · [Tiếp: Pagination](06_pagination_api.md)
