JAVAC = javac
JFLAGS = -d build

SRCS = $(wildcard *.java)
OBJS = $(SRCS:%.java=build/%.class)

all: $(OBJS)

build/%.class: %.java
	$(JAVAC) $(JFLAGS) $<

clean:
	del build\*.class

run: all
	java -cp build Test

.PHONY: all clean run
