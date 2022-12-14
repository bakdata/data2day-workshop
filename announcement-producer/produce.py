import json
import logging
import os

import click

from announcement_producer import AnnouncementProducer

ENCODING = "UTF-8"

DUMP_FILE = "corporate-events-dump"

logging.basicConfig(
    level=os.environ.get("LOGLEVEL", "INFO"),
    format="%(asctime)s | %(name)s | %(levelname)s | %(message)s",
)
log = logging.getLogger(__name__)


@click.command()
@click.option("-f", "--file", required=True, type=str, help="Path to the dump file")
@click.option("--bootstrap-servers", default="localhost:29092", type=str,
              help="Bootstrap servers to connect to")
@click.option("-t", "--topic", default="announcements", type=str,
              help="Topic to produce to")
def produce_announcements(file: str, topic: str, bootstrap_servers: str):
    with open(file, "r", encoding=ENCODING) as dump:
        while line := dump.readline().rstrip():
            json_announcement = json.loads(line)
            key = json_announcement["_id"]
            value = json_announcement["_source"]

            producer = AnnouncementProducer(topic, bootstrap_servers)
            producer.produce_to_topic(key, json.dumps(value))


if __name__ == "__main__":
    produce_announcements()
