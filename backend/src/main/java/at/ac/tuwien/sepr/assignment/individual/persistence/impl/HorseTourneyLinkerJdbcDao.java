package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseTourneyLinkerDao;
import at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Objects;

@Repository
public class HorseTourneyLinkerJdbcDao implements HorseTourneyLinkerDao {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String HORSE_TABLE_NAME = "horse";
  private static final String TOURNAMENT_TABLE_NAME = "tournament";
  private static final String LINKER_TABLE_NAME = "horse_tourney_linker";
  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  public HorseTourneyLinkerJdbcDao(
      NamedParameterJdbcTemplate jdbcNamed,
      JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }


  @Transactional
  @Override
  public TournamentDetailDto create(TournamentDetailDto tournament) {
    LOG.trace("create({})", tournament);

    try {
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

      TournamentDetailDto createdTournament = new TournamentDetailDto(
          generatedId,
          tournament.name(),
          tournament.startDate(),
          tournament.endDate(),
          tournament.participants()
      );

      for (Horse horse : tournament.participants()) {
        LOG.debug("Horse Details: {}", horse);
        int rowsAffectedLinker = jdbcTemplate.update("INSERT INTO " + LINKER_TABLE_NAME
                + " (horse_id, tournament_id) VALUES (?, ?)",
            horse.getId(), createdTournament.id());

        if (rowsAffectedLinker < 1) {
          String errorMessage = String.format("Failed to link horse (ID: %d) with tournament (ID: %d)", horse.getId(), createdTournament.id());
          LOG.warn(errorMessage);
          throw new FailedToCreateException(errorMessage);

        }
      }
      return createdTournament;

    } catch (DataAccessException e) {
      LOG.error("Failed to insert a new tournament: {}", e.getMessage());
      throw new FailedToCreateException("Failed to create a new tournament due to a database error.");
    } catch (Exception e) {
      LOG.error("Failed to insert a new tournament: {}", e.getMessage());
      throw new FailedToCreateException("Failed to create a new tournament.");
    }
  }

  public List<Horse> findParticipantsByTournamentId(long id) {
    String sql = "SELECT h.* "
        + "FROM " + HORSE_TABLE_NAME + " h "
        + "JOIN " + LINKER_TABLE_NAME + " l ON h.id = l.horse_id "
        + "WHERE l.tournament_id = ?";

    return jdbcTemplate.query(sql, this::mapRow, id);
  }

  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setSex(Sex.valueOf(result.getString("sex")))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setHeight(result.getFloat("height"))
        .setWeight(result.getFloat("weight"))
        .setBreedId(result.getObject("breed_id", Long.class))
        ;
  }
}
