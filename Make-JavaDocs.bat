@echo off
echo This batch file will build JavaDocs for all the Java classes found in the
echo "classes" subdirectory of this directory.  
echo ---
echo Press Ctrl-C to cancel

pause

rem Make a list of all .java files
echo Writing a list of all .java files to "classes.list"
dir /s/b classes\*.java >classes.list

rem Process the whole works
echo Starting javadoc
javadoc -author -d docs\javadocs @classes.list