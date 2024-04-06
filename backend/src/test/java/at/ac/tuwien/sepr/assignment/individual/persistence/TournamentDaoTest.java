package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles({"test", "datagen"})
@SpringBootTest
public class TournamentDaoTest extends TestBase {
  @Autowired
  TournamentDao tournamentDao;
  @Autowired
  TournamentMapper tournamentMapper;

  @Test
  public void searchByName() {
    var searchDto = new TournamentSearchDto("Banana", null, null, null);
    var tournaments = tournamentDao.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Tournament())
                .setId(-7L).setName("Banana Cup")
                .setStartDate(LocalDate.of(2013, 2, 15))
                .setEndDate(LocalDate.of(2014, 4, 30))
        );
  }

  @Test
  public void searchBetweenTwoDays() {
    var searchDto = new TournamentSearchDto(null,
        LocalDate.of(2011, 1, 1),
        LocalDate.of(2015, 1, 1),
        null);
    var tournaments = tournamentDao.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Tournament())
                .setId(-7L).setName("Banana Cup")
                .setStartDate(LocalDate.of(2013, 2, 15))
                .setEndDate(LocalDate.of(2014, 4, 30)),
            (new Tournament())
                .setId(-6L).setName("Shell Cup")
                .setStartDate(LocalDate.of(2011, 10, 5))
                .setEndDate(LocalDate.of(2012, 12, 28))
        );
  }

  @Test
  public void onlyEndDateGivenTest() {
    var searchDto = new TournamentSearchDto(
        null,
        null,
        LocalDate.of(2003, 12, 31),
        null);
    var tournaments = tournamentDao.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Tournament())
                .setId(-1L).setName("Rainbow Road")
                .setStartDate(LocalDate.of(2001, 1, 1))
                .setEndDate(LocalDate.of(2002, 3, 2)),
            (new Tournament())
                .setId(-2L).setName("Star Cup")
                .setStartDate(LocalDate.of(2003, 5, 15))
                .setEndDate(LocalDate.of(2004, 7, 20)),
            new Tournament().setId(-10L).setName("Borderline Schizophrenic Cup")
                .setStartDate(LocalDate.of(1999, 1, 1))
                .setEndDate(LocalDate.of(2000, 3, 3))
        );
  }

  @Test
  public void onlyStartDateGivenTest() {
    var searchDto = new TournamentSearchDto(
        null,
        LocalDate.of(2016, 1, 1),
        null,
        null);
    var tournaments = tournamentDao.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Tournament())
                .setId(-8L).setName("Leaf Cup")
                .setStartDate(LocalDate.of(2015, 6, 25))
                .setEndDate(LocalDate.of(2016, 8, 22)),
            (new Tournament())
                .setId(-9L).setName("Lightning Cup")
                .setStartDate(LocalDate.of(2017, 10, 10))
                .setEndDate(LocalDate.of(2018, 12, 15))
        );
  }

  @Test
  public void getTournamentWithInvalidId() {
    long invalidId = 42069L;
    NotFoundException exception = assertThrows(NotFoundException.class, () -> {
      tournamentDao.getById(invalidId);
    });

    assertEquals("No tournament with ID " + invalidId + " found", exception.getMessage());
  }

  @Test
  public void getTournamentWithValidId() throws NotFoundException {
    long validId = -8L;

    var tournament = tournamentDao.getById(validId);
    assertThat(tournament)
        .usingRecursiveComparison()
        .isEqualTo(new Tournament()
            .setId(-8L)
            .setName("Leaf Cup")
            .setStartDate(LocalDate.of(2015, 6, 25))
            .setEndDate(LocalDate.of(2016, 8, 22)));
  }
}
