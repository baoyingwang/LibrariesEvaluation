FROM openjdk:14
# RUN  apk update && apk upgrade && apk add netcat-openbsd
RUN mkdir -p /usr/local/eval-spring
# mvn build时候project.build.finalName将被替换
ADD  @project.build.finalName@.jar /usr/local/eval-spring/
ADD run.sh run.sh
RUN chmod +x run.sh
CMD ./run.sh
