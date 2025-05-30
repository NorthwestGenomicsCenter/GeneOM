BEGIN TRANSACTION ;

CREATE USER multiomic;
ALTER USER multiomic WITH PASSWORD '1234567';
CREATE DATABASE multiomics;
CREATE SCHEMA multiomics;

GRANT ALL ON DATABASE multiomics TO multiomic;
ALTER DATABASE multiomics owner TO multiomic;
GRANT ALL ON SCHEMA multiomics TO multiomic;
alter schema multiomics owner to multiomic;

COMMIT TRANSACTION ;