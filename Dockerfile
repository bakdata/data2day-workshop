ARG CONNECT_IMAGE_VERSION=6.2.0
FROM confluentinc/cp-kafka-connect:${CONNECT_IMAGE_VERSION}
ARG JDBC_VERSION=10.2.5
ARG ELASTICSEARCH_VERSION=11.1.7

# Set plugin path so Connects finds jar
ENV CONNECT_PLUGIN_PATH="/connect-plugins,/usr/share/java"
RUN confluent-hub install confluentinc/kafka-connect-jdbc:${JDBC_VERSION} --no-prompt
RUN confluent-hub install confluentinc/kafka-connect-elasticsearch:${ELASTICSEARCH_VERSION} --no-prompt
