FROM ibm-semeru-runtimes:open-17-jre
RUN mkdir /opt/app
COPY target/my-consumer-0.0.1-jar-with-dependencies.jar /opt/app/consumer.jar
CMD ["java", "-cp", "/opt/app/consumer.jar", "com.ibm.eventstreams.demos.mm2.ConsumerApplication"]