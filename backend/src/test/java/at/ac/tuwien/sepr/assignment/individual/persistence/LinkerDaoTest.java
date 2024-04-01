package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants.expectedHorses;
import static at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants.expectedParticipants;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class LinkerDaoTest extends TestBase {

  @Autowired
  HorseTourneyLinkerDao horseTourneyLinkerDao;

  @Test
  public void getTournamentsAssociatedWithNonExistentHorseId() {
    long nonExistentHorseId = -1L;
    List<Tournament> expectedTournaments = Arrays.asList(
        new Tournament()
            .setId(-1)
            .setName("Rainbow Road")
            .setStartDate(LocalDate.of(2001, 1, 1))
            .setEndDate(LocalDate.of(2002, 3, 2)),
        new Tournament()
            .setId(-2)
            .setName("Star Cup")
            .setStartDate(LocalDate.of(2003, 5, 15))
            .setEndDate(LocalDate.of(2004, 7, 20))
    );
    List<Tournament> tournaments = horseTourneyLinkerDao.getTournamentsAssociatedWithHorseId(nonExistentHorseId);
    assertNotNull(tournaments);
    assertThat(tournaments)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrderElementsOf(expectedTournaments);
  }

  @Test
  public void getHorsesAssociatedWithTournamentId() {
    var horses = horseTourneyLinkerDao.findParticipantsByTournamentId(-1);
    assertNotNull(horses);
    AssertionsForClassTypes.assertThat(horses.toArray())
        .extracting("horseId", "name", "dateOfBirth")
        .containsExactlyInAnyOrder(
            tuple(-1L, "Wendy", LocalDate.of(2019, 8, 5)),
            tuple(-2L, "Hugo", LocalDate.of(2020, 2, 20)),
            tuple(-3L, "Bella", LocalDate.of(2005, 4, 8)),
            tuple(-4L, "Thunder", LocalDate.of(2008, 7, 15)),
            tuple(-5L, "Luna", LocalDate.of(2012, 11, 22)),
            tuple(-6L, "Apollo", LocalDate.of(2003, 9, 3)),
            tuple(-7L, "Sophie", LocalDate.of(2010, 6, 18)),
            tuple(-8L, "Max", LocalDate.of(2006, 3, 27))
        );
  }

  @Test
  public void createInvalidTournament() {
    var toCreate = new TournamentCreateDto(
        "createInvalidTournament",
        LocalDate.of(2001, 1, 1),
        LocalDate.of(2002, 1, 1),
        null
    );

    assertThatThrownBy(() -> horseTourneyLinkerDao.create(toCreate))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  public void createValidTournament() {

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
        "createInvalidTournament",
        LocalDate.of(2001, 1, 1),
        LocalDate.of(2002, 1, 1),
        participants
    );
    Tournament createdTournament = horseTourneyLinkerDao.create(toCreate);

    assertArrayEquals(participants, createdTournament.getParticipants());
  }
}
