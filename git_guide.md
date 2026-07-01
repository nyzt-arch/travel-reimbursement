# Git 常用指令快捷指南

本指南整理了在项目开发中，针对 GitHub 仓库管理的 4 种最常见场景下的 Git 完整命令流程。

---

## 📂 场景 1：在 GitHub 新建了空白仓库，想上传当前本地文件夹的所有文件

适用于**新项目第一次上传**（确保 GitHub 上的仓库是新建且完全空白的）：

```bash
# 1. 进入本地文件夹根目录，初始化本地 Git 仓库
git init

# 2. 将当前文件夹下的所有文件添加到暂存区
git add .

# 3. 提交文件到本地仓库并备注
git commit -m "first commit: 初始化项目"

# 4. 重命名本地默认分支为 main
git branch -M main

# 5. 关联 GitHub 远程仓库 (替换 <仓库链接> 为你在 GitHub 复制的链接)
git remote add origin <你的GitHub仓库链接>

# 6. 推送本地代码到远程仓库并建立跟踪分支 (首次推送需加 -u)
git push -u origin main
```
> **提示**：如果远程仓库不为空导致推送冲突而失败，可在最后一步使用强制推送：
> `git push -f -u origin main`

---

## 🔄 场景 2：已有仓库并且已绑定，修改了代码，想更新上传

适用于**日常开发中，修改或新增文件后的常规代码同步更新**：

```bash
# 1. (可选) 查看哪些文件被修改或新增了，确认当前工作区状态
git status

# 2. 将所有修改和新增的文件添加到暂存区
git add .

# 3. 提交修改到本地仓库，并写明这次改了什么
git commit -m "update: 描述你本次修改了什么功能"

# 4. (推荐) 在推送前先拉取远程最新代码进行合并，防止多人协作冲突
git pull origin main

# 5. 将本地提交的更新推送到远程仓库
git push origin main
```

---

## 🔀 场景 3：本地项目已经关联了 A 仓库，现在想改投上传到 B 仓库

适用于**更换 GitHub 账号、更换新仓库，或者将项目备份到另一个新地址**：

```bash
# 1. (可选) 查看当前项目关联的是哪一个旧仓库地址
git remote -v

# 2. 移除旧仓库的关联 (切断与旧仓库的联系)
git remote remove origin

# 3. 关联新的远程仓库 B 地址
git remote add origin <新的远程仓库链接>

# 4. 确保本地主分支为 main
git branch -M main

# 5. 将代码推送到新仓库并建立新的旧跟踪关系
git push -u origin main
```

---

## 🎯 场景 4：修改了许多文件，但只想上传当前文件夹下的“某些特定文件”

适用于**只想提交局部修改（如只更新文档或个别配置文件），其他未写完的代码暂不提交**：

```bash
# 1. (可选) 查看当前所有被修改的文件路径
git status

# 2. 仅添加你想要提交的文件路径（多个文件用空格隔开）
# 比如：只提交项目下的 README.md 和 sql.txt
git add README.md sql.txt

# 3. (可选) 再次查看状态，确认只有上述指定文件变成了绿色（已进入暂存区）
git status

# 4. 提交暂存区中的特定文件到本地仓库
git commit -m "docs: 仅更新说明文档与数据库脚本"

# 5. 将这部分特定的提交推送至 GitHub 远程仓库
git push origin main
```
