package com.bakdata.data2day.model;

import com.bakdata.rb.avro.corporate.v1.AvroCorporate;
import com.bakdata.rb.proto.corporate.v1.ProtoCorporate;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CorporatePojo {
    private final String id;
    private final String referenceId;
    private final String name;
    private final String street;
    private final String city;

    public ProtoCorporate toProto() {
        return ProtoCorporate
            .newBuilder()
            .setId(this.id)
            .setReferenceId(this.referenceId)
            .setName(this.name)
            .setStreet(this.street)
            .setCity(this.city)
            .build();
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
