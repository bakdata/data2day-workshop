package com.bakdata.data2day.extractor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bakdata.data2day.model.CorporatePojo;
import com.bakdata.data2day.model.PersonPojo;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class JsonExtractorTest {

    @Test
    void shouldExtractPerson() throws IOException {
        final String fixture = Resources.toString(Resources.getResource("test.json"), Charsets.UTF_8);

        final List<PersonPojo> personList = new JsonExtractor(false).extractPerson(fixture);

        assertThat(personList)
            .hasSize(2)
            .extracting(PersonPojo::getFirstName)
            .containsExactly("Alexander", "Oliver");
    }

    @Test
    void shouldExtractCorporate() throws IOException {
        final String fixture = Resources.toString(Resources.getResource("test.json"), Charsets.UTF_8);

        final Optional<CorporatePojo> corporate = new JsonExtractor(false).extractCorporate(fixture);

        assertThat(corporate).isPresent();

        assertThat(corporate.get()).satisfies(expectedCorporate -> {
            assertThat(expectedCorporate.getName()).isEqualTo("Unser Cafe Verwaltungs GmbH");
            assertThat(expectedCorporate.getCity()).isEqualTo("Berlin");
        });
    }

    @Test
    void shouldThrowExceptionWhenWrongFormatInPersonTextAndThrowExceptionIsEnabled() throws IOException {
        final String fixture = Resources.toString(Resources.getResource("exception_test.json"), Charsets.UTF_8);

        assertThatThrownBy(() -> new JsonExtractor(true).extractPerson(fixture))
            .isInstanceOf(JsonExtractorException.class);
    }

    @Test
    void shouldReturnEmptyPersonWhenWrongFormatInTextAndThrowExceptionIsDisabled() throws IOException {
        final String fixture = Resources.toString(Resources.getResource("exception_test.json"), Charsets.UTF_8);

        assertThat(new JsonExtractor(false).extractPerson(fixture)).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenWrongFormatInCorporateTextAndThrowExceptionIsEnabled() throws IOException {
        final String fixture = Resources.toString(Resources.getResource("exception_test.json"), Charsets.UTF_8);

        assertThatThrownBy(() -> new JsonExtractor(true).extractCorporate(fixture))
            .isInstanceOf(JsonExtractorException.class);
    }

    @Test
    void shouldReturnEmptyCorporateWhenWrongFormatInTextAndThrowExceptionIsDisabled() throws IOException {
        final String fixture = Resources.toString(Resources.getResource("exception_test.json"), Charsets.UTF_8);

        assertThat(new JsonExtractor(false).extractCorporate(fixture)).isNotPresent();
    }
}
