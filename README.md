# Intraprocedural PointerAnalysis
软件分析课作业，使用SOOT完成指针分析，不能直接调用SOOT的指针分析模块。

## TODO List
- [x] 源代码阅读
- [x] 添加案例
- [ ] 测试
- [ ] 优化

### version 0.1
- [x] 测试 sample/hello

### version 0.2
- [ ] 考虑 `JInstanceFieldRef` 指令，即 `class.g` 无法用直接转换为Local 类

## 自言自语

指针分析算法设计：
本质上是，是实现Anderson指向分析算法


### FiledRef 问题

Anderson 换成 value, value

> 有问题。。。
> 
> 

```bash
[*] Exception (leftValue Instanceof FieldRef):r0.<objects.A: int i> = 5
java.lang.ClassCastException: soot.AbstractSootFieldRef cannot be cast to soot.Value
```

没有办法，想到一个比较暴力的方法。

定义四种关系，关系如下：
1. Local -> Local
2. Local -> FieldRef
3. FieldRef -> Local
4. FieldRef -> FieldRef

 那如何梳理计算多种关系的结果。

同样，有个需要验证，我先用PrintTransform进行验证。



