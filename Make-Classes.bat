@echo off
echo This batch file will recompile all the Java classes found in the
echo "classes" subdirectory of this directory.  
echo ---
echo ***IMPORTANT*** JDK 1.2 or later is required to run this batch file
echo ---
echo Press Ctrl-C to cancel

pause

rem Make a list of all .java files
echo Writing a list of all .java files to "classes.list"
dir /s/b classes\*.java >classes.list

rem Compile the whole works
echo Starting javac, this may take several minutes, please be patient ...
javac -Xdepend -classpath classes @classes.list