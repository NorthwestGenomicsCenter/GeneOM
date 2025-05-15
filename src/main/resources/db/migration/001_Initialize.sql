BEGIN TRANSACTION ;

-- Create our main database
create database multiomics;

-- Create the main application user
create user multiomic;
alter role multiomic WITH PASSWORD '1234567';

-- Grant on the main database
grant ALL on database multiomics to multiomic;


COMMIT TRANSACTION ;