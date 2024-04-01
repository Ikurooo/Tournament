package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

public record TournamentCreateDto(
    String name,
    LocalDate startDate,
    LocalDate endDate,
    HorseSelectionDto[] participants
) {
}
