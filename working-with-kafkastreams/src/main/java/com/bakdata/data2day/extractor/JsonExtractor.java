package com.bakdata.data2day.extractor;

import com.bakdata.data2day.model.CorporatePojo;
import com.bakdata.data2day.model.CorporatePojo.CorporatePojoBuilder;
import com.bakdata.data2day.model.PersonPojo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class JsonExtractor {
    private static final Pattern PERSON_PATTERN =
        Pattern.compile("(([\\p{L} -]+, )([\\p{L} -]+, )?([\\p{L}.\\/ -]+), \\*\\d{2}.\\d{2}.\\d{4})(, )?");
    private static final Pattern COMMA = Pattern.compile(", ");

    private final ObjectMapper mapper = new ObjectMapper();

    public List<PersonPojo> extractPerson(final String json) {
        try {
            final JsonNode jsonNode = this.mapper.readTree(json);
            final String rawText = jsonNode.get("information").asText();
            final Matcher matcher = PERSON_PATTERN.matcher(rawText);

            final List<PersonPojo> personList = new ArrayList<>();
            while (matcher.find()) {
                final String fullOfficerInfo = matcher.group(0);
                final List<String> officerInfoList = List.of(COMMA.split(fullOfficerInfo));

                final String firstName = officerInfoList.get(1);
                final String lastName = officerInfoList.get(0);
                final PersonPojo.PersonPojoBuilder person = PersonPojo.builder()
                    .id(generateId(firstName + lastName + getCorporateName(rawText)))
                    .firstName(firstName)
                    .lastName(lastName)
                    .corporateId(generateId(getCorporateName(rawText)));

                try {
                    if (officerInfoList.get(2).contains("*")) {
                        person.birthday(officerInfoList.get(2).replace("*", ""));
                        person.birthLocation(matcher.group(5));
                    } else {
                        person.birthday(officerInfoList.get(3).replace("*", ""));
                        person.birthLocation(officerInfoList.get(2));
                    }
                } catch (final IndexOutOfBoundsException exception) {
                    continue;
                }

                personList.add(person.build());
            }
            return personList;
        } catch (final JsonProcessingException exception) {
            return List.of(PersonPojo.builder().build());
        }
    }

    public CorporatePojo extractCorporate(final String json) {
        try {
            final JsonNode jsonNode = this.mapper.readTree(json);

            final String rawText = jsonNode.get("information").asText();
            final List<String> companyNameAddress = List.of(COMMA.split(rawText));

            final CorporatePojoBuilder corporate = CorporatePojo.builder().id(generateId(getCorporateName(rawText)));
            try {
                corporate.referenceId(jsonNode.get("reference_id").asText());
                corporate.name(getCorporateName(rawText));
                corporate.street(companyNameAddress.get(2));
                corporate.city(companyNameAddress.get(1));
            } catch (final IndexOutOfBoundsException exception) {
                return corporate.build();
            }
            return corporate.build();
        } catch (final JsonProcessingException exception) {
            return CorporatePojo.builder().build();
        }
    }

    private static String generateId(final CharSequence input) {
        return Hashing.sha256()
            .hashString(input, StandardCharsets.UTF_8)
            .toString();
    }

    @NotNull
    private static String getCorporateName(final CharSequence rawText) {
        final List<String> companyNameAddress = List.of(COMMA.split(rawText));
        return companyNameAddress.get(0).substring(companyNameAddress.get(0).indexOf(':') + 2);
    }
}
