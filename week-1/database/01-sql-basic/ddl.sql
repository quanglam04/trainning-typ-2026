create table if not exists authors (
    id bigserial primary key,
    full_name varchar(120) not null
);

create table if not exists books (
    id bigserial primary key,
    title varchar(200) not null,
    author_id bigint references authors(id),
    category varchar(80) not null,
    published_year int check (published_year between 1000 and 2100),
    available_copies int not null default 0 check (available_copies >= 0),
    version int not null default 0,
    created_at timestamptz not null default now()
);

create table if not exists members (
    id bigserial primary key,
    full_name varchar(120) not null,
    email varchar(200) not null unique,
    joined_at timestamptz not null default now()
);

create table if not exists loans (
    id bigserial primary key,
    member_id bigint not null references members(id),
    borrowed_at timestamptz not null default now(),
    due_date date not null,
    returned_at timestamptz
);

create table if not exists loan_items (
    loan_id bigint not null references loans(id) on delete cascade,
    book_id bigint not null references books(id),
    quantity int not null default 1 check (quantity > 0),
    primary key (loan_id, book_id)
);

alter table books
add column if not exists shelf_code varchar(20);

create table if not exists ddl_drop_demo (
    id bigserial primary key,
    note text
);

drop table if exists ddl_drop_demo;
