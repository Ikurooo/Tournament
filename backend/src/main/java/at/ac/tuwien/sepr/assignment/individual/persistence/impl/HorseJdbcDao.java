package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToDeleteException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToUpdateException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the {@link HorseDao} interface using JDBC for data access.
 * Handles database operations related to horses.
 */
@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME = "horse";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_SELECT_SEARCH = "SELECT  "
      + "    h.id as \"id\", h.name as \"name\", h.sex as \"sex\", h.date_of_birth as \"date_of_birth\""
      + "    , h.height as \"height\", h.weight as \"weight\", h.breed_id as \"breed_id\""
      + " FROM " + TABLE_NAME + " h LEFT OUTER JOIN breed b ON (h.breed_id = b.id)"
      + " WHERE (:name IS NULL OR UPPER(h.name) LIKE UPPER('%'||:name||'%'))"
      + "  AND (:sex IS NULL OR :sex = sex)"
      + "  AND (:bornEarliest IS NULL OR :bornEarliest <= h.date_of_birth)"
      + "  AND (:bornLatest IS NULL OR :bornLatest >= h.date_of_birth)"
      + "  AND (:breed IS NULL OR UPPER(b.name) LIKE UPPER('%'||:breed||'%'))";

  private static final String SQL_LIMIT_CLAUSE = " LIMIT :limit";
  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
      + " SET name = ?"
      + "  , sex = ?"
      + "  , date_of_birth = ?"
      + "  , height = ?"
      + "  , weight = ?"
      + "  , breed_id = ?"
      + " WHERE id = ?";

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  public HorseJdbcDao(
      NamedParameterJdbcTemplate jdbcNamed,
      JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
  public Horse getById(long id) throws NotFoundException, FailedToRetrieveException {
    LOG.trace("getById({})", id);
    List<Horse> horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (horses.isEmpty()) {
      LOG.warn("Horse with ID {} does not exist", id);
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }

    if (horses.size() > 1) {
      LOG.error("Multiple horses with ID: {} found", id);
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }

    return horses.getFirst();

  }

  @Override
  public Horse create(HorseDetailDto horse) throws FailedToCreateException {
    LOG.trace("create({})", horse);
    KeyHolder keyHolder = new GeneratedKeyHolder();

    int update = jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("INSERT INTO " + TABLE_NAME
              + " (name, sex, date_of_birth, height, weight, breed_id)"
              + " VALUES (?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, horse.name());
      ps.setString(2, horse.sex().toString());
      ps.setDate(3, java.sql.Date.valueOf(horse.dateOfBirth()));
      ps.setDouble(4, horse.height());
      ps.setDouble(5, horse.weight());
      if (horse.breed() != null) {
        ps.setLong(6, horse.breed().id());
      } else {
        ps.setNull(6, Types.BIGINT);
      }
      return ps;
    }, keyHolder);

    if (update != 1) {
      LOG.warn("Failed to insert a new horse. Rows affected: {}", update);
      throw new FailedToCreateException("Failed to insert a new horse.");
    }

    long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();

    var breed = horse.breed() != null ? horse.breed().id() : null;

    return new Horse()
        .setId(generatedId)
        .setName(horse.name())
        .setSex(horse.sex())
        .setDateOfBirth(horse.dateOfBirth())
        .setHeight(horse.height())
        .setWeight(horse.weight())
        .setBreedId(breed);

  }

  @Override
  public void delete(long id) throws NotFoundException, FailedToDeleteException {
    LOG.trace("delete({})", id);
    int deleted = jdbcTemplate.update("DELETE FROM " + TABLE_NAME + " WHERE id = ?", id);

    if (deleted == 0) {
      LOG.warn("Failed to delete horse because horse with ID: {} does not exist", id);
      throw new NotFoundException("No horse with ID %d found for deletion".formatted(id));
    }
  }


  @Override
  public Collection<Horse> search(HorseSearchDto searchParameters) throws FailedToRetrieveException {
    LOG.trace("search({})", searchParameters);
    String query = SQL_SELECT_SEARCH;
    if (searchParameters.limit() != null) {
      query += SQL_LIMIT_CLAUSE;
    }
    BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(searchParameters);
    params.registerSqlType("sex", Types.VARCHAR);

    return jdbcNamed.query(query, params, this::mapRow);

  }

  @Override
  public Horse update(HorseDetailDto horse) throws NotFoundException, FailedToUpdateException {
    LOG.trace("update({})", horse);

    var breed = horse.breed() != null ? horse.breed().id() : null;

    int updated = jdbcTemplate.update(SQL_UPDATE,
        horse.name(),
        horse.sex().toString(),
        horse.dateOfBirth(),
        horse.height(),
        horse.weight(),
        breed,
        horse.id());
    if (updated == 0) {
      LOG.warn("Failed to update horse with ID {} because it does not exist", horse.id());
      throw new NotFoundException("Could not update horse with ID " + horse.id() + ", because it does not exist");
    }

    return new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setSex(horse.sex())
        .setDateOfBirth(horse.dateOfBirth())
        .setHeight(horse.height())
        .setWeight(horse.weight())
        .setBreedId(breed);

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
