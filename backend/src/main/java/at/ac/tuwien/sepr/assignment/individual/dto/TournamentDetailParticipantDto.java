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

  public TournamentDetailParticipantDto setHorseId(Long horseId) {
    this.horseId = horseId;
    return this;
  }

  public TournamentDetailParticipantDto setName(String name) {
    this.name = name;
    return this;
  }

  public TournamentDetailParticipantDto setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  public TournamentDetailParticipantDto setEntryNumber(Long entryNumber) {
    this.entryNumber = entryNumber;
    return this;
  }

  public TournamentDetailParticipantDto setRoundReached(Long roundReached) {
    this.roundReached = roundReached;
    return this;
  }

  public Long getHorseId() {
    return horseId;
  }

  public String getName() {
    return name;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public Long getEntryNumber() {
    return entryNumber;
  }

  public Long getRoundReached() {
    return roundReached;
  }
}