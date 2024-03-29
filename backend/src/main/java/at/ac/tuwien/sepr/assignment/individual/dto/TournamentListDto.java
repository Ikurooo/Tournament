package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;


/**
 * Data Transfer Object (DTO) representing summarized information about a tournament.
 */
public record TournamentListDto(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate
) {
}
