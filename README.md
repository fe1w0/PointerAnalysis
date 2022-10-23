# Intraprocedural PointerAnalysis
软件分析课作业，使用SOOT完成指针分析，不能直接调用SOOT的指针分析模块。

## TODO List
- [x] 源代码阅读
- [x] 添加案例
- [x] 测试
- [ ] 优化

## Version
### version 0.1
- [x] 测试 sample/hello

### version 0.2
- [x] 考虑 `JInstanceFieldRef` 指令，~~即 `class.g` 无法用直接转换为Local 类~~，这个问题比我想象的难很多。

## 自言自语

指针分析算法设计：
本质上是，是实现Anderson指向分析算法

### FieldRef 问题

#### 何为 FieldRef 和 Field
> 有问题。。。，FieldRef 和 Field 无法转换成Value。
>

Field，在soot中，可以理解为Java Class的变量，如 `<objects.A: objects.B f>`

FieldRef，是Field的在域（Field）上的一个具体指针。

我的理解是 `Local.Field == FieldRef`，但Soot的Local好像没有Field迭代的接口

Anderson 换成 value, value


```bash
[*] Exception (leftValue Instanceof FieldRef):r0.<objects.A: int i> = 5
java.lang.ClassCastException: soot.AbstractSootFieldRef cannot be cast to soot.Value
```

没有优雅的办法，只能采用比较暴力的方案了。。。

定义四种关系，关系如下：
1. Local -> Local
2. Local -> FieldRef
3. FieldRef -> Local
4. FieldRef -> FieldRef

对于样本2，目前有两个困难：
1. 如何梳理对多种关系（四种）进行约束求解
2. 如何找到FieldRef和FieldRed对应的Local变量之前对关系，有助于提高精度。 

针对问题2，已经找到解决方案：
```java
if (rightValue instanceof FieldRef) {
    System.out.println("[*] Right Value FieldRef: " + rightValue);
    System.out.println("[*] GetField: " + ((FieldRef) rightValue).getField());
    for (ValueBox valueBox : rightValue.getUseBoxes()) {
        System.out.println(valueBox.getValue().toString());
    }
}
```

我好像把from和to的关系又弄混了。

以 a = b 为例子，b为fromValue，a为toValue。

