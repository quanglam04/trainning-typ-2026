truncate table loan_items, loans, books, authors, members
restart identity cascade;

insert into authors(full_name) values
('Robert C. Martin'),
('Joshua Bloch'),
('George Orwell'),
('Tác giả chưa có sách');

insert into books(title, author_id, category, published_year, available_copies, shelf_code) values
('Clean Code', 1, 'Technology', 2008, 3, 'A-01'),
('Clean Architecture', 1, 'Technology', 2017, 2, 'A-02'),
('Effective Java', 2, 'Technology', 2018, 4, 'A-03'),
('1984', 3, 'Literature', 1949, 5, 'B-01'),
('Animal Farm', 3, 'Literature', 1945, 2, 'B-02'),
('PostgreSQL Practice', null, 'Database', 2025, 1, 'C-01'),
('SQL for Beginners', null, 'Database', 2024, 2, 'C-02'),
('Algorithms Basic', null, 'Technology', 2023, 1, 'A-04');

insert into members(full_name, email) values
('Nguyễn An', 'an@example.com'),
('Trần Bình', 'binh@example.com'),
('Lê Chi', 'chi@example.com'),
('Phạm Dũng', 'dung@example.com');

insert into loans(member_id, borrowed_at, due_date, returned_at) values
(1, now() - interval '20 days', current_date - 6, now() - interval '10 days'),
(1, now() - interval '5 days', current_date + 9, null),
(2, now() - interval '3 days', current_date + 11, null),
(3, now() - interval '2 days', current_date + 12, null);

insert into loan_items(loan_id, book_id, quantity) values
(1, 1, 1),
(1, 4, 1),
(2, 2, 1),
(3, 3, 1),
(3, 4, 1),
(4, 1, 1);
