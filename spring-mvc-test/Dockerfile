FROM java:8

RUN wget http://apache.fayea.com/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz
RUN tar xzvf apache-maven-3.5.0-bin.tar.gz

ENV M2_HOME /apache-maven-3.5.0
ENV PATH $PATH:$M2_HOME/bin

WORKDIR /app

ADD . /app


RUN mvn clean package -Dmaven.test.skip=true

EXPOSE 8080

CMD java -jar target/spring-mvc-test-0.0.1-SNAPSHOT.jar
