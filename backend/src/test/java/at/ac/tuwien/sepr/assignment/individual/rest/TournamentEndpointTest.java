package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants.expectedHorses;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants.expectedParticipants;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class TournamentEndpointTest extends TestBase {
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  private WebApplicationContext webAppContext;
  @Autowired
  private TournamentMapper tournamentMapper;
  private MockMvc mockMvc;

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
  public void getInvalidTournamentIdThrowsNotFoundException() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
            .get("/tournaments/42069")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getTournamentAndThe8HorsesThatBelongToIt() throws Exception {
    byte[] body = mockMvc.perform(MockMvcRequestBuilders
            .get("/tournaments/-1")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    TournamentStandingsDto tournamentStandingsDto = objectMapper.readValue(body, TournamentStandingsDto.class);
    assertNotNull(tournamentStandingsDto);
    assertNotNull(tournamentStandingsDto.participants());
    assertEquals(8, tournamentStandingsDto.participants().length);

    AssertionsForClassTypes.assertThat(tournamentStandingsDto.participants())
        .extracting("horseId", "name", "dateOfBirth")
        .containsExactlyInAnyOrder(
            AssertionsForClassTypes.tuple(-1L, "Wendy", LocalDate.of(2019, 8, 5)),
            AssertionsForClassTypes.tuple(-2L, "Hugo", LocalDate.of(2020, 2, 20)),
            AssertionsForClassTypes.tuple(-3L, "Bella", LocalDate.of(2005, 4, 8)),
            AssertionsForClassTypes.tuple(-4L, "Thunder", LocalDate.of(2008, 7, 15)),
            AssertionsForClassTypes.tuple(-5L, "Luna", LocalDate.of(2012, 11, 22)),
            AssertionsForClassTypes.tuple(-6L, "Apollo", LocalDate.of(2003, 9, 3)),
            AssertionsForClassTypes.tuple(-7L, "Sophie", LocalDate.of(2010, 6, 18)),
            AssertionsForClassTypes.tuple(-8L, "Max", LocalDate.of(2006, 3, 27))
        );
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
            .queryParam("startDate", LocalDate.of(2001, 1, 1).toString())
            .queryParam("endDate", LocalDate.of(2002, 1, 1).toString())
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

  @Test
  public void createValidTournament() throws Exception {
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
    TournamentCreateDto originalTournament = new TournamentCreateDto(
        "createValidTournament",
        LocalDate.of(2001, 1, 1),
        LocalDate.of(2002, 1, 1),
        participants
    );

    String requestBody = objectMapper.writeValueAsString(originalTournament);

    var result = mockMvc.perform(MockMvcRequestBuilders
            .post("/tournaments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    Tournament tournamentResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Tournament.class);

    assertEquals(originalTournament.name(), tournamentResponse.getName());
    assertEquals(originalTournament.startDate(), tournamentResponse.getStartDate());
    assertEquals(originalTournament.endDate(), tournamentResponse.getEndDate());

    assertThat(tournamentResponse.getParticipants())
        .extracting("id", "name", "dateOfBirth")
        .containsOnly(
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
