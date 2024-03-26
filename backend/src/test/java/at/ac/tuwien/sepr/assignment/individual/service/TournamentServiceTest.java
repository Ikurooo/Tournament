package at.ac.tuwien.sepr.assignment.individual.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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

}
