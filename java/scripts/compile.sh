#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


# compile the java program
javac -d $DIR/../classes $DIR/../src/Amazon.java

#run the java program
#Use your database name, port number and login
java -cp $DIR/../classes:$DIR/../lib/pg73jdbc3.jar Amazon $USER"_phase3_DB" $PGPORT $USER

