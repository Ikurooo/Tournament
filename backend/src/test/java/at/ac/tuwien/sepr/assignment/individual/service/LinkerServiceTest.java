package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class LinkerServiceTest extends TestBase {
  @Autowired
  LinkerService linkerService;

  @Test
  public void getTournamentAndThe8HorsesThatBelongToIt() throws NotFoundException {
    var tournament = linkerService.getById(-1L);
    assertNotNull(tournament);
    assertNotNull(tournament.participants());
    assertEquals(8, tournament.participants().length);
    assertThat(tournament.participants())
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
}
