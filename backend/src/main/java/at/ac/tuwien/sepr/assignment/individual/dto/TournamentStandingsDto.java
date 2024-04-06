package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing the standings of a tournament.
 */
public record TournamentStandingsDto(
    Long id,
    String name,
    LocalDate startDate,
    TournamentDetailParticipantDto[] participants,
    TournamentStandingsTreeDto tree
) {
  public static TournamentStandingsDto createFromTournament(
      Tournament tournament,
      TournamentDetailParticipantDto[] participants,
      TournamentStandingsTreeDto tree) {
    return new TournamentStandingsDto(
        tournament.getId(),
        tournament.getName(),
        tournament.getStartDate(),
        participants,
        tree
    );
  }

  @Override
  public String toString() {
    return "TournamentStandingsDto{"
        + "id=" + id
        + ", name='" + name + '\''
        + '}';
  }
}