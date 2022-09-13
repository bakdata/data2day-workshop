# data2day-workshop

Start the docker-compose Kafka cluster, including the Redpanda Console, Elasticsearch, and Neo4j:

1. Make sure `docker` and `docker-compose` are installed on your machine
2. To start the kafka cluster run `docker-compose up -d`


## Elasticsearch

After you set up the Kafka connectors, you can search the Elasticsearch indices using the Elasticsearch REST-API. E.g. the announcements index:

```sh
curl "http://localhost:9200/announcements/_search"
```
