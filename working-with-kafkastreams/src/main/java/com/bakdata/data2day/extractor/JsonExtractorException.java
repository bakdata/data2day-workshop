package com.bakdata.data2day.extractor;

/**
 * An exception thrown when extracting information from the input JSON.
 */
public class JsonExtractorException extends RuntimeException {

    /**
     * Initializes the exception with a message.
     *
     * @param message The error message
     */
    public JsonExtractorException(final String message) {
        super(message);
    }
}
