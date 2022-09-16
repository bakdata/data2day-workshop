# Kafka Connect

Use the Redpanda Console to create Kafka Connectors easily. You can access the Redpanda console [here](http://localhost:8080).

## Explore Elasticsearch indices

After you set up the Kafka connectors, you can search the Elasticsearch indices using the Elasticsearch REST-API. E.g.
the announcements index:

```sh
curl "http://localhost:9200/announcements/_search"
```
