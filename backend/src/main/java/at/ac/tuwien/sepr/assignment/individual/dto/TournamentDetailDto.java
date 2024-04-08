package at.ac.tuwien.sepr.assignment.individual.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing detailed information about a tournament.
 */
public record TournamentDetailDto(
    Long id,
    String name,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDate,
    TournamentDetailParticipantDto[] participants
) {
}
