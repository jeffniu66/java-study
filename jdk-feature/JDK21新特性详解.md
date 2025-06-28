# JDK 21 新特性详解

JDK 21 是继 JDK 17 之后的下一个长期支持（LTS）版本，于2023年9月发布。它包含了许多重要的新特性和改进。

## 🎯 主要新特性概览

| 特性 | JEP | 状态 | 重要性 | 描述 |
|------|-----|------|--------|------|
| 虚拟线程 | JEP 444 | ✅ 正式 | ⭐⭐⭐⭐⭐ | 轻量级线程，革命性并发编程 |
| 字符串模板 | JEP 430 | 🔄 预览 | ⭐⭐⭐⭐ | 安全的字符串插值 |
| 序列集合 | JEP 431 | ✅ 正式 | ⭐⭐⭐ | 统一的集合操作接口 |
| 模式匹配增强 | JEP 441 | ✅ 正式 | ⭐⭐⭐⭐ | switch表达式的guard条件 |
| Record模式 | JEP 440 | ✅ 正式 | ⭐⭐⭐ | Record的解构匹配 |
| 结构化并发 | JEP 453 | 🔄 预览 | ⭐⭐⭐⭐ | 更好的并发任务管理 |
| 作用域值 | JEP 446 | 🔄 预览 | ⭐⭐⭐ | ThreadLocal的现代替代 |

## 1. 虚拟线程 (Virtual Threads) - JEP 444

### 概述
虚拟线程是JDK 21最重要的特性，提供了轻量级的线程实现。

### 核心特点
- **轻量级**：每个虚拟线程只占用几KB内存
- **高并发**：可以创建数百万个虚拟线程
- **阻塞友好**：阻塞时不会占用载体线程

### 代码示例
```java
// 基本创建
Thread.ofVirtual().start(() -> {
    System.out.println("Hello Virtual Thread!");
});

// 使用执行器
try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> {
        // 任务逻辑
    });
}

// 带名称的虚拟线程
Thread.ofVirtual()
    .name("worker-thread")
    .start(() -> {
        // 任务逻辑
    });
```

### 适用场景
- I/O密集型应用
- 高并发Web服务
- 微服务架构
- 传统阻塞式编程

## 2. 字符串模板 (String Templates) - JEP 430

### 概述
提供安全、高效的字符串插值机制，替代传统的字符串拼接。

### 语法示例
```java
String name = "Alice";
int age = 25;

// 传统方式
String msg1 = "Hello, " + name + "! You are " + age + " years old.";

// 字符串模板
String msg2 = STR."Hello, \{name}! You are \{age} years old.";

// 格式化模板
String msg3 = FMT."Hello, %-10s\{name}! You are %d\{age} years old.";
```

### 模板处理器
- **STR**：基本字符串插值
- **FMT**：格式化字符串
- **RAW**：原始模板访问

### 优势
- 类型安全
- 性能优化
- 代码可读性
- 防止注入攻击

## 3. 序列集合 (Sequenced Collections) - JEP 431

### 概述
引入新的接口层次结构，为集合提供统一的序列操作。

### 新接口
```java
interface SequencedCollection<E> extends Collection<E> {
    SequencedCollection<E> reversed();
    void addFirst(E);
    void addLast(E);
    E getFirst();
    E getLast();
    E removeFirst();
    E removeLast();
}

interface SequencedSet<E> extends Set<E>, SequencedCollection<E> {
    SequencedSet<E> reversed();
}

interface SequencedMap<K,V> extends Map<K,V> {
    SequencedMap<K,V> reversed();
    SequencedSet<K> sequencedKeySet();
    SequencedCollection<V> sequencedValues();
    SequencedSet<Map.Entry<K,V>> sequencedEntrySet();
    V putFirst(K, V);
    V putLast(K, V);
}
```

### 使用示例
```java
List<String> list = new ArrayList<>();
list.addFirst("first");
list.addLast("last");
String first = list.getFirst();
String last = list.getLast();
List<String> reversed = list.reversed();
```

## 4. 模式匹配增强 - JEP 441

### 概述
为switch表达式添加guard条件，使模式匹配更加强大。

### Guard条件语法
```java
switch (obj) {
    case String s when s.length() > 5 -> "Long string: " + s;
    case String s -> "Short string: " + s;
    case Integer i when i > 0 -> "Positive number: " + i;
    case Integer i -> "Non-positive number: " + i;
    case null -> "Null value";
    default -> "Unknown type";
}
```

### 优势
- 更精确的模式匹配
- 减少嵌套if语句
- 提高代码可读性

## 5. Record模式 (Record Patterns) - JEP 440

### 概述
允许在模式匹配中直接解构Record。

### 语法示例
```java
record Point(int x, int y) {}
record Circle(Point center, int radius) {}

Object shape = new Circle(new Point(0, 0), 5);

switch (shape) {
    case Circle(Point(var x, var y), var r) -> 
        "Circle at (" + x + "," + y + ") with radius " + r;
    case Point(var x, var y) -> 
        "Point at (" + x + "," + y + ")";
    default -> "Unknown shape";
}
```

### 嵌套解构
```java
record Address(String street, String city) {}
record Person(String name, Address address) {}

switch (person) {
    case Person(var name, Address(var street, var city)) ->
        name + " lives on " + street + " in " + city;
}
```

## 6. 结构化并发 (Structured Concurrency) - JEP 453

### 概述
提供更好的并发任务管理，确保子任务的生命周期与父任务一致。

### 基本用法
```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<String> task1 = scope.fork(() -> fetchData1());
    Future<String> task2 = scope.fork(() -> fetchData2());
    
    scope.join();           // 等待所有任务
    scope.throwIfFailed();  // 如果有失败则抛出异常
    
    String result1 = task1.resultNow();
    String result2 = task2.resultNow();
}
```

### 不同的策略
```java
// 失败时关闭所有任务
StructuredTaskScope.ShutdownOnFailure scope1;

// 成功时关闭其他任务
StructuredTaskScope.ShutdownOnSuccess<String> scope2;
```

### 优势
- 自动管理子任务生命周期
- 统一的错误处理
- 防止任务泄漏
- 更清晰的并发代码结构

## 7. 作用域值 (Scoped Values) - JEP 446

### 概述
提供ThreadLocal的现代替代方案，更适合虚拟线程。

### 基本用法
```java
public static final ScopedValue<String> USER_ID = ScopedValue.newInstance();

// 绑定值到作用域
ScopedValue.where(USER_ID, "user123").run(() -> {
    // 在这个作用域内，USER_ID.get() 返回 "user123"
    processRequest();
});

// 嵌套作用域
ScopedValue.where(USER_ID, "user123")
    .where(REQUEST_ID, "req456")
    .run(() -> {
        String userId = USER_ID.get();      // "user123"
        String requestId = REQUEST_ID.get(); // "req456"
    });
```

### 与ThreadLocal对比
| 特性 | ThreadLocal | ScopedValue |
|------|-------------|-------------|
| 可变性 | 可变 | 不可变 |
| 作用域 | 线程级别 | 明确边界 |
| 内存泄漏 | 风险较高 | 风险较低 |
| 虚拟线程 | 不适合 | 专门设计 |
| 性能 | 一般 | 更好 |

## 8. 其他重要特性

### JEP 439: Generational ZGC
- 分代Z垃圾收集器
- 更好的内存管理
- 降低GC延迟

### JEP 449: Deprecate Windows 32-bit x86 Port
- 弃用Windows 32位x86端口
- 专注于64位平台

### JEP 451: Prepare to Disallow Dynamic Agent Loading
- 准备禁止动态代理加载
- 提高安全性

### JEP 452: Key Encapsulation Mechanism API
- 密钥封装机制API
- 增强加密功能

## 9. API增强

### Math类新方法
```java
// 限制值在指定范围内
int clamped = Math.clamp(value, min, max);
```

### Character类新方法
```java
// 检查是否为emoji字符
boolean isEmoji = Character.isEmoji(codePoint);
```

### StringBuilder/StringBuffer增强
- 性能优化
- 新的构造方法

## 10. 性能改进

### 启动时间优化
- 更快的类加载
- 优化的JIT编译

### 内存使用优化
- 更好的内存布局
- 减少内存占用

### JIT编译器改进
- 更好的优化策略
- 提高运行时性能

## 11. 迁移指南

### 从JDK 17迁移到JDK 21
1. **评估现有代码**：检查是否使用了被弃用的API
2. **测试兼容性**：运行现有测试套件
3. **逐步采用新特性**：从虚拟线程开始
4. **性能测试**：验证性能改进
5. **安全审查**：检查新的安全特性

### 注意事项
- 某些预览特性需要启用预览模式
- 检查第三方库的兼容性
- 更新构建工具和IDE

## 12. 总结

JDK 21作为LTS版本，带来了许多重要改进：

### 🚀 革命性特性
- **虚拟线程**：改变并发编程范式
- **字符串模板**：更安全的字符串处理

### 🔧 实用改进
- **序列集合**：统一的集合操作
- **模式匹配增强**：更强大的类型检查

### 🏗️ 架构优化
- **结构化并发**：更好的并发管理
- **作用域值**：现代化的数据传递

### 📈 性能提升
- 启动时间优化
- 内存使用改进
- GC性能提升

JDK 21是Java生态系统的重要里程碑，特别是虚拟线程的引入将显著改变Java并发编程的方式。
