## 草稿

功能：

+ 前端
  1. 上传图片
  2. 保存图片
  3. 子窗口扩大缩小：改变图像
  4. 套索工具，选择重要区域；或去除某区域
  5. 撤回功能
+ 后端
  1. 计算Energy Map
  2. 缩图：每次计算最小的Seam
  3. 扩图：计算第K条seam；论文

论文

+ 





最小路径问题?

[[最短路径问题\]—Dijkstra 算法最详解 - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/129373740)





在构建Vertical Seam-Carving的Energy Map后在最上方和最下方构建一个虚拟的点，则问题转化为两个虚拟点的最小路径问题



缩放之后扩大后不是原图像（？）

## 会议

- [ ] 后端，前端思路
  - [ ] 每一个变量的含义以及内容
- [ ] 信息同步
  + [ ] git使用
- [ ] 重新分配ddl
- [ ] 现有的小问题
  + [ ] 终端共享
  + [ ] 代码：removeHorizontalSeam不需要calEnergyMap();
- [ ] 预估dilemma
- [ ] 分工

## 后端思路

### 初始化

前端上传图片后，进行初始化

1. `void calEnergyMap()`

   + `double deltaSquare()`
2. `void calVcost(),`; `void calHcost()`

   + `int minIndex()`
3. 初始化全局变量`int HStrechLog=0`, `int VStrechLog=0`

### 操作

前端传来的操作：`x+1`, `y+1`, `x-1`, `y-1`，

函数原型：`do(op)`

直接传入到`operate(op)`函数

最后将操作数push到大小为50的`undoStack`中，清空`redoStack`



`void operate(int op)`函数如下

每个操作数对应一个二进制数编码：00，01，11，10

eg: `public static int XADD=0b00;`

1. `x+1`，`strech`函数：更新当前的`energyMap`并重新计算`Vcost`数组，执行`findVSeam(HstrechLog*2+1)`，对`currentImage`进行线性插值，即在`seam`对应的像素点右侧插入新的像素，最后`seamsHistory.push(seam)`，`HstrechLog+=1`
2. `y+1`类似
3. `x-1`，`compress`函数：更新当前的`energyMap`并重新计算`Vcost`数组，执行`findVSeam(1)`计算能量最小的`seam`，然后将`seam`对应`currentImage`的像素点删除，将删除的像素点放在数组`colors`，并push到`ColorStack`，最后`seamsHistory.push(seam)`，`HstrechLog=0`
4. `y-1`类似



### Undo&Redo

前端传来的操作：`undo`

`op=undoStack.pop()`后判断：

1. `x+1`，`undoStrech`函数：
2. `y+1`类似
3. `x-1`，`undoCompress`函数：
4. `y-1`类似

> `int op=~undoStack.pop()&0b11`将栈弹出的op取反
>
> `operate(op)`执行
>
> `redoStack.push(op)`存储op

前端传来的操作：`redo`

`op=redoStack.pop()`后执行`operate(op)`

## 前端思路

### 图片缩放





### 撤回

按一次`ctrl+Z`喵一次，按住超过一秒则一直喵

按一次`ctrl+Y`嘎一次，按住超过一秒则一直嘎



## 代码

## 论文

## 文献

[Improved seam carving for video retargeting | ACM Transactions on Graphics](https://dl.acm.org/doi/abs/10.1145/1360612.1360615)

![image-20240506190217288](E:\resource\markdown\.pictures\image-20240506190217288.png)

![image-20240506194939017](E:\resource\markdown\.pictures\image-20240506194939017.png)