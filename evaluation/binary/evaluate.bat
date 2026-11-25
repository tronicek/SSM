@echo off
set SSM_JAR="/research/projects/SSM/target/SSM-1.0.jar"
set OPTIONS=-Xmx8G

java %OPTIONS% -cp %SSM_JAR% binary.Evaluation win.properties 6 > times6.txt
java %OPTIONS% -cp %SSM_JAR% binary.Evaluation win.properties 10 > times10.txt
java %OPTIONS% -cp %SSM_JAR% binary.Evaluation win.properties 20 > times20.txt
java %OPTIONS% -cp %SSM_JAR% binary.Evaluation win.properties 30 > times30.txt
java %OPTIONS% -cp %SSM_JAR% binary.Evaluation win.properties 40 > times40.txt
java %OPTIONS% -cp %SSM_JAR% binary.Evaluation win.properties 50 > times50.txt
java %OPTIONS% -cp %SSM_JAR% binary.Evaluation win.properties 60 > times60.txt
java %OPTIONS% -cp %SSM_JAR% binary.Evaluation win.properties 70 > times70.txt
java %OPTIONS% -cp %SSM_JAR% binary.Evaluation win.properties 80 > times80.txt
