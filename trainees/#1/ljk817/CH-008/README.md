# CH-008 客户问题跟踪系统

- Training ID：`TRAINING-ffd7b289-b60c-481a-9604-5441e7972dd7`
- Challenge：`CH-008`
- Trainee：`ljk817`
- Batch：`#1`

一个面向客户服务团队和企业客户的问题生命周期管理系统，覆盖客户建档、问题提交、负责人分配、状态流转、公开回复、内部备注、分类配置和基础统计。

## 技术栈

- 前端：Vue 3、TypeScript、Vite、Element Plus、Nginx
- 后端：Java 17、Spring Boot 3.5、Spring Security、Spring Data JPA、Flyway
- 数据库：MySQL 8.4
- 部署：Docker Compose；提供 Kubernetes Deployment、Service、StatefulSet、PV/PVC 清单

## 功能与权限

| 角色 | 主要权限 |
| --- | --- |
| `ADMIN` | 用户、客户、分类和全部问题管理，也可参与问题处理 |
| `AGENT` | 客户维护、问题创建、分配、状态更新、公开回复和内部备注 |
| `VIEWER` | 只读查看内部工作台、客户、问题及处理记录 |
| `CUSTOMER` | 仅查看、提交和补充所属公司的问题，不能读取其他客户数据和内部备注 |

同一客户公司的多个 `CUSTOMER` 账号共享公司级问题数据。后端同时执行角色校验和客户归属校验，不能仅依靠前端隐藏按钮实现权限控制。

## 目录

```text
backend/       Spring Boot API、权限控制和 Flyway 数据库迁移
frontend/      Vue 3 前端和 Nginx 配置
deploy/        部署文件索引与部署边界说明
k8s/           Kubernetes Deployment / Service / StatefulSet / PV / PVC
scripts/       MySQL 备份与恢复脚本
docs/          技术设计、自测、备份恢复和 AI Coding 说明
compose.yaml   Docker Compose 单服务器编排
.env.example   无真实凭据的环境变量示例
```

## 配置

复制环境变量示例并替换示例密码：

```bash
cp .env.example .env
```

首次启动会使用 `INITIAL_ADMIN_USERNAME` 和 `INITIAL_ADMIN_PASSWORD` 创建管理员。管理员密码必须至少 12 位；初始化完成后，后续重启不会覆盖数据库中的账号密码。

真实密码只保存在未提交的 `.env` 或运行时 Secret 中。仓库中的示例和 Kubernetes 清单均不包含真实凭据。

## Docker Compose 启动

```bash
docker compose up -d --build
docker compose ps
```

访问地址：

- 系统页面：<http://localhost>
- 健康检查：<http://localhost/api/actuator/health>
- API 文档：<http://localhost/api/swagger-ui.html>

停止服务：

```bash
docker compose down
```

`docker compose down` 不会删除 MySQL 数据卷。存在业务数据时不要执行 `docker compose down -v`。

## 开发模式

开发环境需要 MySQL、Maven、Node.js 和 npm。

```bash
# 后端
cd backend
mvn spring-boot:run

# 前端（另一个终端）
cd frontend
npm install
npm run dev
```

浏览器访问 <http://localhost:5173>，Vite 会把 `/api` 转发到 `http://localhost:8080`。

构建检查：

```bash
cd backend && mvn -B test
cd frontend && npm install && npm run build
```

## 数据持久化和备份

Compose 使用命名卷 `mysql-data` 保存 `/var/lib/mysql`。重新构建前后端或执行不带 `-v` 的 `docker compose down` 不会删除数据库数据。

```bash
chmod +x scripts/mysql-backup.sh scripts/mysql-restore.sh
./scripts/mysql-backup.sh
```

备份默认写入被 Git 忽略的 `backups/mysql/`。恢复步骤和定时备份示例见 [docs/backup-restore.md](docs/backup-restore.md)。

## Kubernetes

`k8s/` 提供面向 VMware 双节点学习集群的完整部署清单，使用 Kustomize 管理资源，MySQL 使用 StatefulSet 和静态 PV/PVC。

```bash
kubectl apply -f k8s/namespace.yaml
# 按 k8s/README.md 创建运行时 Secret
kubectl apply -k k8s/
```

详细说明见 [k8s/README.md](k8s/README.md)。当前公网运行环境采用 Docker Compose；Kubernetes 集群和清单用于学习与后续迁移，尚未宣称业务应用已在该集群完成部署验收。

## 验证与设计

- [技术设计](docs/design.md)
- [自测说明](docs/self-test.md)
- [AI Coding 使用说明](docs/ai-coding.md)
- [备份与恢复](docs/backup-restore.md)

## 已知限制

- 当前公网环境使用 HTTP，交付生产环境前应配置域名、HTTPS，并将 `SESSION_COOKIE_SECURE` 设置为 `true`。
- 当前没有自动化测试套件，现阶段采用编译构建、接口健康检查和角色业务流程手工验证。
- Kubernetes 使用本地镜像和静态本地 PV，适合双节点学习集群，不是生产高可用方案。
- 完整审计日志、通知中心和附件上传不在本次 Challenge 范围内。
