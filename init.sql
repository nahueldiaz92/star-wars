-- Primero verifica si la base de datos existe usando una conexión diferente
SELECT 'CREATE DATABASE starwars'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'starwars')\gexec

-- Luego otorga privilegios
GRANT ALL PRIVILEGES ON DATABASE starwars TO admin;