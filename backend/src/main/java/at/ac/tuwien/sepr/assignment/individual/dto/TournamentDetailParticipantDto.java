package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

public record TournamentDetailParticipantDto(
    Long horseId,
    String name,
    LocalDate dateOfBirth,
    Long entryNumber,
    Long roundReached
) {
}
