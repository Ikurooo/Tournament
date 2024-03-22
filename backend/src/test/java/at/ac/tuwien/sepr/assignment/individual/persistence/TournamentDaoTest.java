package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles({"test", "datagen"})
@SpringBootTest
public class TournamentDaoTest extends TestBase {

  @Autowired
  TournamentDao tournamentDao;
  @Autowired
  TournamentMapper tournamentMapper;

  private void assertTournamentsEqual(Tournament expected, Tournament actual) {
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getStartDate(), actual.getStartDate());
    assertEquals(expected.getEndDate(), actual.getEndDate());
  }


  @Test
  public void searchByName() {
    var searchDto = new TournamentSearchDto("Banana", null, null, null);
    var tournaments = tournamentDao.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Tournament())
                .setId(-7).setName("Banana Cup")
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
                .setId(-7).setName("Banana Cup")
                .setStartDate(LocalDate.of(2013, 2, 15))
                .setEndDate(LocalDate.of(2014, 4, 30)),
            (new Tournament())
                .setId(-6).setName("Shell Cup")
                .setStartDate(LocalDate.of(2011, 10, 5))
                .setEndDate(LocalDate.of(2012, 12, 28))
        );
  }

  @Test
  public void insertion() {
    var baldBalls = new Tournament()
        .setName("Bald Balls")
        .setStartDate(LocalDate.of(2013, 1, 1))
        .setEndDate(LocalDate.of(2014, 6, 18));

    var rogerTogger = new Tournament()
        .setName("Roger Togger")
        .setStartDate(LocalDate.of(2015, 1, 1))
        .setEndDate(LocalDate.of(2015, 6, 18));

    var billyWilly = new Tournament()
        .setName("Billy Willy")
        .setStartDate(LocalDate.of(2017, 1, 1))
        .setEndDate(LocalDate.of(2018, 6, 18));

    var createdBaldBalls = tournamentDao.create(tournamentMapper.entityToDetailDto(baldBalls));
    var createdRogerTogger = tournamentDao.create(tournamentMapper.entityToDetailDto(rogerTogger));
    var createdBillyWilly = tournamentDao.create(tournamentMapper.entityToDetailDto(billyWilly));

    assertTournamentsEqual(baldBalls, createdBaldBalls);
    assertTournamentsEqual(rogerTogger, createdRogerTogger);
    assertTournamentsEqual(billyWilly, createdBillyWilly);
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
                .setId(-1).setName("Rainbow Road")
                .setStartDate(LocalDate.of(2001, 1, 1))
                .setEndDate(LocalDate.of(2002, 3, 2)),
            (new Tournament())
                .setId(-2).setName("Star Cup")
                .setStartDate(LocalDate.of(2003, 5, 15))
                .setEndDate(LocalDate.of(2004, 7, 20))
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
                .setId(-8).setName("Leaf Cup")
                .setStartDate(LocalDate.of(2015, 6, 25))
                .setEndDate(LocalDate.of(2016, 8, 22)),
            (new Tournament())
                .setId(-9).setName("Lightning Cup")
                .setStartDate(LocalDate.of(2017, 10, 10))
                .setEndDate(LocalDate.of(2018, 12, 15))
        );
  }
}
