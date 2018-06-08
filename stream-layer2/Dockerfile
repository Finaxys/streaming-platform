FROM maven:3.5.2-jdk-8
VOLUME /home/finaxys/StreamingAsAService/streaming-platform/stream-layer2
VOLUME /home/finaxys/.m2
RUN mkdir /docker/proj /docker/flink -p
RUN cd /docker/flink && wget https://archive.apache.org/dist/flink/flink-1.4.2/flink-1.4.2-bin-scala_2.11.tgz
COPY pom.xml /docker/proj/pom.xml
COPY src /docker/proj/src
EXPOSE 8081
RUN cd /docker/proj && mvn clean && mvn package
RUN cd /docker/flink && tar -xf flink-1.4.2-bin-scala_2.11.tgz
