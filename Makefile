# 定义编译器和编译选项
JAVAC = javac
JFLAGS = -d build

# 定义源文件和目标文件夹
SRCS = $(wildcard *.java)
OBJS = $(SRCS:%.java=build/%.class)

# 默认规则
all: $(OBJS)

# 类文件规则
build/%.class: %.java
	$(JAVAC) $(JFLAGS) $<

# 清理规则
clean:
	del build\*.class

# 将主窗口放在屏幕中间并运行程序的规则
run: all
	java -cp build Test

# 伪目标
.PHONY: all clean run
