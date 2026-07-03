# 记账本 - Android 记账软件

一款纯本地单用户的轻量级记账工具，数据不出本机，隐私零担忧。

## 功能特性

- 快速记账（3秒内完成一笔记录）
- 收支分类管理（预设8+分类，支持自定义）
- 账单列表（按日期分组，支持筛选、搜索、编辑、删除）
- 图表统计（饼图/柱状图，日/月/年视图）
- 本地数据加密（SQLCipher）
- 应用锁/生物识别保护
- 数据备份与恢复

## 技术栈

- Kotlin + Jetpack Compose
- Room + SQLCipher（数据库加密）
- DataStore（偏好设置）
- Hilt（依赖注入）
- Material 3 设计规范

## 获取 APK 的两种方式

### 方式一：GitHub Actions 自动构建（推荐，最简单）

无需安装 Android Studio，3-5 分钟即可获得 APK：

1. **注册 GitHub 账号**（免费）：https://github.com/signup
2. **创建新仓库**：
   - 登录 GitHub，点击右上角 `+` → `New repository`
   - 仓库名填 `AccountingApp`，选择 `Public`
   - 点击 `Create repository`
3. **上传代码**：
   - 在仓库页面，点击 `uploading an existing file`
   - 将本项目 ZIP 中的所有文件拖拽上传（或选择文件）
   - 点击 `Commit changes`
4. **等待自动构建**：
   - 点击上方 `Actions` 标签
   - 你会看到 `Build Android APK` 工作流正在运行
   - 等待 3-5 分钟，直到状态变为绿色 ✅
5. **下载 APK**：
   - 点击完成的构建记录
   - 页面底部找到 `Artifacts` 区域
   - 点击 `app-debug` 下载 APK 文件
   - 将 APK 传输到手机安装即可

### 方式二：本地 Android Studio 构建

需要电脑安装 Android Studio：

1. 下载并安装 Android Studio：https://developer.android.com/studio
2. 解压本项目 ZIP 文件
3. 用 Android Studio 打开项目文件夹
4. 等待 Gradle 同步完成（首次可能需要下载依赖，约5-10分钟）
5. 点击菜单 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
6. 生成的 APK 位于：`app/build/outputs/apk/debug/app-debug.apk`

## 安装到手机

1. 将 APK 文件传输到手机（微信、QQ、数据线均可）
2. 在手机上点击 APK 文件
3. 如果提示「禁止安装未知来源应用」，请前往设置开启
4. 完成安装，开始使用

## 隐私声明

- 所有数据存储在本地，不上传任何服务器
- 支持数据库加密和应用锁保护
- 无需注册登录，开箱即用

## 版本信息

- 当前版本：1.0.0
- 最低支持：Android 8.0 (API 26)
- 目标版本：Android 14 (API 34)
