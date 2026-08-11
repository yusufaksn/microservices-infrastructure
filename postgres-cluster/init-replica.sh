#!/bin/bash
set -e

until pg_isready -h postgres_ticket -p 5432 -U "$POSTGRES_USER"; do
    echo "Waiting for primary database..."
    sleep 2
done

rm -rf "${PGDATA:?}"/*

PGPASSWORD=replicapass pg_basebackup \
    -h postgres_ticket \
    -D "$PGDATA" \
    -U replicauser \
    -v \
    -P \
    -X stream \
    -c fast \
    -R

echo "hot_standby = on" >> "$PGDATA/postgresql.conf"

chmod 700 "$PGDATA"

exec docker-entrypoint.sh postgres