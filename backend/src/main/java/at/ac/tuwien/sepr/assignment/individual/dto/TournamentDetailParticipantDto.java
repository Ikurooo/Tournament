package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing detailed information about a participant in a tournament.
 */
public record TournamentDetailParticipantDto(
    Long horseId,
    String name,
    LocalDate dateOfBirth,
    Long entryNumber,
    Long roundReached
) {
}
