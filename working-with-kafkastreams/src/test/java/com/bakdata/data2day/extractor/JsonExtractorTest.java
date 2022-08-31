package com.bakdata.data2day.extractor;

import static org.assertj.core.api.Assertions.assertThat;

import com.bakdata.data2day.model.CorporatePojo;
import com.bakdata.data2day.model.PersonPojo;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class JsonExtractorTest {
    private final JsonExtractor jsonExtractor = new JsonExtractor();

    @Test
    void shouldExtractPerson() throws IOException {
        final String fixture = Resources.toString(Resources.getResource("test.json"), Charsets.UTF_8);

        final List<PersonPojo> personList = this.jsonExtractor.extractPerson(fixture);

        assertThat(personList)
            .hasSize(2)
            .extracting(PersonPojo::getFirstName)
            .containsExactly("Alexander", "Oliver");
    }

    @Test
    void shouldExtractCorporate() throws IOException {
        final String fixture = Resources.toString(Resources.getResource("test.json"), Charsets.UTF_8);

        final CorporatePojo corporate = this.jsonExtractor.extractCorporate(fixture);

        assertThat(corporate).satisfies(expectedCorporate -> {
            assertThat(expectedCorporate.getName()).isEqualTo("Unser Cafe Verwaltungs GmbH");
            assertThat(expectedCorporate.getCity()).isEqualTo("Berlin");
        });
    }
}