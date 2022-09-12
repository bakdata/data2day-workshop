# Announcement producer

1. Make sure `docker` and `docker-compose` are installed on your machine
2. To start the kafka cluster run `docker-compose up -d`
3. Add the `corporate-events-dump` file to the root.
4. Install producer dependencies: `poetry install`
5. Run `poetry run python -m produce -f <path-to-dump-file>`

After you set up the Kafka connectors, you can search the Elasticsearch indices using the Elasticsearch REST-API. E.g. the announcements index:

```sh
curl "http://localhost:9200/announcements/_search"
```
