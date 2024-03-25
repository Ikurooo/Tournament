package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class TournamentEndpointTest extends TestBase {
  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  @Test
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/asdf123")
        ).andExpect(status().isNotFound());
  }

  @Test
  public void gettingAllTournaments() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/tournaments")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<TournamentListDto> tournamentResult = objectMapper.readerFor(TournamentListDto.class)
        .<TournamentListDto>readValues(body).readAll();

    assertThat(tournamentResult).isNotNull();
    assertThat(tournamentResult)
        .hasSize(9) // Number of tournaments inserted
        .extracting(TournamentListDto::id, TournamentListDto::name, TournamentListDto::startDate, TournamentListDto::endDate)
        .contains(
            tuple(-1L, "Rainbow Road", LocalDate.of(2001, 1, 1), LocalDate.of(2002, 3, 2)),
            tuple(-2L, "Star Cup", LocalDate.of(2003, 5, 15), LocalDate.of(2004, 7, 20)),
            tuple(-3L, "Mushroom Cup", LocalDate.of(2005, 9, 10), LocalDate.of(2006, 11, 25)),
            tuple(-4L, "Flower Cup", LocalDate.of(2007, 1, 30), LocalDate.of(2008, 4, 12)),
            tuple(-5L, "Special Cup", LocalDate.of(2009, 6, 20), LocalDate.of(2010, 8, 18)),
            tuple(-6L, "Shell Cup", LocalDate.of(2011, 10, 5), LocalDate.of(2012, 12, 28)),
            tuple(-7L, "Banana Cup", LocalDate.of(2013, 2, 15), LocalDate.of(2014, 4, 30)),
            tuple(-8L, "Leaf Cup", LocalDate.of(2015, 6, 25), LocalDate.of(2016, 8, 22)),
            tuple(-9L, "Lightning Cup", LocalDate.of(2017, 10, 10), LocalDate.of(2018, 12, 15))
        );
  }

  @Test
  public void searchByTournamentNameFindsOneTournament() throws Exception {
    var body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/tournaments")
            .queryParam("name", "Rainbow Road")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    var tournamentsIterator = objectMapper.readerFor(TournamentListDto.class)
        .<TournamentListDto>readValues(body);
    assertNotNull(tournamentsIterator);
    var tournaments = new ArrayList<TournamentListDto>();
    tournamentsIterator.forEachRemaining(tournaments::add);

    assertThat(tournaments)
        .extracting("id", "name", "startDate", "endDate")
        .as("ID, Name, Start Date, End Date")
        .containsExactly(
            tuple(-1L, "Rainbow Road", LocalDate.of(2001, 1, 1), LocalDate.of(2002, 3, 2))
        );
  }

  @Test
  public void searchByStartDateBetween2001_01_01And2001_01_01ReturnsOneTournament() throws Exception {
    var body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/tournaments")
            .queryParam("startDate", LocalDate.of(2001, 1, 1).toString())  // Earliest date in the insertions
            .queryParam("endDate", LocalDate.of(2002, 1, 1).toString())     // Changed to include all tournaments
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    var tournamentsResult = objectMapper.readerFor(TournamentListDto.class)
        .<TournamentListDto>readValues(body);
    assertNotNull(tournamentsResult);

    var tournaments = new ArrayList<TournamentListDto>();
    tournamentsResult.forEachRemaining(tournaments::add);

    assertThat(tournaments)
        .hasSize(1)
        .extracting(TournamentListDto::id, TournamentListDto::name, TournamentListDto::startDate, TournamentListDto::endDate)
        .containsExactlyInAnyOrder(
            tuple(-1L, "Rainbow Road", LocalDate.of(2001, 1, 1), LocalDate.of(2002, 3, 2))
        );
  }


}
