@echo off

REM
REM Creates the javadocs for the Q2Java packages, and sticks them in the docs/ subdirectory.
REM



mkdir docs 2> nul

javadoc -sourcepath %classpath%;.\classes -d .\docs -version -author q2java q2java.baseq2 q2java.baseq2.event q2java.baseq2.gui q2java.baseq2.model q2java.baseq2.rule q2java.baseq2.spawn q2java.core q2java.core.event q2java.core.gui q2java.ctf q2java.ctf.spawn q2java.gui javax.vecmath 

echo.

pause

