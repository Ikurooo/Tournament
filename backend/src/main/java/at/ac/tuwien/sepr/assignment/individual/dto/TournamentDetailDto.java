package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

public record TournamentDetailDto(
    String name,
    LocalDate startDate,
    LocalDate endDate
) {
}
