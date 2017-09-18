# Dockerfile - Webapp

FROM tomcat:9.0.0

RUN mkdir -p /usr/app/config
RUN mkdir -p /usr/app/security
RUN mkdir -p /usr/app/store

RUN mkdir -p /tmp/app/upload


ENV CATALINA_OPTS="-Dpicdrop.app.properties=/usr/app/config/picdrop.config.docker.properties"

RUN rm -r  /usr/local/tomcat/webapps/*
COPY ./target/picdrop.war /usr/local/tomcat/webapps

CMD ["catalina.sh", "run"]