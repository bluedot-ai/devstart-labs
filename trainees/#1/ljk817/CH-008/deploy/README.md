# 部署文件说明

为保持 Docker 构建上下文简单，本项目的部署文件按以下位置组织：

- `../compose.yaml`：单服务器 Docker Compose 编排；构建 `../frontend` 和 `../backend`。
- `../frontend/Dockerfile`：Vue 前端构建与 Nginx 运行镜像。
- `../backend/Dockerfile`：Spring Boot 构建与 Java 运行镜像。
- `../k8s/`：Kubernetes Deployment、Service、StatefulSet、ConfigMap、PV/PVC 和 Kustomize 配置。
- `../scripts/`：MySQL 备份与恢复脚本。

实际使用方式见项目根目录 `README.md`、`k8s/README.md` 和 `docs/backup-restore.md`。
