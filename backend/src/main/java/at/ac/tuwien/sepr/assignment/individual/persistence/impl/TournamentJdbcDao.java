package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the {@link TournamentDao} interface using JDBC for data access.
 * Handles database operations related to tournaments.
 */
@Repository
public class TournamentJdbcDao implements TournamentDao {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME = "tournament";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_SELECT_SEARCH = "SELECT "
      + "    t.id as \"id\", t.name as \"name\", t.start_date as \"start_date\", t.end_date "
      + "as \"end_date\""
      + " FROM " + TABLE_NAME + " t"
      + " WHERE (:name IS NULL OR UPPER(t.name) LIKE UPPER(concat('%', :name, '%')))"
      + "  AND ((:startDate IS NULL AND :endDate IS NULL) OR "
      + "       (t.start_date <= :endDate AND t.end_date >= :startDate) OR"
      + "       (:startDate is NULL AND t.start_date <= :endDate) OR"
      + "       (:endDate is NULL AND t.end_date >= :startDate)"
      + "      )";

  private static final String SQL_LIMIT_CLAUSE = " LIMIT :limit";
  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  public TournamentJdbcDao(JdbcTemplate jdbcTemplate,
                           NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
  public Collection<Tournament> search(TournamentSearchDto searchParameters) throws FailedToRetrieveException {
    LOG.trace("search({})", searchParameters);
    try {
      String query = SQL_SELECT_SEARCH;
      if (searchParameters.limit() != null) {
        query += SQL_LIMIT_CLAUSE;
      }
      BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(searchParameters);

      return jdbcNamed.query(query, params, this::mapRow);
    } catch (DataAccessException e) {
      LOG.error("Failed to search tournaments: {}", e.getMessage());
      throw new FailedToRetrieveException("Failed to search tournaments", e);
    }
  }

  @Override
  public Tournament getById(long id) throws NotFoundException, FailedToRetrieveException {
    LOG.trace("getById({})", id);
    try {
      List<Tournament> tournaments = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

      if (tournaments.isEmpty()) {
        LOG.warn("Tournament with ID {} does not exist", id);
        throw new NotFoundException("No tournament with ID %d found".formatted(id));
      }

      if (tournaments.size() > 1) {
        LOG.error("Multiple tournaments with ID: {} found", id);
        throw new FatalException("Too many tournaments with ID %d found".formatted(id));
      }

      return tournaments.getFirst();
    } catch (DataAccessException e) {
      LOG.error("Failed to retrieve tournament with ID {}: {}", id, e.getMessage());
      throw new FailedToRetrieveException("Failed to retrieve tournament with ID " + id, e);
    }
  }


  private Tournament mapRow(ResultSet result, int rownum) throws SQLException {
    return new Tournament()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setStartDate(result.getDate("start_date").toLocalDate())
        .setEndDate(result.getDate("end_date").toLocalDate())
        ;
  }
}
