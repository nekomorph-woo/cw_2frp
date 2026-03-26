# 单元测试规则

> 本规则适用于 IntelliJ Platform 插件开发项目

## 核心原则

**所有纯单元测试必须在独立的 `unit-test` 子模块中运行，禁止使用主模块的 `src/test` 目录。**

这是为了避免 IntelliJ Platform Gradle Plugin 的 JUnit5TestSessionListener 干扰，保证单元测试的纯洁性和可独立运行性。

## 项目结构

```
project/
├── src/main/kotlin/
├── unit-test/                      # 独立单元测试子模块
│   ├── build.gradle.kts
│   └── src/test/kotlin/
│       ├── *Test.kt
├── build.gradle.kts               # 主模块配置
└── settings.gradle.kts            # 包含子模块
```

## 运行测试

```bash
# 设置 JAVA_HOME（IDEA 下载的 Java）
export JAVA_HOME=/Users/chaooswoo/Library/Java/JavaVirtualMachines/ms-21.0.10/Contents/Home

# 运行纯单元测试
./gradlew :unit-test:test
```

## TDD 流程

1. **RED**: 在 `unit-test/src/test/kotlin/` 编写失败的测试
2. **GREEN**: 在 `src/main/kotlin/` 编写最小实现
3. **REFACTOR**: 重构代码，确保测试仍通过
4. **重复**: 下一个测试用例

## 纯逻辑模块标准

可放入 `unit-test` 测试的模块：
- 不依赖 `com.intellij.*`
- 不依赖 `org.jetbrains.jewel.*`
- 不依赖其他 IntelliJ Platform API

需要 IntelliJ API 的模块测试：
- 在 IDE 中运行
- 或使用 `src/test` 目录（需要 IDE 运行）

## 编写测试的原则

1. **测试行为，而非实现** - 测试公共接口的预期行为
2. **一次一个测试** - TDD 循环：RED → GREEN → REFACTOR
3. **最小实现** - 只写让当前测试通过的最小代码
4. **避免 mock 内部实现** - 不要与实现细节耦合
