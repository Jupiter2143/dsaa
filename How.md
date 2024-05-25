# Compile the project and Run by Using Makefile

## Prerequisites

1. [Install MinGW](https://sourceforge.net/projects/mingw/): This is a collection of freely available and freely distributable Windows specific header files and import libraries combined with GNU toolsets that allow one to produce native Windows programs that do not rely on any third-party C runtime DLLs.
2. **Configure Environment Variables**: Add the `bin` directory of your MinGW installation to your Windows PATH environment variable so that you can call `mingw32-make` from the command line anywhere.
3. **Write a Makefile**: This is a file that contains build instructions and rules for `make` to follow.
4. **Run `mingw32-make`**: Open a command line window in the directory containing your Makefile and run `mingw32-make` to start the build process.

## Compile

`Makefile` : the file that contains build instructions and rules for `make` to follow.

`mingw32-make` : the command to run the make utility.

## Use

`mingw32-make run` : the command to run the make utility with the `run` target.

`mingw32-make clean` : the command to run the make utility with the `clean` target.

