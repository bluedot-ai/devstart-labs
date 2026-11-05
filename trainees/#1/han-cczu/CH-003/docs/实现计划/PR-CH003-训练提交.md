# CH-003：训练作品提交

| 项目 | 内容 |
|---|---|
| Training ID | `TRAINING-f45fd5e0-4e9c-44b4-819e-594b781da966` |
| 批次 / Trainee | `#1` / `han-cczu` |
| Challenge | `CH-003` · 培训课程管理系统 · Version `1.0` |
| 本地分支 | `trainee/han-cczu/CH-003` |
| Fork / 目标仓库 | `han-cczu/devstart-labs` / `bluedot-ai/devstart-labs:main` |
| 作品目录 | `trainees/#1/han-cczu/CH-003/` |
| 源码快照 | `han-cczu/TrainingCourseManagementSystem` 的 `ed4ea92fbf816089014dfb36dc6c8a854801d569` |

## 范围

以训练仓库最新 `main` 为基础，导入既有作品，只调整训练信息、项目入口和复验记录。业务代码、Flyway 迁移、构建配置保持源码快照内容。保留原开发仓库的 PR / CI 链接作为开发过程记录，不在训练仓库根目录新增工作流。

## 任务与状态

- [x] 阅读训练仓库 README、CONTRIBUTING、trainee-guide、trainees/README 和 PR 模板。
- [x] 克隆本人 Fork，配置 `upstream` 并从上游 `main` 创建训练分支。
- [x] 根据用户提供的信息填写 Training ID、批次、Challenge 编号及版本。
- [x] 仅导入已提交快照中的文件，排除本地真实 `secret.env`、依赖目录、构建产物、备份与镜像归档。
- [x] 保留 frontend、backend、deploy、docs、README 和 e2e 的工程边界。
- [x] 更新 README、自测说明、技术设计、AGENTS 和实现计划总览中的训练提交信息。
- [x] 从同一源码快照复验后端测试、前端类型检查和构建。
- [x] 验证 Compose 配置可解析。
- [x] 检查最终暂存范围、凭据风险、Maven Wrapper 可执行位与文档链接。
- [x] 完成本地提交内容和 PR 模板材料。
- [ ] 推送 Fork 的训练分支，并向上游 `main` 创建 PR。
- [ ] 逐条响应 Review，获得 Reviewer Approve，由维护者合并。

## 本次验证（2026-11-05）

验证使用从上述 Git 提交导出的相同源码，执行目录为本作品的项目根目录。业务源码和构建配置未修改；本次新增和调整的是训练交付文档。

最终暂存 210 个文件，全部位于个人作品目录；核对了原快照的 209 个文件，仅训练交付文档有内容调整，`backend/mvnw.cmd` 按 `.gitattributes` 规范化换行。37 个相对文档链接存在，`backend/mvnw` 保留 `100755` 可执行位；未发现禁止文件或常见真实凭据格式。PR 模板材料已在本地准备，尚未推送或创建上游 PR。

| 检查 | 命令 | 结果 |
|---|---|---|
| 后端 | `cd backend; .\mvnw.cmd test` | 95 个测试，0 失败、0 错误、0 跳过，BUILD SUCCESS；约 1 分 37 秒 |
| 前端依赖 | `cd frontend; pnpm install --frozen-lockfile` | 通过 |
| 前端类型与构建 | `cd frontend; pnpm build` | vue-tsc 与 Vite 构建通过 |
| Compose 配置 | `docker compose -f deploy/docker-compose.yml config --quiet` | 通过，相对构建路径可解析 |

正常、异常和边界用例的详细说明见 [自测说明](../自测说明.md) 和 [本地测试复查](../本地测试复查-2026-11-04.md)。本次后端复验包含并发抢占容量、权限、状态转换及请求边界等现有用例。

## UI 与部署验证边界

- 本次未重新执行 UI 自测；2026-11-04 的 27 项 UI 自测及 3 项运行器检查是历史结果。
- 本次未重新构建 Docker 镜像或发布生产环境。部署清单、镜像构建及验证命令见 [部署手册](../部署手册.md)。
- 历史部署记录为 2026-11-04 发布 `e8c05d3`，早于当前源码的后续修复和界面调整；本次未检查线上实际版本。
- 原源码快照包含独立开发仓库 PR #13 中的文档更新，不影响业务源码。相关记录见 [AI Coding 使用说明](../AI-Coding使用说明.md)。

## PR 完成标准

本地准备完成后，提交 `feat(CH-003): 提交培训课程管理系统训练作品`。PR 标题为 `CH-003: 培训课程管理系统（Version 1.0）`，填写上游 PR 模板，设计、测试、部署和 AI 使用说明均链接至本作品目录。最终合入以导师 Review 和上游维护者操作为准；本地检查通过不代表已经提交或验收。
