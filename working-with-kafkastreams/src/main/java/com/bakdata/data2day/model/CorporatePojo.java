package com.bakdata.data2day.model;

import com.bakdata.rb.avro.corporate.v1.AvroCorporate;
import com.bakdata.rb.proto.corporate.v1.ProtoCorporate;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents a corporate object.
 */
@Builder
@Getter
public class CorporatePojo {
    private final String id;
    private final String referenceId;
    private final String name;
    private final String street;
    private final String city;

    /**
     * Converts the corporate POJO to a Protobuf corporate.
     *
     * @return {@link ProtoCorporate} object
     */
    public ProtoCorporate toProto() {
        return ProtoCorporate.newBuilder()
            .setId(Objects.requireNonNullElse(this.id, ""))
            .setReferenceId(Objects.requireNonNullElse(this.referenceId, ""))
            .setName(Objects.requireNonNullElse(this.name, ""))
            .setStreet(Objects.requireNonNullElse(this.street, ""))
            .setCity(Objects.requireNonNullElse(this.city, ""))
            .build();
    }

    /**
     * Converts the person POJO to an Avro corporate.
     *
     * @return {@link AvroCorporate} object
     */
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
