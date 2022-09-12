package com.bakdata.data2day.model;

import com.bakdata.rb.avro.corporate.v1.AvroCorporate;
import com.bakdata.rb.proto.corporate.v1.ProtoCorporate;
import java.util.Arrays;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

@Builder
@Getter
public class CorporatePojo {
    private final String id;
    private final String referenceId;
    private final String name;
    private final String street;
    private final String city;

    public ProtoCorporate toProto() {
        return ProtoCorporate.newBuilder()
            .setId(Objects.requireNonNullElse(this.id, ""))
            .setReferenceId(Objects.requireNonNullElse(this.referenceId, ""))
            .setName(Objects.requireNonNullElse(this.name, ""))
            .setStreet(Objects.requireNonNullElse(this.street, ""))
            .setCity(Objects.requireNonNullElse(this.city, ""))
            .build();
    }

    @SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
    public static void main(String[] args) {
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> textLines = builder.stream("TextLinesTopic");

        final KTable<String, Long> wordCounts = textLines
            .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
            .groupBy((key, word) -> word)
            .count();

        wordCounts.toStream().to("WordsWithCountsTopic", Produced.valueSerde(Serdes.Long()));
    }

    public AvroCorporate toAvro() {
        return AvroCorporate
            .newBuilder()
            .setId(this.id)
            .setReferenceId(this.referenceId)
            .setName(this.name)
            .setStreet(this.street)
            .setCity(this.city)
            .build();
    }
}
