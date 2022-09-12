FROM openjdk:11.0.15-slim
RUN apt-get update && apt-get upgrade -y

COPY build/libs/nordic.jar /deployments/app.jar
COPY metrics/jmx_prometheus_javaagent-*.jar /metrics/jmx_prometheus_javaagent.jar
COPY metrics/jmx_exporter_config.yml /metrics/jmx_exporter_config.yml

WORKDIR /deployments

RUN chmod 600 .

CMD ["java", \
           "-Dcom.sun.management.jmxremote.ssl=false", \
           "-Dcom.sun.management.jmxremote.authenticate=false", \
           "-Dcom.sun.management.jmxremote.port=5555", \
           "-javaagent:/metrics/jmx_prometheus_javaagent.jar=5556:/metrics/jmx_exporter_config.yml",\
           "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]