import logging

from confluent_kafka import SerializingProducer
from confluent_kafka.serialization import StringSerializer

log = logging.getLogger(__name__)


class AnnouncementProducer:
    BOOTSTRAP_SERVER: str = "k8kafka-cp-kafka-headless.infrastructure:9092"
    TOPIC = "announcements"

    def __init__(self):
        producer_conf = {
            "bootstrap.servers": self.BOOTSTRAP_SERVER,
            "key.serializer": StringSerializer("utf_8"),
            "value.serializer": StringSerializer("utf_8"),
        }

        self.producer = SerializingProducer(producer_conf)

    def produce_to_topic(self, key: str, value: str):
        self.producer.produce(
            topic=self.TOPIC,
            partition=-1,
            key=key,
            value=value,
            on_delivery=self.delivery_report,
        )

        # It is a naive approach to flush after each produce this can be optimised
        self.producer.poll()

    @classmethod
    def delivery_report(cls, err, msg):
        """
        Reports the failure or success of a message delivery.
        Args:
            err (KafkaError): The error that occurred on None on success.
            msg (Message): The message that was produced or failed.
        Note:
            In the delivery report callback the Message.key() and Message.value()
            will be the binary format as encoded by any configured Serializers and
            not the same object that was passed to produce().
            If you wish to pass the original object(s) for key and value to delivery
            report callback we recommend a bound callback or lambda where you pass
            the objects along.
        """
        if err is not None:
            log.error("Delivery failed for User record {}: {}".format(msg.key(), err))
            return
        log.info(
            "Record with key {} successfully produced to {} [{}] at offset {}".format(
                msg.key(), msg.topic(), msg.partition(), msg.offset()
            )
        )
