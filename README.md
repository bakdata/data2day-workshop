# data2day-workshop


Start the docker-compose Kafka cluster, including the Redpanda Console, Elasticsearch, and Neo4j:

1. Make sure `docker` and `docker-compose` are installed on your machine
2. To start the kafka cluster run `docker-compose up -d`

When the cluster is running, you can follow these steps:

1. Install and run the announcements [producer](./announcement-producer/README.md)
2. Run the [corporate and persons extractor Kafka Streams app](./working-with-kafkastreams/README.md)
3. [Create the Kafka Connectors](./connectors/README.md) and explore the results in Elasticsearch
