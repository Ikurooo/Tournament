package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants.expectedParticipants;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
    assertThat(horses)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrderElementsOf(expectedParticipants);
  }

  @Test
  public void createInvalidTournament() {
    var toCreate = new TournamentDetailDto(
        null,
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
    Horse[] participantArray = expectedParticipants.toArray(new Horse[0]);
    var toCreate = new TournamentDetailDto(
        null,
        "createInvalidTournament",
        LocalDate.of(2001, 1, 1),
        LocalDate.of(2002, 1, 1),
        participantArray
    );
    Tournament createdTournament = horseTourneyLinkerDao.create(toCreate);

    assertArrayEquals(expectedParticipants.toArray(), createdTournament.getParticipants());
  }
}
