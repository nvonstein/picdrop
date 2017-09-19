# Dockerfile - Webapp

FROM tomcat:9.0.0

RUN mkdir -p /usr/local/picdrop/config
RUN mkdir -p /usr/local/picdrop/security
RUN mkdir -p /usr/local/picdrop/store

RUN mkdir -p /tmp/app/upload


ENV CATALINA_OPTS="-Dpicdrop.app.properties=/usr/local/picdrop/config/picdrop.config.docker.properties"

RUN rm -r  /usr/local/tomcat/webapps/*
COPY ./target/picdrop.war /usr/local/tomcat/webapps

RUN ln -sf /dev/stderr /usr/local/tomcat/logs/picdrop.error.out

CMD ["catalina.sh", "run"]