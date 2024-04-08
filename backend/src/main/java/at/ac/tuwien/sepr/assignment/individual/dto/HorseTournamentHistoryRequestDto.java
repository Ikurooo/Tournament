package at.ac.tuwien.sepr.assignment.individual.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) for requesting horse details in s specific timeframe.
 */
public class HorseTournamentHistoryRequestDto {
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dateOfCurrentTournament;
  private List<TournamentDetailParticipantDto> horses;

  public HorseTournamentHistoryRequestDto() {
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
