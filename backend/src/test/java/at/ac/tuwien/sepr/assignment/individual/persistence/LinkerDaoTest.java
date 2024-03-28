package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class LinkerDaoTest extends TestBase {

  @Autowired
  HorseTourneyLinkerDao horseTourneyLinkerDao;

  @Test
  public void getHorsesAssociatedWithTournamentId() {
    var horses = horseTourneyLinkerDao.findParticipantsByTournamentId(-1);
    List<Horse> expectedHorses = Arrays.asList(
        new Horse()
            .setId(-1L)
            .setName("Wendy")
            .setSex(Sex.FEMALE)
            .setDateOfBirth(LocalDate.of(2019, 8, 5))
            .setHeight(1.40f)
            .setWeight(380)
            .setBreedId(-15L),

        new Horse()
            .setId(-2L)
            .setName("Hugo")
            .setSex(Sex.MALE)
            .setDateOfBirth(LocalDate.of(2020, 2, 20))
            .setHeight(1.20f)
            .setWeight(320)
            .setBreedId(-20L),

        new Horse()
            .setId(-3L)
            .setName("Bella")
            .setSex(Sex.FEMALE)
            .setDateOfBirth(LocalDate.of(2005, 4, 8))
            .setHeight(1.45f)
            .setWeight(550)
            .setBreedId(-1L),

        new Horse()
            .setId(-4L)
            .setName("Thunder")
            .setSex(Sex.MALE)
            .setDateOfBirth(LocalDate.of(2008, 7, 15))
            .setHeight(1.60f)
            .setWeight(600)
            .setBreedId(-2L),

        new Horse()
            .setId(-5L)
            .setName("Luna")
            .setSex(Sex.FEMALE)
            .setDateOfBirth(LocalDate.of(2012, 11, 22))
            .setHeight(1.65f)
            .setWeight(650)
            .setBreedId(-3L),

        new Horse()
            .setId(-6L)
            .setName("Apollo")
            .setSex(Sex.MALE)
            .setDateOfBirth(LocalDate.of(2003, 9, 3))
            .setHeight(1.52f)
            .setWeight(500)
            .setBreedId(-4L),

        new Horse()
            .setId(-7L)
            .setName("Sophie")
            .setSex(Sex.FEMALE)
            .setDateOfBirth(LocalDate.of(2010, 6, 18))
            .setHeight(1.70f)
            .setWeight(700)
            .setBreedId(-5L),


        new Horse()
            .setId(-8L)
            .setName("Max")
            .setSex(Sex.MALE)
            .setDateOfBirth(LocalDate.of(2006, 3, 27))
            .setHeight(1.55f)
            .setWeight(580)
            .setBreedId(-6L)
    );

    assertNotNull(horses);
    assertThat(horses)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrderElementsOf(expectedHorses);
  }
}
