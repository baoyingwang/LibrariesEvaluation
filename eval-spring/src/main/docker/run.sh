#!/bin/sh
echo "********************************************************"
echo "Starting Eval SpringBoot Server"
echo "********************************************************"
java -Dspring.profiles.active=$PROFILE -jar /usr/local/eval-spring/@project.build.finalName@.jar
