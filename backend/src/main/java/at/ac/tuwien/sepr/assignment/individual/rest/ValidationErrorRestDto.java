package at.ac.tuwien.sepr.assignment.individual.rest;

import java.util.List;

/**
 * A DTO class representing validation errors in a RESTful API response.
 * This class encapsulates a message describing the overall validation
 * error and a list of individual error messages.
 */
public record ValidationErrorRestDto(String message, List<String> errors) {
}

