FROM maven:3.9.6-amazoncorretto-8-debian-bookworm AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM tomcat:10-jre8-openjdk-buster
RUN apt-get update && apt-get install -y openssh-server && mkdir /var/run/sshd && echo 'root:1234' | chpasswd


# WAR ve ROOT klasörünü Tomcat'e kopyala
COPY --from=build /build/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war
COPY --from=build /build/target/ROOT /usr/local/tomcat/webapps/ROOT

# SSH ayarları
COPY init.sh /init.sh
RUN chmod +x /init.sh

EXPOSE 8080 22
CMD ["/bin/bash", "/init.sh"]
