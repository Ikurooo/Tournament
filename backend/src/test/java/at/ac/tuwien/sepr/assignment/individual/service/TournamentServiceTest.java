package at.ac.tuwien.sepr.assignment.individual.service;

import static at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants.expectedHorses;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants.expectedParticipants;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class TournamentServiceTest extends TestBase {

  @Autowired
  TournamentService tournamentService;

  @Autowired
  TournamentMapper tournamentMapper;

  @Test
  public void searchByNameFindsOneTournament() {
    var searchDto = new TournamentSearchDto("Star Cup", null, null, null);
    var tournaments = tournamentService.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments)
        .extracting("id", "name", "startDate", "endDate")
        .as("ID, Name, Start Date, End Date")
        .containsExactly(
            tuple(-2L, "Star Cup", LocalDate.of(2003, 5, 15), LocalDate.of(2004, 7, 20))
        );
  }

  @Test
  public void searchByStartDateBetween2009_06_20And2012_12_28ReturnsThreeTournaments() {
    var searchDto = new TournamentSearchDto(null, LocalDate.of(2009, 6, 20), LocalDate.of(2012, 12, 28), null);
    var tournaments = tournamentService.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments)
        .hasSize(2)
        .extracting("id", "name", "startDate", "endDate")
        .containsExactlyInAnyOrder(
            tuple(-5L, "Special Cup", LocalDate.of(2009, 6, 20), LocalDate.of(2010, 8, 18)),
            tuple(-6L, "Shell Cup", LocalDate.of(2011, 10, 5), LocalDate.of(2012, 12, 28))
        );
  }

  @Test
  public void createInvalidTournamentLessThan8Participants() throws ConflictException, NotFoundException {
    HorseSelectionDto[] participants = {
        new HorseSelectionDto(-1L, "Wendy", LocalDate.of(2019, 8, 5)),
        new HorseSelectionDto(-2L, "Hugo", LocalDate.of(2020, 2, 20)),
        new HorseSelectionDto(-2L, "Hugo", LocalDate.of(2020, 2, 20)),
        new HorseSelectionDto(-3L, "Bella", LocalDate.of(2005, 4, 8)),
        new HorseSelectionDto(-4L, "Thunder", LocalDate.of(2008, 7, 15)),
        new HorseSelectionDto(-5L, "Luna", LocalDate.of(2012, 11, 22)),
        new HorseSelectionDto(-6L, "Apollo", LocalDate.of(2003, 9, 3)),
        new HorseSelectionDto(-7L, "Sophie", LocalDate.of(2010, 6, 18))
    };

    var tournament = new TournamentCreateDto(
        "Not8",
        LocalDate.of(2000, 1, 1),
        LocalDate.of(2001, 1, 1),
        participants);

    assertThatThrownBy(() -> tournamentService.create(tournament))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("Validation of tournament for create failed. Failed validations: Duplicate participant found: Horse ID -2.");
  }

  @Test
  public void createValidTournament() throws ConflictException, NotFoundException, ValidationException {
    HorseSelectionDto[] participants = {
        new HorseSelectionDto(-1L, "Wendy", LocalDate.of(2019, 8, 5)),
        new HorseSelectionDto(-2L, "Hugo", LocalDate.of(2020, 2, 20)),
        new HorseSelectionDto(-3L, "Bella", LocalDate.of(2005, 4, 8)),
        new HorseSelectionDto(-4L, "Thunder", LocalDate.of(2008, 7, 15)),
        new HorseSelectionDto(-5L, "Luna", LocalDate.of(2012, 11, 22)),
        new HorseSelectionDto(-6L, "Apollo", LocalDate.of(2003, 9, 3)),
        new HorseSelectionDto(-7L, "Sophie", LocalDate.of(2010, 6, 18)),
        new HorseSelectionDto(-8L, "Max", LocalDate.of(2006, 3, 27))
    };

    var toCreate = new TournamentCreateDto(
        "createValidTournament",
        LocalDate.of(2001, 1, 1),
        LocalDate.of(2002, 1, 1),
        participants
    );
    Tournament createdTournament = tournamentService.create(toCreate);
    assertArrayEquals(participants, createdTournament.getParticipants());
  }
}
