# 第一次训练 PR：从领取任务到合并

本指南面向第一次正式使用 GitHub PR 的新人。你需要 Git、自己的 GitHub 账号、仓库读取和推送训练 Branch 的权限。权限不足时联系负责人，不要改为直接提交 main。

以下示例使用批次 `2026-11`、用户名 `lijingkang`、题目 `CH-006`；请替换为负责人提供的批次、你的 GitHub 用户名和实际 Challenge，不要照抄他人的目录。

## 1. 获取 Training ID

从 DevStart 或训练负责人处获得自己的 Training ID，记录在项目 README 和 PR 的 Training 区域。Training ID 用于关联训练过程与工程作品，不是密码。

## 2. 获取 Challenge

在 DevStart 按训练流程获取并确认题目，记录 Challenge Code、名称和 Version。阅读公开题卡，澄清需求后再动手；不要把 Reviewer 私有题库或隐藏验收内容复制进作品。

确认批次标识以及负责 Review 的导师。如果不清楚要求，先与负责人沟通。

## 3. Clone Repository

```sh
git clone https://github.com/bluedot-ai/devstart-labs.git
cd devstart-labs
git status
```

先阅读根目录 README、CONTRIBUTING 和 trainees/README。GitHub 推送认证请按团队批准的方式配置；不要把 Token 写进 Git remote URL、脚本或 README。

## 4. 创建 Branch

在没有未提交改动的情况下，以最新 main 为起点：

```sh
git switch main
git pull --ff-only origin main
git switch -c trainee/lijingkang/CH-006
git branch --show-current
```

最后一条应显示你的 `trainee/...` Branch，不能是 main。每人独立 Branch；不要共享另一个人的开发 Branch。

如果 Branch 已存在，用 `git switch trainee/lijingkang/CH-006` 切回继续工作，不要重复创建。切换前先保存当前改动，遇到不理解的冲突联系导师，不要盲目强制操作。

## 5. 创建个人 Challenge 目录

```sh
mkdir -p trainees/2026-11/lijingkang/CH-006/frontend
mkdir -p trainees/2026-11/lijingkang/CH-006/backend
mkdir -p trainees/2026-11/lijingkang/CH-006/deploy
mkdir -p trainees/2026-11/lijingkang/CH-006/docs
```

自行创建项目 README 和 `docs/design.md`，再按 Challenge 开发。仓库不会预建真实新人目录。每个 Challenge 独立目录，保留 frontend/backend/deploy/docs/README 的基本边界；Git 不跟踪空目录，加入实际文件后才会出现在提交中。

## 6. 开发

- 先整理需求与技术设计，再逐步实现和验证。
- MySQL 初始化、Docker 和 Kubernetes 配置随作品提交，注明命令执行目录。
- README 写清前后端启动、配置项、数据库初始化、测试、镜像构建、部署与验证方式。
- 测试覆盖正常、异常和边界场景，记录实际结果。
- 理解并人工验证 AI 生成的代码，记录 AI 的帮助、错误建议及人工修改。
- 使用 `.env.example`、`application-example.yml` 和 placeholder 描述配置，真实值只在环境变量或本地忽略文件中提供。

不得提交 Database Password、API Key、Token、Private Key、Kubeconfig、Cloud Credential；日志和截图也要检查。不要提交大型二进制文件、依赖目录、数据库备份或镜像归档。

## 7. Commit

如果 Git 提示尚未配置提交身份，先在本仓库配置姓名及 GitHub 已验证邮箱（或 GitHub 提供的 noreply 邮箱），将下面示例替换为自己的信息：

```sh
git config user.name "你的显示名"
git config user.email "你的 GitHub 已验证邮箱或 noreply 邮箱"
```

配置的是提交身份，不是登录凭据；不要把 Token 填入邮箱或姓名。

先检查，再按自己的目录暂存：

```sh
git status
git diff
git add trainees/2026-11/lijingkang/CH-006/
git diff --cached
git commit -m "feat(CH-006): implement GPU application workflow"
```

确认暂存内容没有凭据、无关文件或其他人的作品后再 Commit。采用 `feat:`、`fix:`、`docs:`、`test:`、`refactor:`、`chore:`；不要使用 `update`、`111`、`test`、`修改一下` 等消息。

## 8. Push

```sh
git push -u origin trainee/lijingkang/CH-006
```

后续在同一 Branch 上的普通推送可用 `git push`。不得 push main，不要使用 force push 绕过问题。

## 9. Create PR

1. 打开 [devstart-labs](https://github.com/bluedot-ai/devstart-labs) 的 Pull requests 页面。
2. 选择 **New pull request**，base 为 `main`，compare 为自己的 `trainee/...` Branch。
3. 标题说明 Challenge 与结果，例如 `CH-006: GPU application workflow`。
4. 填写自动载入的 PR 模板：Training ID、Challenge、Trainee、完成项、设计链接、测试、部署、AI Coding 和待关注问题。
5. 只勾选实际完成且验证的项；未完成或不适用时说明原因。
6. 未准备好时可以先开 Draft PR；准备好后转为可 Review，并选择负责人为 Reviewer（无权限时请负责人处理）。

## 10. Respond Review

阅读每条评论。Reviewer 使用 `[BLOCKER]`、`[MAJOR]`、`[SUGGESTION]` 标记问题。回复你对问题的理解和处理计划；不理解就提问，不要只回复“已改”或静默忽略。

Review 不仅检查格式，还检查需求、数据库、状态机、异常、API、测试、Docker、Kubernetes、工程结构以及你是否理解 AI 生成的代码。

## 11. 修改

在同一 Branch 修复，执行相关测试并更新文档，再提交、推送：

```sh
git status
git add trainees/2026-11/lijingkang/CH-006/
git diff --cached
git commit -m "fix(CH-006): reject duplicate approval"
git push
```

同一个 PR 会自动显示新提交，不必为每轮 Review 新开 PR。逐条回应修改位置、提交与验证结果，申请重新 Review；由 Reviewer 复核后确认讨论已解决。

## 12. Approve

由 Reviewer 审查并执行 **Approve**，不是新人替自己批准。BLOCKER 必须解决；MAJOR 的处理或例外必须获得 Reviewer 明确认可。批准后如继续修改，应请 Reviewer 再次确认，不能绕过新问题直接合并。

## 13. Merge

Review 通过、要求的问题处理完成、必要自测成功后，由有权限的维护者合并。推荐 **Squash Merge**，使用清楚的提交标题，保持 main 历史易读。

新人不要在未通过 Review 时自行 Merge。合并后先确认本地工作区没有未提交改动，再同步：

```sh
git switch main
git pull --ff-only origin main
```

是否删除已合并 Branch 按团队安排处理；保留训练目录与 PR 历史作为实践记录。

V0.1 不强制统一 CI；你的项目仍必须在 README 中提供可复现的 build、test、docker build 和部署命令。
