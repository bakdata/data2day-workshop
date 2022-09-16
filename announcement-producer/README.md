# Announcement producer

To run and produce announcements into a Kafka topic, following these steps:

1. Add the `corporate-events-dump` file to the root.
2. Install producer dependencies: `poetry install`
3. Run `poetry run python -m produce -f <path-to-dump-file>`

## Docker

`docker build .`

## Kubernetes

`kubectl apply -f job.yaml`
