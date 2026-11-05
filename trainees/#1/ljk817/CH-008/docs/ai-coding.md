# AI Coding 使用说明

## 使用工具

- OpenAI Codex

## AI 主要帮助完成

- 梳理客户问题跟踪的领域模型、角色和接口边界。
- 生成并迭代 Vue 页面、Spring Boot API、数据库迁移和权限控制代码。
- 编写 Dockerfile、Docker Compose、Kubernetes 清单和 MySQL 备份脚本。
- 协助分析部署日志、编译错误、登录失败和环境变量问题。
- 整理 README、技术设计、自测和部署说明。

## 曾出现的错误或不合适建议

- 上传代码时曾遗漏 `ActiveUpdateRequest`，导致后端出现 `cannot find symbol` 编译错误；补齐缺失文件后重新构建通过。
- 初始管理员密码虽然写入本地文件，但 Compose 环境变量未正确读取，导致首次启动校验失败；通过检查 `.env` 键名和 Compose 插值后修复。
- 曾考虑把当前公网系统直接迁移到 Kubernetes，但结合单机学习项目的规模和运维成本，最终保留 Docker Compose 作为当前运行方案，把 Kubernetes 作为独立学习环境。

## 人工确认和修改

- 根据实际业务重新确认 `ADMIN`、`AGENT`、`VIEWER`、`CUSTOMER` 四类角色，而不是直接接受生成结果。
- 增加客户对象级数据隔离，确保客户不能通过接口参数读取其他公司问题和内部备注。
- 对 Docker 数据卷、备份恢复、首次管理员初始化和 Cookie 配置进行了实际部署验证。
- 根据云服务器和 VMware 环境调整部署方案，并在文档中明确区分“已上线的 Docker Compose 环境”和“未承载线上业务的 Kubernetes 学习环境”。

所有 AI 生成内容均经过代码检查、编译构建或手工业务流程验证；发现问题后根据日志和实际需求进行修改。
