@echo off
rem make sure a policy file exists in case we're running with JDK 1.2
if not exist q2java.policy copy q2java.policy.sample q2java.policy

if exist classes\q2java\Engine.class goto run
echo You don't seem to have compiled the Java classes, please
echo run Make-Classes.bat or something similar before
echo trying to run Q2Java
goto quit

:run
cd ..
start quake2 +set game q2java +set deathmatch 1 %1 %2 %3 %4 %5 %6 %7 %8 %9

:quit