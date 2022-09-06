package com.bakdata.data2day.extractor;

import com.bakdata.data2day.model.CorporatePojo;
import com.bakdata.data2day.model.CorporatePojo.CorporatePojoBuilder;
import com.bakdata.data2day.model.PersonPojo;
import com.bakdata.data2day.model.PersonPojo.PersonPojoBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
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
                final PersonPojoBuilder person = createPerson(rawText, officerInfoList);
                try {
                    addBirthInformationToPerson(matcher, officerInfoList, person);
                } catch (final IndexOutOfBoundsException exception) {
                    log.error("The was an error in getting the birthday and birth location of the person in: {}",
                        jsonNode.get("id"));
                    continue;
                }

                personList.add(person.build());
            }
            return personList;
        } catch (final JsonProcessingException exception) {
            log.error("Error in reading the input JSON.");
            return List.of(PersonPojo.builder().build());
        }
    }

    private static PersonPojoBuilder createPerson(final String rawText, final List<String> officerInfoList) {
        final String firstName = officerInfoList.get(1);
        final String lastName = officerInfoList.get(0);
        return PersonPojo.builder()
            .id(generateId(firstName + lastName + getCorporateName(rawText)))
            .firstName(firstName)
            .lastName(lastName)
            .corporateId(generateId(getCorporateName(rawText)));
    }

    private static void addBirthInformationToPerson(final MatchResult matcher,
        final List<String> officerInfoList,
        final PersonPojoBuilder person) {
        if (officerInfoList.get(2).contains("*")) {
            person.birthday(officerInfoList.get(2).replace("*", ""));
            person.birthLocation(matcher.group(5));
        } else {
            person.birthday(officerInfoList.get(3).replace("*", ""));
            person.birthLocation(officerInfoList.get(2));
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
                log.error("There was a problem extracting getting the street or city name for {}", jsonNode.get("id"));
                return corporate.build();
            }
            return corporate.build();
        } catch (final JsonProcessingException exception) {
            log.error("Error in reading the input JSON. Returning empty corporate.");
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
