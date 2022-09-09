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

/**
 * A JSON extractor to pull out information from a JSON string into {@link CorporatePojo} and {@link PersonPojo}.
 */
@Slf4j
public class JsonExtractor {
    private static final Pattern PERSON_PATTERN =
        Pattern.compile("(([\\p{L} -]+, )([\\p{L} -]+, )?([\\p{L}.\\/ -]+), \\*\\d{2}.\\d{2}.\\d{4})(, )?");
    private static final Pattern COMMA = Pattern.compile(", ");

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Extracts person information.
     *
     * @param json input JSON string
     * @return List of {@link  PersonPojo}
     */
    public List<PersonPojo> extractPerson(final String json) {
        try {
            final JsonNode jsonNode = this.mapper.readTree(json);
            final String rawText = jsonNode.get("information").asText();
            final Matcher matcher = PERSON_PATTERN.matcher(rawText);

            final List<PersonPojo> personList = new ArrayList<>();
            while (matcher.find()) {
                final String fullOfficerInfo = matcher.group(0);
                final List<String> officerInfoList = List.of(COMMA.split(fullOfficerInfo));
                if (officerInfoList.isEmpty()) {
                    final String message =
                        String.format("Could not extract any person from %s", jsonNode.get("id"));
                    throw new JsonExtractorException(message);
                }
                final PersonPojoBuilder person = createPerson(rawText, officerInfoList);
                addBirthInformationToPerson(matcher, officerInfoList, person);

                personList.add(person.build());
            }
            return personList;
        } catch (final JsonProcessingException exception) {
            log.error("Error in reading the input JSON.");
            throw new RuntimeException("");
        }
    }

    /**
     * Extracts corporate information.
     *
     * @param json input JSON string
     * @return a {@link CorporatePojo} object
     */
    public CorporatePojo extractCorporate(final String json) {
        try {
            final JsonNode jsonNode = this.mapper.readTree(json);

            final String rawText = jsonNode.get("information").asText();
            final List<String> companyNameAddress = List.of(COMMA.split(rawText));
            if (companyNameAddress.isEmpty()) {
                final String message =
                    String.format("Could not extract street and city information from %s", jsonNode.get("id"));
                throw new JsonExtractorException(message);
            }
            return createCorporate(jsonNode, rawText, companyNameAddress);
        } catch (final JsonProcessingException exception) {
            final String errorMessage = "Error in reading the input JSON.";
            log.error(errorMessage);
            throw new JsonExtractorException(errorMessage);
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

    private static CorporatePojo createCorporate(final JsonNode jsonNode, final String rawText,
        final List<String> companyNameAddress) {
        final CorporatePojoBuilder corporate = CorporatePojo.builder().id(generateId(getCorporateName(rawText)));
        corporate.referenceId(jsonNode.get("reference_id").asText());
        corporate.name(getCorporateName(rawText));
        corporate.street(companyNameAddress.get(2));
        corporate.city(companyNameAddress.get(1));

        return corporate.build();
    }

    private static String generateId(final CharSequence input) {
        return Hashing.sha256()
            .hashString(input, StandardCharsets.UTF_8)
            .toString();
    }

    @NotNull
    private static String getCorporateName(final CharSequence rawText) {
        final List<String> companyNameAddress = List.of(COMMA.split(rawText));
        if (companyNameAddress.isEmpty()) {
            throw new JsonExtractorException("Could not extract any corporate name");
        }
        return companyNameAddress.get(0).substring(companyNameAddress.get(0).indexOf(':') + 2);
    }
}
