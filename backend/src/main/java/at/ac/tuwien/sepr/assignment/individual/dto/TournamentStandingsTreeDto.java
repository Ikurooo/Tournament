package at.ac.tuwien.sepr.assignment.individual.dto;

public class TournamentStandingsTreeDto {

  private TournamentDetailParticipantDto thisParticipant;
  private TournamentStandingsTreeDto[] branches;

  public TournamentStandingsTreeDto() {

  }

  public TournamentDetailParticipantDto getThisParticipant() {
    return thisParticipant;
  }

  public void setThisParticipant(TournamentDetailParticipantDto thisParticipant) {
    this.thisParticipant = thisParticipant;
  }

  public TournamentStandingsTreeDto[] getBranches() {
    return branches;
  }

  public void setBranches(TournamentStandingsTreeDto[] branches) {
    this.branches = branches;
  }
}
