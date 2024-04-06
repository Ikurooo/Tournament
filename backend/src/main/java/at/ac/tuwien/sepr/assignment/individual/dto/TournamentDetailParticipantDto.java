package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing detailed information about a participant in a tournament.
 */
public class TournamentDetailParticipantDto {
  private Long horseId;
  private String name;
  private LocalDate dateOfBirth;
  private Long entryNumber;
  private Long roundReached;

  public TournamentDetailParticipantDto() {
  }

  public TournamentDetailParticipantDto(Long horseId, String name, LocalDate dateOfBirth, Long entryNumber, Long roundReached) {
    this.horseId = horseId;
    this.name = name;
    this.dateOfBirth = dateOfBirth;
    this.entryNumber = entryNumber;
    this.roundReached = roundReached;
  }

  public Long getHorseId() {
    return horseId;
  }

  public TournamentDetailParticipantDto setHorseId(Long horseId) {
    this.horseId = horseId;
    return this;
  }

  public String getName() {
    return name;
  }

  public TournamentDetailParticipantDto setName(String name) {
    this.name = name;
    return this;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public TournamentDetailParticipantDto setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  public Long getEntryNumber() {
    return entryNumber;
  }

  public TournamentDetailParticipantDto setEntryNumber(Long entryNumber) {
    this.entryNumber = entryNumber;
    return this;
  }

  public Long getRoundReached() {
    return roundReached;
  }

  public TournamentDetailParticipantDto setRoundReached(Long roundReached) {
    this.roundReached = roundReached;
    return this;
  }

  @Override
  public String toString() {
    return "TournamentDetailParticipantDto{"
        + "horseId=" + horseId
        + ", name='" + name + '\''
        + '}';
  }
}