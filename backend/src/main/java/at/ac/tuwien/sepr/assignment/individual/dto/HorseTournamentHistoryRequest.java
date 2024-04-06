package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;
import java.util.List;

public class HorseTournamentHistoryRequest {
  private LocalDate dateOfCurrentTournament;
  private List<TournamentDetailParticipantDto> horses;

  public HorseTournamentHistoryRequest() {
  }

  public HorseTournamentHistoryRequest(List<TournamentDetailParticipantDto> horses, LocalDate dateOfCurrentTournament) {
    this.horses = horses;
    this.dateOfCurrentTournament = dateOfCurrentTournament;
  }

  public LocalDate getDateOfCurrentTournament() {
    return dateOfCurrentTournament;
  }

  public void setDateOfCurrentTournament(LocalDate dateOfCurrentTournament) {
    this.dateOfCurrentTournament = dateOfCurrentTournament;
  }

  public List<TournamentDetailParticipantDto> getHorses() {
    return horses;
  }

  public void setHorses(List<TournamentDetailParticipantDto> horses) {
    this.horses = horses;
  }
}
