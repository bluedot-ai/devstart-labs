#!/usr/bin/env bash
set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

if [[ $# -lt 1 ]]; then
  echo "用法：$0 <备份文件.sql.gz> [--confirm]" >&2
  exit 1
fi

BACKUP_FILE="$(realpath "$1")"
if [[ ! -f "${BACKUP_FILE}" ]]; then
  echo "错误：备份文件不存在：${BACKUP_FILE}" >&2
  exit 1
fi

gzip -t "${BACKUP_FILE}"

if [[ "${2:-}" != "--confirm" ]]; then
  echo "恢复会覆盖当前数据库中的同名表。"
  echo "确认文件无误后重新执行："
  echo "$0 '${BACKUP_FILE}' --confirm"
  exit 2
fi

cd "${PROJECT_DIR}"
if ! docker compose ps --status running mysql --format json | grep -q 'mysql'; then
  echo "错误：MySQL 容器没有运行。" >&2
  exit 1
fi

echo "正在从备份恢复数据库：${BACKUP_FILE}"
gzip -dc "${BACKUP_FILE}" | docker compose exec -T mysql sh -c \
  'exec mysql -uroot -p"$MYSQL_ROOT_PASSWORD" "$MYSQL_DATABASE"'

echo "数据库恢复完成。"
