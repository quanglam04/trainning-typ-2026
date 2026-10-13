create table if not exists accounts (
    id bigserial primary key,
    owner_name varchar(120) not null,
    balance numeric(12, 2) not null check (balance >= 0)
);

truncate table accounts restart identity;

insert into accounts(owner_name, balance) values
('Account A', 1000.00),
('Account B', 500.00);

select *
from accounts
order by id;

begin;

update accounts
set balance = balance - 100
where id = 1;

update accounts
set balance = balance + 100
where id = 2;

commit;

select *
from accounts
order by id;

begin;

update accounts
set balance = balance - 50
where id = 1;

select *
from accounts
order by id;

rollback;

select *
from accounts
order by id;
