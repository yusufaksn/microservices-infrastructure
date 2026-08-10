#!/bin/bash
set -e

until pg_isready -h postgres_ticket -p 5432 -U "$POSTGRES_USER"; do
  echo "Primary veritabanı (postgres_ticket) bekleniyor..."
  sleep 2
done


if [ -d "$PGDATA" ] && [ "$(ls -A $PGDATA)" ]; then
    rm -rf ${PGDATA:?}/*
fi


PGPASSWORD='replicapass' pg_basebackup \
  -h postgres_ticket \
  -D "$PGDATA" \
  -U replicauser \
  -v -P \
  -X stream \
  -c fast \
  -R


echo "hot_standby = on" >> "$PGDATA/postgresql.conf"
chmod 700 "$PGDATA"


exec docker-entrypoint.sh postgres