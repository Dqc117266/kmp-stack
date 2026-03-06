# KMP-Stack 项目开发规范 (Simplified Clean Architecture)

## 1. 架构原则
本项目采用 **KMP + Clean Architecture + MVI** 架构。
- **逻辑共享最大化**：90% 的业务代码、数据流、状态管理必须位于 `commonMain`。
- **单向数据流 (MVI)**：UI 状态由 `State` 驱动，用户操作通过 `Intent` 传递。
- **接口隔离**：Domain 层不依赖任何技术实现（Ktor/SQLDelight），仅定义抽象。

## 2. 核心分层与包结构
所有功能模块位于 `feature-{name}` 中，结构如下：

```text
feature-{name}/src/commonMain/kotlin/.../
├── data/
│   ├── model/           # DTOs (@Serializable)
│   └── repository/      # Repository 实现类
├── domain/
│   ├── model/           # Entities (纯 Kotlin 类)
│   ├── repository/      # 接口 (Interface)
│   └── usecase/         # 业务逻辑单元 (invoke)
└── presentation/
    ├── {Name}Contract   # State, Intent, Effect
    ├── {Name}ViewModel  # 状态持有者
    └── {Name}Screen     # Compose UI 界面