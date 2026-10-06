select
    category,
    count(*) as total_books,
    sum(available_copies) as total_available,
    avg(available_copies) as avg_available
from books
group by category
having count(*) >= 2
order by total_books desc, category;

select
    count(*) as book_count,
    sum(available_copies) as total_available,
    avg(available_copies) as avg_available,
    min(published_year) as oldest_year,
    max(published_year) as newest_year
from books;

select
    count(*) as total_books,
    count(author_id) as books_with_author
from books;
