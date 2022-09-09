# Announcement producer

1. To start the kafka cluster run `docker-compose up -d`
2. Add the `corporate-events-dump` file to the root.
3. Install producer dependencies: `poetry install`
4. Run `poetry run python -m produce -f <path-to-dump-file>`

After you set up the Kafka connectors, you can search the Elasticsearch indices using the Elasticsearch REST-API. E.g. the announcements index:

```sh
curl "http://localhost:9200/announcements/_search"
```
