import json
import logging
import os

from announcement_producer import AnnouncementProducer

ENCODING = "UTF-8"

DUMP_FILE = "corporate-events-dump"

logging.basicConfig(
    level=os.environ.get("LOGLEVEL", "INFO"),
    format="%(asctime)s | %(name)s | %(levelname)s | %(message)s",
)
log = logging.getLogger(__name__)


if __name__ == "__main__":
    with open(DUMP_FILE, "r", encoding=ENCODING) as file:
        while line := file.readline().rstrip():
            json_announcement = json.loads(line)
            key = json_announcement["_id"]
            value = json_announcement["_source"]

            AnnouncementProducer().produce_to_topic(key, json.dumps(value))
