SSM_JAR="/home/tronz635/research/SSM/SSM-1.0.jar"
OPTIONS="-Xmx8G"

java $OPTIONS -cp $SSM_JAR acgt.Evaluation linux.properties 6 > times6.txt
java $OPTIONS -cp $SSM_JAR acgt.Evaluation linux.properties 10 > times10.txt
java $OPTIONS -cp $SSM_JAR acgt.Evaluation linux.properties 20 > times20.txt
java $OPTIONS -cp $SSM_JAR acgt.Evaluation linux.properties 30 > times30.txt
java $OPTIONS -cp $SSM_JAR acgt.Evaluation linux.properties 40 > times40.txt
java $OPTIONS -cp $SSM_JAR acgt.Evaluation linux.properties 50 > times50.txt
java $OPTIONS -cp $SSM_JAR acgt.Evaluation linux.properties 60 > times60.txt
java $OPTIONS -cp $SSM_JAR acgt.Evaluation linux.properties 70 > times70.txt
java $OPTIONS -cp $SSM_JAR acgt.Evaluation linux.properties 80 > times80.txt
