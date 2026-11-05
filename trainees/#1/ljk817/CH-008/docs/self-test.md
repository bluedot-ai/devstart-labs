# 自测说明

## 验证环境

- 云服务器：Ubuntu 22.04、Docker、Docker Compose
- 运行组件：Nginx/Vue、Spring Boot、MySQL 8.4
- 浏览器：Chrome / Edge

## 构建与启动

执行：

```bash
docker compose up -d --build
docker compose ps
```

实际结果：前端和后端镜像构建成功；MySQL 容器状态为 `Healthy`；后端和前端容器状态为 `Started/Up`；后端日志显示 Tomcat 在 8080 端口启动完成。

## Normal Case

- 管理员登录并查看工作台统计。
- 管理员创建处理人员、只读用户和绑定客户的客户用户。
- 处理人员创建客户和问题、分配负责人、更新状态、增加公开回复和内部备注。
- 客户用户查看所属公司的问题并补充公开信息。
- 同一公司的多个客户账号可以查看公司共享的问题。
- 执行 MySQL 备份脚本，成功生成 `.sql.gz` 文件。

结果：以上流程均完成手工验证。

## Error Case

- 错误账号或密码登录时返回拒绝响应。
- 未登录访问受保护接口时返回未授权响应。
- `CUSTOMER` 请求其他客户的问题时被后端拒绝。
- 首次启动未提供至少 12 位管理员密码时，后端停止启动并输出明确错误。

结果：错误被拦截，没有产生越权数据修改。

## Boundary Case

- `VIEWER` 可以查看客户和问题，但页面不显示新增、编辑操作，后端也不允许写入。
- 容器重建后，MySQL 命名卷中的客户和问题数据仍然存在。
- `docker compose down` 后重新启动，未删除命名卷时数据仍然存在。
- 客户账号只能读取公开回复，不能读取内部备注。

结果：权限边界和数据持久化符合设计。

## Kubernetes 说明

已在 VMware 中完成 kubeadm 双节点集群及 Calico 网络的基础搭建，并提交 Deployment、Service、StatefulSet、PV/PVC 等清单。本次公网业务部署未迁移到 Kubernetes，因此不把“应用已在 Kubernetes 运行”列为已完成验收项。

## 当前测试限制

项目暂未建立自动化单元测试和端到端测试套件。PR 提交前主要依据 Maven/npm 编译构建、Docker Compose 启动结果、健康检查和上述业务流程手工验证。
