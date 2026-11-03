# 贡献与 Code Review 规范

## 提交规则

1. 不允许直接向 main push。所有训练作品必须使用 Pull Request。
2. 每人使用独立 Branch，推荐 `trainee/<github-username>/<challenge-code>`。
3. 每个 Challenge 使用独立目录：`trainees/<batch>/<github-username>/<challenge-code>/`。不要修改他人的作品目录。
4. PR 必须指向 main，填写 Training ID、Challenge、Trainee，以及 PR 模板中适用的设计、测试、部署和 AI Coding 内容。
5. 必须逐条响应 Review Comment：说明处理方式、对应提交和验证结果；不采纳时说明理由并与 Reviewer 达成一致。
6. Review 未通过不得 Merge。修改后请求重新 Review，由 Reviewer 确认问题处理结果。
7. 不得提交 Password、Token、API Key、Kubeconfig、Private Key 或 Cloud Credential。
8. 不得提交大型二进制文件、依赖目录、构建产物、数据库备份和镜像归档；需要分享的交付物放在负责人批准的制品位置，在 README/PR 中提供访问方式。
9. Challenge README 必须完整，确保其他人能够按说明配置、启动、验证项目。
10. Docker/Kubernetes 配置必须进入仓库，至少包含镜像构建说明、Deployment / Service 清单，凭据只使用 placeholder 或外部注入。

首次操作参见 [trainee-guide.md](docs/trainee-guide.md)。本次规范初始化提交是仓库引导；后续新人作品一律走上述 PR 流程。

## Commit

支持：`feat`、`fix`、`docs`、`test`、`refactor`、`chore`。可以带 Challenge scope。

```text
feat(CH-006): implement GPU application workflow
fix(CH-006): reject duplicate approval
test(CH-006): cover insufficient capacity
docs(CH-006): explain local startup and deployment
```

一条 Commit 表达一组清楚的改动。不要使用 `update`、`111`、`test`、`修改一下` 等无法说明内容的消息。提交前检查 `git status` 和 `git diff --cached`，只暂存自己的作品相关文件。

## PR

- 标题描述 Challenge 和本次结果，例如 `CH-006: GPU application workflow`。
- 使用 [.github/pull_request_template.md](.github/pull_request_template.md)，不要删除未完成项来掩盖缺失；确实不适用的内容写明原因。
- 设计链接指向仓库内文件，例如 `trainees/2026-11/lijingkang/CH-006/docs/design.md`。
- 测试说明包含实际执行的命令、结果与 Normal / Error / Boundary Case；不要仅写“已测试”。
- 部署结果应可复核；未成功的部署和未解决问题应明确披露。
- Review 修改继续提交到同一 Branch，同一个 PR 会自动更新；不必为每条评论另开 PR。

## Review Severity

所有 Review 意见以以下前缀开头：

| 前缀 | 含义 | 处理要求 |
|---|---|---|
| `[BLOCKER]` | 阻止验收的业务正确性、安全或关键流程问题 | 合并前必须解决并由 Reviewer 确认 |
| `[MAJOR]` | 重要的设计、工程或测试缺陷 | 原则上合并前解决；有例外须由 Reviewer 明确认可并记录原因 |
| `[SUGGESTION]` | 可讨论的改善建议 | 回应采纳情况和理由，由 Reviewer 判断是否影响批准 |

```text
[BLOCKER] Approval can be executed repeatedly and may deduct GPU capacity twice.
[MAJOR] The test suite does not cover insufficient GPU capacity.
[SUGGESTION] Extract the capacity validation into a named function.
```

Reviewer 重点检查：

- 需求理解与边界是否清楚；
- 数据库关系、约束与数据完整性；
- 状态机、重复操作、异常和边界处理；
- API 语义、校验及错误反馈；
- 正常、异常、边界测试是否真正执行；
- Docker 是否可构建，Kubernetes 配置是否可部署；
- 工程边界与维护性；
- 新人是否能解释、验证并维护 AI 生成的代码。

不要只检查格式、缩进和命名。新人可以指出与意见不同的判断，但不能静默忽略评论。Reviewer 应在复核修改后解决相应讨论并明确 Approve。

## Secret 与文件

禁止提交：Database Password、API Key、Token、Private Key、Kubeconfig、Cloud Credential，以及在日志、截图、测试数据、连接 URL 中出现的同类真实凭据。

- `.env.example`、`application-example.yml` 仅包含配置项和 `REPLACE_ME` 等 placeholder。
- 本地真实配置使用被忽略的文件或环境变量；不要把实际 Kubernetes Secret 值写入清单。Base64 不是脱敏。
- `.gitignore` 是辅助措施，不能代替逐项检查；即使文件未被忽略，真实凭据也不得提交。
- 如误提交凭据，立即停止传播并通知负责人，撤销/轮换凭据；只删除当前文件不能移除 Git 历史中的泄露，历史处理由维护者协调。
- 无统一大小阈值；大型二进制文件一律先与负责人确认外部存放方式，不以压缩、改后缀绕过规则。

## Merge 与 CI

建议使用 **Squash Merge**，将一个训练 PR 合并为清楚的提交。合并前重新确认 Review 已 Approve、要求的问题已处理、必要自测已通过；由有合并权限的维护者操作，新人不要自行绕过审批。

V0.1 不要求统一 CI，也不新增复杂 GitHub Actions。每个项目需在 README 中说明自己的 build/test/docker build 命令；后续版本再统一自动检查。本轮不修改复杂 Repository Settings，文档本身不等同于已配置分支保护。
