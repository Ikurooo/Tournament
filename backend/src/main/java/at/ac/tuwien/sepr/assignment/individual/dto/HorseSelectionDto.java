package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing basic information about a horse for selection purposes.
 */
public record HorseSelectionDto(
    long id,
    String name,
    LocalDate dateOfBirth
) {
}
