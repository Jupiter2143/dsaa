## 整体目标

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
  3. 扩图：计算第K条seam

论文

+ 



## Seam-Carving核心算法

最小路径问题?

[最短路径问题—Dijkstra 算法最详解 - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/129373740)





在构建Vertical Seam-Carving的Energy Map后在最上方和最下方构建一个虚拟的点，则问题转化为两个虚拟点的最小路径问题

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

### 辅助函数

`direction`是`seam`的方向

1. `void insertPixels(boolean direction, int[] seam, Color[] pixels, )`在seam左侧插入pixels
2. `Color[] removePixels(boolean direction, int[] seam)`

### 初始化

前端上传图片后，进行初始化

1. `void calEnergyMap()`
+ `double deltaSquare()`
2. `void calVcost(),`; `void calHcost()`

   + `int minIndex()`
3. 初始化全局变量`int HStrechLog=0`, `int VStrechLog=0`

### 操作

前端传来的操作：`x+1`, `y+1`, `x-1`, `y-1`，

`void operate(int op)`函数如下

每个操作数对应一个二进制数编码：00，01，11，10

eg: `public static int XADD=0b00;`

1. `x+1`，`strech`函数：

   update`energyMap`, update`Vcost`, `int[] seam=findVseam(HstrechLog*2+1)`
   在`seam`对应的像素点左侧线性插值
   最后`undoSeamsStack.push(seam)`，`HstrechLog+=1`

2. `y+1`类似

3. `x-1`，`compress`函数：

   update`energyMap`, update`Vcost`, `int[] seam=findVseam(1)`
   `pixels=removePixels(false,seam)`

   最后`undoSeamsStack.push(seam)`，`undoPixelsStack.push(pixels)`，`HstrechLog=0`

4. `y-1`类似

最后`undoStack.push(op)`，清空`redoStack`，`redoSeamsStack`，`redoPixelsStack`

### Undo&Redo

前端传来的操作：`undo`

`op=undoStack.pop()`后判断：

1. `x+1`，`undoStrech`函数：
   `seam=undoSeamsStack.pop`, `pixels=removePixels(false, seam)`
   最后`redoSeamsStack.push(seam)`, `redoPixelsStack.push(pixels)`
2. `y+1`类似
3. `x-1`，`undoCompress`函数：
   `seam=undoSeamsStack.pop`, `pixels=undoPixelsStack.pop`, 
   ` insertPixels(false,seam,pixels)`
   最后`redoSeamsStack.push(seam)`，`redoPixelsStack.push(pixels)`
4. `y-1`类似

最后`redoStack.push(op)`



前端传来的操作：`redo`

`op=redoStack.pop()`后判断：

1. `x+1`，`redoStrech`函数：

   `seam=redoSeamsStack.pop`, `pixels=redoPixelsStack.pop`, 
   ` insertPixels(false,seam,pixel)`
   最后`undoSeamsStack.push(seam)`，`undoPixelsStack.push(pixels)`

2. `y+1`类似

3. `x-1`，`redoCompress`函数：

   `seam=redoSeamsStack.pop`, `pixels=removePixels(false, seam)`
   最后`undoSeamsStack.push(seam)`, `undoPixelsStack.push(pixels)`

4. `y-1`类似

> 整合`undo`和`redo`相关函数

### mask

传入当前图片大小的矩阵`mask`：`0`代表不做处理，`1e9`表示保护，`-1e9`表示优先删除

更改`energyMap`的计算方法：原有的`energyMap`与`mask`相加

## 前端思路

### 图片上传

在子窗口中放置图像，图像大

### 图片缩放

改变子窗口大小时更改图像大小

### 撤回

按一次`ctrl+Z`或者`ctrl+Y`执行`undo`一次，按住超过一秒则一直`undo`

### 界面

菜单栏：文件——编辑——工具——帮助

+ 文件：打开，保存，退出
+ 编辑：撤销，重做
+ 工具：套索，橡皮擦
+ 帮助：官方网站(github)



主界面：上方显示像素长度，宽度（可编辑）；

左侧显示图片，右侧：

+ 右上角原图缩放
+ spinBox调节W，H
+ undo; redo

## 优化



根据原有的`energyMap`进行`update`而不是重新计算

## 论文







## 文献

[Improved seam carving for video retargeting | ACM Transactions on Graphics](https://dl.acm.org/doi/abs/10.1145/1360612.1360615)

![image-20240506190217288](E:\resource\markdown\.pictures\image-20240506190217288.png)

![image-20240506194939017](E:\resource\markdown\.pictures\image-20240506194939017.png)