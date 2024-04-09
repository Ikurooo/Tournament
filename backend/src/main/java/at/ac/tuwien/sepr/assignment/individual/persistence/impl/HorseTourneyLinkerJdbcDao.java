package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToUpdateException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseTourneyLinkerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of the {@link HorseTourneyLinkerDao} interface using JDBC for data access.
 * Handles database operations related to both tournaments and horses.
 */
@Repository
public class HorseTourneyLinkerJdbcDao implements HorseTourneyLinkerDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String HORSE_TABLE_NAME = "horse";
  private static final String TOURNAMENT_TABLE_NAME = "tournament";
  private static final String LINKER_TABLE_NAME = "horse_tourney_linker";
  private static final String INSERT_NEW_TOURNAMENT = "INSERT INTO " + LINKER_TABLE_NAME + " "
      + "(horse_id, tournament_id, round_reached, entry_number) VALUES (?, ?, null, null)";
  private static final String UPDATE_STANDINGS_FOR_HORSE_IN_TOURNAMENT = "UPDATE " + LINKER_TABLE_NAME
      + " SET round_reached = ?"
      + " , entry_number = ?"
      + " WHERE horse_id = ?"
      + " AND tournament_id = ?";
  // TODO that collection sending thing is wild check if it breaks the thingy somewhere UwU
  private static final String FIND_ROUNDS_REACHED_FOR_PAST_YEAR = "SELECT h.name, h.id, l.round_reached, l.entry_number"
      + " FROM " + LINKER_TABLE_NAME + " l"
      + " JOIN " + TOURNAMENT_TABLE_NAME + " t ON l.tournament_id = t.id"
      + " JOIN " + HORSE_TABLE_NAME + " h ON l.horse_id = h.id"
      + " WHERE l.horse_id = ?"
      + " AND t.end_date > ?"
      + " AND t.end_date < ?";
  private static final String FIND_PARTICIPANTS_BY_TOURNAMENT_ID = "SELECT * "
      + "FROM " + HORSE_TABLE_NAME + " h "
      + "JOIN " + LINKER_TABLE_NAME + " l ON h.id = l.horse_id "
      + "WHERE l.tournament_id = ?";
  private static final String FIND_TOURNAMENT_BY_PARTICIPANT_ID =
      "SELECT t.* "
          + "FROM " + TOURNAMENT_TABLE_NAME + " t "
          + "JOIN " + LINKER_TABLE_NAME + " l ON t.id = l.tournament_id "
          + "WHERE l.horse_id = ?";
  private final JdbcTemplate jdbcTemplate;

  public HorseTourneyLinkerJdbcDao(
      JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Transactional
  @Override
  public Collection<TournamentDetailParticipantDto> updateStandings(Collection<TournamentDetailParticipantDto> horses,
                                                                    long tournamentId) throws FailedToUpdateException {
    LOG.trace("updateStandings({}, {})", horses, tournamentId);
    try {
      horses.forEach(horse -> jdbcTemplate.update(UPDATE_STANDINGS_FOR_HORSE_IN_TOURNAMENT,
          horse.getRoundReached(), horse.getEntryNumber(), horse.getHorseId(), tournamentId)
      );
      return horses;
    } catch (DataAccessException e) {
      LOG.error("Failed to update a standings of tournament tournament: {}", e.getMessage());
      throw new FailedToUpdateException("Failed to update a standings of tournament tournament due to a database error.");
    }
  }

  @Override
  public Collection<TournamentDetailParticipantDto> getHorseDetailsForPastYear(TournamentDetailParticipantDto horse,
                                                                               LocalDate dateOfCurrentTournament)
      throws FailedToRetrieveException {
    LOG.trace("getHorseDetailsForPastYear({}, {})", horse, dateOfCurrentTournament);
    try {
      var horseId = horse.getHorseId();
      var oneYearPrior = dateOfCurrentTournament.minusYears(1);
      return jdbcTemplate.query(FIND_ROUNDS_REACHED_FOR_PAST_YEAR,
          this::mapRowRoundReached,
          horseId,
          oneYearPrior,
          dateOfCurrentTournament);
    } catch (DataAccessException e) {
      LOG.error("Failed to find the rounds reach for the participant with ID {}: {}", horse.getHorseId(), e.getMessage());
      throw new FailedToRetrieveException("Failed to find the rounds reach for the participant with ID " + horse.getHorseId(), e);
    }
  }

  @Transactional
  @Override
  public Tournament create(TournamentCreateDto tournament) throws FailedToCreateException {
    LOG.trace("create({})", tournament);
    KeyHolder keyHolder = new GeneratedKeyHolder();

    int rowsAffectedTournament = jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO " + TOURNAMENT_TABLE_NAME + " (name, start_date, end_date) VALUES (?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, tournament.name());
      ps.setDate(2, java.sql.Date.valueOf(tournament.startDate()));
      ps.setDate(3, java.sql.Date.valueOf(tournament.endDate()));
      return ps;
    }, keyHolder);

    if (rowsAffectedTournament != 1) {
      LOG.error("Failed to insert a new tournament. Number of rows affected: {}", rowsAffectedTournament);
      throw new FailedToCreateException("Failed to create a new tournament. No records were inserted.");
    }

    long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();

    for (HorseSelectionDto horse : tournament.participants()) {
      LOG.debug("Horse Details: {}", horse);
      int rowsAffectedLinker = jdbcTemplate.update(INSERT_NEW_TOURNAMENT, horse.id(), generatedId);

      if (rowsAffectedLinker < 1) {
        String errorMessage = String.format("Failed to link horse (ID: %d) with tournament (ID: %d)", horse.id(), generatedId);
        throw new FailedToCreateException(errorMessage);

      }
    }
    return new Tournament()
        .setId(generatedId)
        .setName(tournament.name())
        .setStartDate(tournament.startDate())
        .setEndDate(tournament.endDate())
        .setParticipants(tournament.participants());

  }

  @Override
  public Collection<TournamentDetailParticipantDto> findParticipantsByTournamentId(long id) throws FailedToRetrieveException {
    LOG.trace("findParticipantsByTournamentId({})", id);
    return jdbcTemplate.query(FIND_PARTICIPANTS_BY_TOURNAMENT_ID, this::mapRowHorse, id);
  }

  @Override
  public List<Tournament> getTournamentsAssociatedWithHorseId(long id) throws FailedToRetrieveException {
    LOG.trace("getTournamentsAssociatedWithHorseId({})", id);
    return jdbcTemplate.query(FIND_TOURNAMENT_BY_PARTICIPANT_ID, this::mapRowTournament, id);
  }

  private TournamentDetailParticipantDto mapRowRoundReached(ResultSet result, int rownum) throws SQLException {
    return new TournamentDetailParticipantDto()
        .setHorseId(result.getLong("id"))
        .setRoundReached(result.getLong("round_reached"))
        .setEntryNumber(result.getLong("entry_number"))
        .setName(result.getString("name"))
        ;
  }

  private TournamentDetailParticipantDto mapRowHorse(ResultSet result, int rownum) throws SQLException {
    return new TournamentDetailParticipantDto()
        .setHorseId(result.getLong("id"))
        .setName(result.getString("name"))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setRoundReached(result.getLong("round_reached"))
        .setEntryNumber(result.getLong("entry_number"))
        ;
  }

  private Tournament mapRowTournament(ResultSet result, int rownum) throws SQLException {
    return new Tournament()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setStartDate(result.getDate("start_date").toLocalDate())
        .setEndDate(result.getDate("end_date").toLocalDate());
  }
}
