package com.bakdata.data2day.model;

import com.bakdata.rb.avro.person.v1.AvroPerson;
import com.bakdata.rb.proto.person.v1.ProtoPerson;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents a person object.
 */
@Builder
@Getter
public class PersonPojo {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String birthday;
    private final String birthLocation;
    private final String corporateId;

    /**
     * Converts the person POJO to a Protobuf person.
     *
     * @return {@link ProtoPerson} object
     */
    public ProtoPerson toProto() {
        return ProtoPerson.newBuilder()
            .setId(Objects.requireNonNullElse(this.id, ""))
            .setFirstName(Objects.requireNonNullElse(this.firstName, ""))
            .setLastName(Objects.requireNonNullElse(this.lastName, ""))
            .setBirthday(Objects.requireNonNullElse(this.birthday, ""))
            .setBirthLocation(Objects.requireNonNullElse(this.birthLocation, ""))
            .setCorporateId(Objects.requireNonNullElse(this.corporateId, ""))
            .build();
    }

    /**
     * Converts the person POJO to an Avro person.
     *
     * @return {@link AvroPerson} object
     */
    public AvroPerson toAvro() {
        return AvroPerson.newBuilder()
            .setId(this.id)
            .setFirstName(this.firstName)
            .setLastName(this.lastName)
            .setBirthday(this.birthday)
            .setBirthLocation(this.birthLocation)
            .setCorporateId(this.corporateId)
            .build();
    }
}
