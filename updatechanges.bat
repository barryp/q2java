@echo off
set savedCLASSPATH=%CLASSPATH%
set CLASSPATH=g:\javawork\xalan\xerces.jar;g:\javawork\xalan\xalan.jar;%CLASSPATH%
java org.apache.xalan.xslt.Process -in changes-%1.xml -xsl changes.xsl -out Changes-%1.html
set CLASSPATH=%savedCLASSPATH%