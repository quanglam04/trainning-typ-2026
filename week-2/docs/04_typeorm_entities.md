# 04 — ORM, entity mapping và quan hệ

## ORM làm gì?

ORM ánh xạ object sang mô hình quan hệ và sinh SQL cho thao tác dữ liệu. Nó giảm code
CRUD lặp, hỗ trợ mapping kiểu và relation; không xóa nhu cầu hiểu SQL, transaction,
index hoặc execution plan. QueryBuilder vẫn sinh SQL, còn Repository là API thao tác
entity; không phải mọi ORM query đều nhanh hơn hoặc chậm hơn raw SQL theo tỷ lệ cố định.

## Nguồn schema chính thức

`sql/01_schema.sql` tuần 1 là nguồn bootstrap. TypeORM map các bảng có sẵn, không dùng
synchronize để sửa schema. DTO có thể đặt giới hạn chặt hơn DB; mapping cột phải khớp
database đang sử dụng.

| Property TypeScript | Column PostgreSQL | Kiểu/đặc điểm |
|---|---|---|
| id | id | UUID PK |
| authorId, categoryId | author_id, category_id | UUID FK, NOT NULL, RESTRICT |
| title, slug | title, slug | VARCHAR(255), VARCHAR(280); slug UNIQUE |
| description | description | TEXT NOT NULL |
| contentJson | content_json | JSONB NOT NULL |
| thumbnailUrl | thumbnail_url | VARCHAR(512), nullable |
| version | version | VARCHAR(32), default 1.0.0 |
| isPublic, isFeatured | is_public, is_featured | BOOLEAN |
| viewsCount, forksCount | views_count, forks_count | INT, không âm |
| ratingAvg, ratingsCount | rating_avg, ratings_count | NUMERIC(3,2), INT |
| tags | tags | TEXT[] |
| createdAt/updatedAt/deletedAt | created_at/updated_at/deleted_at | TIMESTAMPTZ; deleted_at nullable |

HTML/metadata được lưu trong envelope content_json; xem [DTO](03_dto_validation.md).
Cột numeric có transformer khi đọc để ratingAvg được trả dưới dạng number trong JSON.

## Relations và bên giữ FK

```typescript
@ManyToOne(() => CategoryEntity, c => c.templates, {
  onDelete: "RESTRICT", nullable: false,
})
@JoinColumn({ name: "category_id" })
category!: CategoryEntity;
```

Một Template thuộc một Author và một Category. Bên ManyToOne (templates) giữ FK;
OneToMany ở author/category là chiều ngược, không tạo mảng UUID trong bảng cha.
RESTRICT ngăn xóa vật lý bản ghi cha còn được template tham chiếu. Đây khác
`cascade: ['insert','update']` của ORM: cascade đó truyền thao tác save object graph,
không phải chính sách FK ON DELETE.

Eager tự load relation theo cơ chế repository hỗ trợ; lazy dùng Promise và có thể
phát sinh query khi truy cập. Adapter bài này không bật cả hai; query chủ động
leftJoinAndSelect category. Với quan hệ OneToMany lớn, cần cân nhắc JOIN hoặc tải
theo lô dựa trên số row, kích thước response và query plan.

## Mapping một phần và thông tin nhạy cảm

AuthorEntity chỉ map id/is_active/deleted_at phục vụ kiểm tra tham chiếu, không phải
user CRUD đầy đủ. Response author chỉ có id. CategoryEntity map id/name/slug.
Không đưa passwordHash vào TemplateEntity hoặc trả nguyên user relation ra HTTP.
Mapping thiếu cột có chủ đích dùng để đọc không được dùng auto-generate migration
rồi áp ngay lên production: có thể suy luận sai các cột chưa khai báo.

## Timestamps, soft delete và version

CreateDateColumn/UpdateDateColumn hỗ trợ thời điểm tạo/cập nhật qua ORM. Không có
nghĩa mọi câu raw SQL bên ngoài ORM đều được tự cập nhật timestamp.
DeleteDateColumn hỗ trợ softDelete và mặc định loại row đã xóa khỏi truy vấn entity;
row vật lý/slug UNIQUE vẫn còn. Soft delete không tự xóa quan hệ hoặc chuyển quyền.
`version` ở schema là chuỗi phiên bản nội dung, không dùng VersionColumn, chưa có
optimistic concurrency control.

## Migrations và kiểm chứng

`synchronize:false`, `migrationsRun:false`, `installExtensions:false`. Lần này không
đổi schema nên chưa cần migration mới. Thay đổi schema sau này cần migration được
rà soát và kiểm chứng nâng cấp; synchronize không cung cấp lịch sử migration.
DB integration lấy column metadata trước/sau khởi động và CRUD để đối chiếu giữ
nguyên, kiểm tra row sau soft-delete và persistence qua instance mới.

[Mốc nguồn và SQL](../SOURCE_SNAPSHOT.md).
Nguồn: [TypeORM entities](https://typeorm.io/docs/entity/entities),
[relations](https://typeorm.io/docs/relations/relations),
[migrations](https://typeorm.io/docs/migrations/why/).

[Trước](03_dto_validation.md) · [Tiếp: Repository](05_repository_pattern.md)
