# MySQL 备份与恢复

项目使用 Docker 命名卷 `mysql-data` 保存 MySQL 数据。容器重新构建或重启不会删除数据，但命名卷不能代替独立备份。

## 创建备份

在服务器项目目录执行：

```bash
chmod +x scripts/mysql-backup.sh scripts/mysql-restore.sh
./scripts/mysql-backup.sh
```

备份默认保存到：

```text
backups/mysql/customer-tracker-年月日-时分秒.sql.gz
```

也可以指定其他备份目录：

```bash
./scripts/mysql-backup.sh /opt/backups/customer-issue-tracker
```

脚本采用 `mysqldump --single-transaction` 创建一致性备份，完成后使用 `gzip -t` 校验压缩文件，并将权限设置为仅当前用户可读写。

## 校验备份

```bash
gzip -t backups/mysql/customer-tracker-*.sql.gz
ls -lh backups/mysql/
```

`gzip -t` 没有输出且退出码为 `0` 表示压缩文件完整：

```bash
echo $?
```

## 恢复备份

恢复会覆盖当前数据库中的同名表。第一次不带确认参数执行时，脚本只显示提示，不会修改数据库：

```bash
./scripts/mysql-restore.sh backups/mysql/customer-tracker-20260101-120000.sql.gz
```

确认备份文件无误后执行：

```bash
./scripts/mysql-restore.sh backups/mysql/customer-tracker-20260101-120000.sql.gz --confirm
```

恢复后检查服务：

```bash
docker compose restart backend
docker compose ps
curl -fsS http://localhost/api/actuator/health
```

## 定时备份示例

以下示例每天凌晨 2 点执行备份。先通过 `crontab -e` 打开当前用户的定时任务，然后加入：

```cron
0 2 * * * cd /opt/customer-issue-tracker && ./scripts/mysql-backup.sh /opt/backups/customer-issue-tracker >> /var/log/customer-tracker-backup.log 2>&1
```

定时任务属于可选配置。正式启用前，应先手动执行脚本并确认备份文件能够恢复。

## 注意事项

- 不要执行 `docker compose down -v`，该命令会删除 MySQL 数据卷。
- 不要把包含真实数据的备份提交到 GitHub。
- `.gitignore` 已排除项目内的 `backups/` 目录。
- 重要备份应复制到服务器之外保存。
