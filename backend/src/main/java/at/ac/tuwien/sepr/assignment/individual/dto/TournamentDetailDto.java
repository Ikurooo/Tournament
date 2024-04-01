package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.entity.Horse;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing detailed information about a tournament.
 */
public record TournamentDetailDto(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    TournamentDetailParticipantDto[] participants
) {
}
