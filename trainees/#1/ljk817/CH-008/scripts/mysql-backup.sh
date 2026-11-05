#!/usr/bin/env bash
set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
BACKUP_DIR="${1:-${PROJECT_DIR}/backups/mysql}"
TIMESTAMP="$(date '+%Y%m%d-%H%M%S')"
BACKUP_FILE="${BACKUP_DIR}/customer-tracker-${TIMESTAMP}.sql.gz"
TEMP_FILE="${BACKUP_FILE}.tmp"

mkdir -p "${BACKUP_DIR}"
cd "${PROJECT_DIR}"

cleanup() {
  rm -f "${TEMP_FILE}"
}
trap cleanup EXIT

if ! docker compose ps --status running mysql --format json | grep -q 'mysql'; then
  echo "错误：MySQL 容器没有运行。" >&2
  exit 1
fi

echo "正在备份 MySQL 数据库……"
docker compose exec -T mysql sh -c \
  'exec mysqldump --single-transaction --routines --triggers --set-gtid-purged=OFF -uroot -p"$MYSQL_ROOT_PASSWORD" "$MYSQL_DATABASE"' \
  | gzip -9 > "${TEMP_FILE}"

gzip -t "${TEMP_FILE}"
mv "${TEMP_FILE}" "${BACKUP_FILE}"
chmod 600 "${BACKUP_FILE}"
trap - EXIT

echo "备份完成：${BACKUP_FILE}"
du -h "${BACKUP_FILE}"
