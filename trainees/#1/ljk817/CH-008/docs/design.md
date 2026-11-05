# 技术设计

## 1. 目标

系统用于建立“客户提交问题—客服分配与处理—双方公开沟通—问题关闭”的业务闭环，同时隔离客服内部备注和不同客户公司的数据。

## 2. 系统架构

```text
浏览器
  │ HTTP
  ▼
Nginx + Vue 前端
  │ /api 反向代理
  ▼
Spring Boot 后端
  │ Spring Data JPA
  ▼
MySQL 8.4 ── Docker Volume / Kubernetes PVC
```

单服务器环境由 Docker Compose 管理前端、后端和 MySQL。Nginx 提供静态页面并代理 `/api`；后端负责认证、授权、业务规则和数据访问；Flyway 管理数据库版本。

## 3. 核心数据关系

- `Customer`：客户公司档案。
- `SystemUser`：系统账号；客户账号可绑定一个 `Customer`。
- `CustomerIssue`：问题主记录，关联客户、分类、负责人、状态和严重程度。
- `IssueRecord`：处理记录，区分客户可见的公开回复和仅内部可见的备注。
- `IssueCategory`：可配置的问题分类。

一个客户可绑定多个客户用户并拥有多个问题；一个问题可包含多条处理记录。

## 4. 权限设计

后端通过 Spring Security 维护服务端会话，并在业务服务中继续校验对象归属：

- `ADMIN` 可管理账号并访问全部业务数据。
- `AGENT` 可处理全部客户和问题，但不能管理系统账号。
- `VIEWER` 只能读取内部数据。
- `CUSTOMER` 只能访问绑定客户的数据，不能读取内部备注。

这种设计同时防止功能越权和通过修改 URL、请求参数产生的对象级越权。

## 5. 问题生命周期

主要状态为待处理、处理中、已解决和已关闭。处理人员可以分配负责人、调整严重程度、更新状态并追加处理记录；客户可以提交问题和补充公开信息。

## 6. 安全设计

- 使用 HttpOnly、SameSite Cookie 保存会话。
- 使用 CSRF 防护保护写操作。
- 密码使用 Spring Security 密码编码器保存，不保存明文。
- 首次管理员密码通过环境变量注入且要求至少 12 位。
- 真实数据库密码和 Kubernetes Secret 不进入 Git。

## 7. 持久化与部署

Docker Compose 使用命名卷保存 MySQL 数据，并提供 `mysqldump` 备份与恢复脚本。Kubernetes 方案使用 StatefulSet、静态 PV/PVC 和 `Retain` 回收策略。当前公网环境使用 Docker Compose；Kubernetes 清单用于 VMware 学习集群的后续迁移实践。
