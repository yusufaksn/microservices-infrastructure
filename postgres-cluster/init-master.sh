#!/bin/bash
set -e


echo "host replication replicauser 0.0.0.0/0 md5" >> "$PGDATA/pg_hba.conf"


psql -v ON_ERROR_STOP=1 \
    --username "$POSTGRES_USER" \
    --dbname "$POSTGRES_DB" <<-EOSQL

DO \$\$
BEGIN
    IF NOT EXISTS (
        SELECT FROM pg_catalog.pg_roles
        WHERE rolname = 'replicauser'
    ) THEN
        CREATE ROLE replicauser
        WITH REPLICATION LOGIN ENCRYPTED PASSWORD 'replicapass';
    END IF;
END
\$\$;
EOSQL