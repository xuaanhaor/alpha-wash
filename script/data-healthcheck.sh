#!/bin/sh

# wait-for-postgres.sh
echo "🔍 Waiting for PostgreSQL to be ready..."

until pg_isready -h db -p 5432 -U postgres; do
  echo "Postgres is unavailable - sleeping"
  sleep 2
done

echo "✅ Postgres is ready - starting app"