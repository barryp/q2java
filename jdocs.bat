@echo off

REM
REM Creates the javadocs for the Q2Java packages, and sticks them in the docs/ subdirectory.
REM

mkdir docs 2> nul
javadoc -sourcepath %classpath%;.\classes -d .\docs -version -author -private baseq2 baseq2.spawn q2java q2jgame javax.vecmath
echo.
pause
