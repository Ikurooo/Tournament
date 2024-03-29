package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.entity.Horse;

// TODO: implement TournamentDetailParticipantDto
// TODO: implement TournamentStandingsTreeDto
/**
 * Data Transfer Object (DTO) representing the standings of a tournament.
 */
public record TournamentStandingsDto(
    Long id,
    String name,
    Horse[] participants
// TournamentStandingsTreeDto tree
) {

}