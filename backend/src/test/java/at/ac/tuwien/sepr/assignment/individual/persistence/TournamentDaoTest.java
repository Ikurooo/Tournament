package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles({"test", "datagen"})
@SpringBootTest
public class TournamentDaoTest extends TestBase {

  @Autowired
  TournamentDao tournamentDao;

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
        LocalDate.of(2001, 1, 1),
        LocalDate.of(2005, 12, 31),
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
                .setEndDate(LocalDate.of(2004, 7, 20)),
            (new Tournament())
                .setId(-3).setName("Mushroom Cup")
                .setStartDate(LocalDate.of(2005, 9, 10))
                .setEndDate(LocalDate.of(2006, 11, 25))
        );
  }
}
