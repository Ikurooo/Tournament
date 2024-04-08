package at.ac.tuwien.sepr.assignment.individual.dto;

/**
 * Data Transfer Object (DTO) representing the standings tree of a tournament.
 */
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

  @Override
  public String toString() {
    String thisParticipantString = (thisParticipant != null) ? thisParticipant.toString() : "null";
    if (branches == null) {
      return "";
    }
    String leftBranchString = (branches[0] != null) ? branches[0].toString() : "";
    String rightBranchString = (branches[1] != null) ? branches[1].toString() : "";

    return thisParticipantString + "{" + leftBranchString + "}" + "{" + rightBranchString + "}";
  }
}
