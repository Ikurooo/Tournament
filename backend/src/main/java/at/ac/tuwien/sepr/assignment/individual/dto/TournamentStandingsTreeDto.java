package at.ac.tuwien.sepr.assignment.individual.dto;

public record TournamentStandingsTreeDto(
    TournamentDetailParticipantDto thisParticipant,
    TournamentStandingsTreeDto[] branches
) {
}
