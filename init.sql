SELECT 'CREATE DATABASE starwars'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'starwars')\gexec

GRANT ALL PRIVILEGES ON DATABASE starwars TO admin;