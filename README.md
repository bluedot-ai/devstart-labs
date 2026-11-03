# BlueDot DevStart Labs V0.1

研发新人训练项目、GitHub Pull Request（PR）与 Code Review 的真实实践仓库。本仓库用于提交新人训练作品，不是 DevStart 平台产品源码。

- 产品前端：[devstart-ui](https://github.com/bluedot-ai/devstart-ui)
- 产品后端：[devstart-backend](https://github.com/bluedot-ai/devstart-backend)
- 新人从这里开始：[训练操作指南](docs/trainee-guide.md)
- 提交前阅读：[贡献与 Review 规范](CONTRIBUTING.md)

## 作品目录

```text
trainees/<batch>/<github-username>/<challenge-code>/
```

例如：`trainees/2026-11/lijingkang/CH-006/`。`batch` 使用负责人分配的批次标识（示例为 `2026-11`），`github-username` 使用本人 GitHub 用户名，`challenge-code` 使用正式题目编号。

个人目录由新人在自己的 Branch 中创建；仓库不预建真实新人目录。每个 Challenge 独立目录，作品文件不要直接放在仓库根目录。

建议结构：

```text
CH-006/
├── frontend/
├── backend/
├── deploy/
├── docs/
│   └── design.md
└── README.md
```

不要求所有项目内部完全一致，但应保留 frontend、backend、deploy、docs、README.md 的基本边界。详细目录要求见 [trainees/README.md](trainees/README.md)。

## 工作流

```text
Branch → Commit → Push → Pull Request → Review → 修改 → Approve → Merge
```

- 推荐 Branch：`trainee/<github-username>/<challenge-code>`，例如 `trainee/lijingkang/CH-006`。
- 不允许新人直接 push main；每人使用独立 Branch，所有作品通过 PR。
- Commit 使用基础 Conventional Commit：`feat:`、`fix:`、`docs:`、`test:`、`refactor:`、`chore:`。
- 示例：`feat(CH-006): implement GPU application workflow`。
- 禁止无意义消息：`update`、`111`、`test`、`修改一下`。
- 创建 PR 时填写仓库自动提供的 [PR 模板](.github/pull_request_template.md)，关联 Training ID 与 Challenge。
- 必须响应 Review；未通过 Review 不得 Merge。建议使用 **Squash Merge**，由有权限的维护者合并。

## Review 重点

Review 使用统一前缀 `[BLOCKER]`、`[MAJOR]`、`[SUGGESTION]`，方便后续版本解析。

例如：`[BLOCKER] Approval can be executed repeatedly and may deduct GPU capacity twice.`

重点检查需求理解、数据库设计、状态机、异常处理、API 设计、测试、Docker、Kubernetes、工程结构，以及新人是否理解 AI 生成的代码。格式、缩进和命名只是 Review 的一部分。

## 凭据与交付

不得提交 Database Password、API Key、Token、Private Key、Kubeconfig、Cloud Credential。使用 `.env.example`、`application-example.yml` 和明确的 placeholder 描述配置；真实值通过环境变量或本地忽略文件提供。

不要提交大型二进制文件、构建产物、数据库备份或容器镜像归档。完整 README、Dockerfile、Kubernetes Deployment / Service 等配置必须随作品入库，配置中不得包含真实凭据。

## V0.1 边界

本轮仅初始化仓库规范，不包含业务系统代码。不强制统一 CI，不创建复杂 GitHub Actions；V0.2 再考虑 frontend build、backend build、test、docker build。本仓库文档规定 PR 和审批流程，本轮不修改 Repository Settings 或分支保护规则。
