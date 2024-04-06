package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;

import java.util.Collection;

// TODO: implement TournamentDetailParticipantDto
// TODO: implement TournamentStandingsTreeDto

/**
 * Data Transfer Object (DTO) representing the standings of a tournament.
 */
public class TournamentStandingsDto {
  private Long id;
  private String name;
  private TournamentDetailParticipantDto[] participants;
  private TournamentStandingsTreeDto tree;

  public TournamentStandingsDto() {
  }

  public TournamentStandingsDto(Tournament tournament,
                                TournamentDetailParticipantDto[] participants,
                                TournamentStandingsTreeDto tree) {
    this.id = tournament.getId();
    this.name = tournament.getName();
    this.participants = participants;
    this.tree = tree;
  }

  public TournamentStandingsDto(Long id, String name, TournamentDetailParticipantDto[] participants, TournamentStandingsTreeDto tree) {
    this.id = id;
    this.name = name;
    this.participants = participants;
    this.tree = tree;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TournamentDetailParticipantDto[] getParticipants() {
    return participants;
  }

  public void setParticipants(TournamentDetailParticipantDto[] participants) {
    this.participants = participants;
  }

  public TournamentStandingsTreeDto getTree() {
    return tree;
  }

  public void setTree(TournamentStandingsTreeDto tree) {
    this.tree = tree;
  }
}