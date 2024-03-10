# Builder
FROM maven:3.9-eclipse-temurin-17-alpine as builder
WORKDIR /workspace/app

ENV HOME=/usr/app
RUN mkdir -p $HOME
ADD . $HOME

COPY pom.xml .
COPY src src

RUN mvn install -DskipTests -Dmaven.javadoc.skip=true

# Runner!
FROM eclipse-temurin:17-jdk-alpine as runner
VOLUME /tmp
WORKDIR /workspace/app

ENV TERM=xterm-256color
ENV version="0.1.3-SNAPSHOT"
ENV gav="data-0.1.3-SNAPSHOT"
LABEL version=${version}

RUN echo "PS1='\e[92m\u\e[0m@\e[94m\h\e[0m:\e[35m\w\e[0m# '" >> /root/.bashrc
COPY --from=builder /workspace/app/target/*.jar .
COPY run.sh run.sh
RUN chmod +x run.sh

EXPOSE 8080

ENTRYPOINT ["./run.sh"]