# 新人训练作品目录

本目录只存放新人训练作品。不要提前创建真实新人目录；由新人在自己的 Branch 中创建。

## 路径

```text
trainees/<batch>/<github-username>/<challenge-code>/
```

例如 `trainees/2026-11/lijingkang/CH-006/`，仅为示例，不代表仓库已经创建该新人目录。批次标识由负责人告知；使用本人真实 GitHub 用户名和正式 Challenge Code。

每个 Challenge 独立目录。即使同一个人做不同 Challenge，也应分别创建目录。不要把作品混入仓库根目录，也不要修改其他新人的作品。

## 建议结构

```text
CH-006/
├── frontend/
├── backend/
├── deploy/
├── docs/
│   └── design.md
└── README.md
```

内部可以按实际技术组织，但保留以下基本边界：

- `frontend/`：前端源码与依赖描述，不提交 node_modules 或构建产物。
- `backend/`：后端源码、依赖描述与必要数据库迁移；真实数据库密码不入库。
- `deploy/`：Docker 镜像构建相关文件或说明、Kubernetes Deployment / Service 清单；Dockerfile 也可随构建上下文放在 frontend/backend，在此处说明位置。
- `docs/`：设计、测试、部署等工程文档，建议以 `docs/design.md` 保存技术设计。
- `README.md`：项目入口，必须包含 Training ID、Challenge、项目说明、目录、环境要求、配置示例、数据库初始化、前后端启动、测试、Docker 构建、Kubernetes 部署及验证、已知限制、AI Coding 使用说明或对应文档链接。

配置示例使用 `.env.example`、`application-example.yml` 或明确 placeholder。示例必须可供读者理解所需配置，但不能携带真实凭据。

空目录不会被 Git 跟踪；按开发实际加入源码、配置或说明，不需要为了占位提交无意义文件。

## 交付路径

Branch `trainee/<github-username>/<challenge-code>` → 有意义的 Commit → Push 自己的 Branch → PR 到 main → 响应 Review → 修改 → Reviewer Approve → 维护者 Squash Merge。

阅读 [完整操作指南](../docs/trainee-guide.md) 和 [贡献规范](../CONTRIBUTING.md)。
