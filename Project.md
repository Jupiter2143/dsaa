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

## 后端思路

### 初始化

前端上传图片后，进行初始化

1. `void calEnergyMap()`

   + `double deltaSquare()`

2. `void calVcost(),`; `void calHcost()`

   + `int minIndex()`

3. `void calVseams()`; `void calHseams()`计算出k条Vseam和Hseam，并存储在`Vseams`和`Hseams`中，用于后续的`strech`操作

   > 这里的k待定，表示最大的`strech`程度
   >
   > 暂定`k=0.5width`或`k=0.5height`
   >
   > `Vseams`中每一列第k小的数都需要加k-1，k从1遍历到该数组的宽度

   + `int[] findHseam(int k)`k从1遍历到k
   + `int[] findVseam(int k)`k从1遍历到k

### 操作

前端传来的操作：`x+1`, `y+1`, `x-1`, `y-1`，
每个操作数对应一个二进制数编码：00，01，11，10

eg: `public static int XADD=0b00;`

在函数`Image operate(int op)`中执行

1. `x+1`，判断current image和origin image的width
   + 如果大于等于，则进行`strech`操作：选择`Vseams`中的第`currentImageWidth-OriginImageWidth+1`条seam，对current image进行线性插值，即在seam对应的像素点右侧插入新的像素
   + 如果小于，则进行`undoCompress`操作：`VseamsStack.pop()`后将`seam`对应`originImage`的像素点插入到`currentImage`的相应位置
2. `y+1`类似
3. `x-1`，判断current image和origin image
   + 如果小于等于，则进行`Compress`操作：更新当前的`energyMap`并重新计算`Hcost`数组，执行`findHorizontalSeam(1)`计算能量最小的`seam`，然后将`seam`对应`currentImage`的像素点删除，最后`Vseams.push(seam)`
   + 如果大于，则进行`undoStrech`操作：选择`Vseams`中第`currentImageWidth-OriginImageWidth`条seam，将`seam`对应`currentImage`右侧的像素点删除
4. `y-1`类似

最后将操作数push到大小为50的`undoStack`中，清空`redoStack`

### Undo&Redo

前端传来的操作：`undo`

> `int op=~undoStack.pop()&0b11`将栈弹出的op取反
>
> `operate(op)`执行
>
> `redoStack.push(op)`存储op

前端传来的操作：`redo`

> `int op=~redoStack.pop()&0b11`将栈弹出的op取反
>
> `operate(op)`执行
>
> `undoStack.push(op)`存储op

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