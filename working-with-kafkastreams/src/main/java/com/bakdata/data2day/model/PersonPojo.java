package com.bakdata.data2day.model;

import com.bakdata.rb.avro.person.v1.AvroPerson;
import com.bakdata.rb.proto.person.v1.ProtoPerson;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PersonPojo {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String birthday;
    private final String birthLocation;
    private final String corporateId;

    public ProtoPerson toProto() {
        return ProtoPerson.newBuilder()
            .setId(this.id)
            .setFirstName(this.firstName)
            .setLastName(this.lastName)
            .setBirthday(this.birthday)
            .setBirthLocation(this.birthLocation)
            .setCorporateId(this.corporateId)
            .build();
    }

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
