package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.entity.Horse;

import java.time.LocalDate;

public record TournamentDetailDto(
    String name,
    LocalDate startDate,
    LocalDate endDate,
    Horse[] participants
) {
}
