package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class TournamentJdbcDao implements TournamentDao {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME = "tournament";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_SELECT_SEARCH = "SELECT "
        + "    t.id as \"id\", t.name as \"name\", t.start_date as \"start_date\""
        + "    , t.end_date as \"end_date\", t.max_participants as \"max_participants\""
        + " FROM " + TABLE_NAME + " t"
        + " WHERE (:name IS NULL OR UPPER(t.name) LIKE UPPER('%'||:name||'%'))"
        + "  AND (:startDate IS NULL OR :startDate <= t.start_date)"
        + "  AND (:endDate IS NULL OR :endDate >= t.end_date)"
        + "  AND (:maxParticipants IS NULL OR :maxParticipants = t.max_participants)";

  private static final String SQL_LIMIT_CLAUSE = " LIMIT :limit";

  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
        + " SET name = ?"
        + "  , start_date = ?"
        + "  , end_date = ?"
        + "  , max_participants = ?"
        + " WHERE id = ?";


  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  public TournamentJdbcDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
  public Collection<Tournament> search(TournamentSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var query = SQL_SELECT_SEARCH;
    if (searchParameters.limit() != null) {
      query += SQL_LIMIT_CLAUSE;
    }
    var params = new BeanPropertySqlParameterSource(searchParameters);

    return jdbcNamed.query(query, params, this::mapRow);
  }

  @Override
  public Tournament update(TournamentDetailDto tournament) throws NotFoundException {
    return null;
  }

  @Override
  public Tournament getById(long id) throws NotFoundException {
    return null;
  }

  @Override
  public Tournament create(TournamentDetailDto tournament) {
    return null;
  }

  @Override
  public void delete(long id) throws NotFoundException {

  }

  private Tournament mapRow(ResultSet result, int rownum) throws SQLException {
    return new Tournament()
            .setId(result.getLong("id"))
            .setName(result.getString("name"))
            .setStartDate(result.getDate("start_date").toLocalDate())
            .setEndDate(result.getDate("end_date").toLocalDate())
            .setMaxParticipants(result.getInt("max_participants"))
            ;
  }
}
