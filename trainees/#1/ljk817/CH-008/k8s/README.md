# Kubernetes 部署

这套清单用于当前 VMware 双节点学习集群：

- 控制节点：`k8s-master`（192.168.217.134）
- 工作节点：`k8s-worker`（192.168.217.136）
- 命名空间：`customer-tracker`
- Windows 访问地址：<http://192.168.217.136:30080>

应用和 MySQL 固定调度到 `k8s-worker`。这是为了使用已导入到 worker 的本地镜像，以及没有 StorageClass 时的静态本地 PV。该方案适合学习和验收，不用于多节点生产高可用。

## 资源组成

- 前端：Deployment + NodePort Service
- 后端：Deployment + ClusterIP Service
- MySQL：StatefulSet + Headless Service
- 配置：ConfigMap
- 密码：运行时创建的 Secret（不提交到 Git）
- 数据：静态 PV + PVC，回收策略为 `Retain`

## 1. 导入镜像

在已经构建镜像的 Docker 主机导出：

```bash
docker save customer-issue-tracker-backend:latest customer-issue-tracker-frontend:latest mysql:8.4 | gzip > customer-tracker-images.tar.gz
```

将压缩包传到 `k8s-worker` 后执行：

```bash
gzip -dc customer-tracker-images.tar.gz | sudo ctr -n k8s.io images import -
sudo ctr -n k8s.io images list | grep -E 'customer-issue-tracker|mysql.*8.4'
```

## 2. 创建命名空间和 Secret

在 `k8s-master` 的本目录执行：

```bash
kubectl apply -f namespace.yaml
```

使用交互输入避免密码进入 Shell 历史：

```bash
read -rsp "MySQL 应用密码: " MYSQL_PASSWORD; echo
read -rsp "MySQL root 密码: " MYSQL_ROOT_PASSWORD; echo
read -rp "初始管理员账号 [admin]: " INITIAL_ADMIN_USERNAME
INITIAL_ADMIN_USERNAME=${INITIAL_ADMIN_USERNAME:-admin}
read -rsp "初始管理员密码（至少12位）: " INITIAL_ADMIN_PASSWORD; echo
kubectl -n customer-tracker create secret generic customer-tracker-secrets \
  --from-literal=MYSQL_PASSWORD="$MYSQL_PASSWORD" \
  --from-literal=MYSQL_ROOT_PASSWORD="$MYSQL_ROOT_PASSWORD" \
  --from-literal=INITIAL_ADMIN_USERNAME="$INITIAL_ADMIN_USERNAME" \
  --from-literal=INITIAL_ADMIN_PASSWORD="$INITIAL_ADMIN_PASSWORD"
unset MYSQL_PASSWORD MYSQL_ROOT_PASSWORD INITIAL_ADMIN_USERNAME INITIAL_ADMIN_PASSWORD
```

## 3. 部署

```bash
kubectl apply -k .
kubectl -n customer-tracker get pods -w
```

所有 Pod 为 `Running` 且 `READY` 后按 `Ctrl+C` 退出观察，然后检查：

```bash
kubectl -n customer-tracker get all
kubectl -n customer-tracker get pvc
kubectl get pv customer-tracker-mysql-pv
```

## 4. 访问

Windows 浏览器打开：

```text
http://192.168.217.136:30080
```

## 5. 排错

```bash
kubectl -n customer-tracker describe pod <Pod名称>
kubectl -n customer-tracker logs statefulset/mysql
kubectl -n customer-tracker logs deployment/backend
kubectl -n customer-tracker logs deployment/frontend
```

## 6. 持久化验证

先在页面创建一条测试数据，然后删除 MySQL Pod：

```bash
kubectl -n customer-tracker delete pod mysql-0
kubectl -n customer-tracker get pods -w
```

StatefulSet 会重新创建 `mysql-0`。Pod 恢复后刷新页面，测试数据仍然存在，即完成 PVC 持久化验证。

不要删除 PV 对应的 worker 路径 `/var/lib/customer-issue-tracker/mysql`。即使 PVC 被误删，PV 的 `Retain` 策略也会保留底层数据，但重新绑定需要人工处理。
