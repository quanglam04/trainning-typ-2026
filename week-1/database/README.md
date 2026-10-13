# Database - Week 1

Bài toán thực hành: **Quản lý thư viện**

## Môi trường

```text
datasource name: Week1 Library
host: localhost
port: 5435
database: week1_library
user: postgres
password: postgres
```

Chạy PostgreSQL:

```bash
docker compose up -d
docker compose ps
```

## Thứ tự module

1. `01-sql-basic`
2. `02-index`
3. `03-pagination`
4. `04-locking`
5. `05-transaction`

Mỗi module tự chứa:
- `README.md`: lý thuyết đúng theo yêu cầu đề;
- file `.sql`: demo;
- `screenshots/`: hướng dẫn và ảnh minh chứng của chính module.
