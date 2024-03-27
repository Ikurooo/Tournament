package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseTourneyLinkerDao;
import at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.Statement;
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
  public Tournament create(TournamentDetailDto tournament) {
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
        LOG.error("Failed to insert a new tournament. Rows affected: {}", rowsAffectedTournament);
        throw new FailedToCreateException("Failed to insert a new tournament.");
      }

      long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();

      Tournament createdTournament = new Tournament()
          .setId(generatedId)
          .setName(tournament.name())
          .setStartDate(tournament.startDate())
          .setEndDate(tournament.endDate());

      for (Horse horse : tournament.participants()) {
        var rowsAffectedLinker = jdbcTemplate.update("INSERT INTO " + LINKER_TABLE_NAME
                + " (horse_id, tournament_id) VALUES (?, ?)",
            createdTournament.getId(), horse.getId());

        if (rowsAffectedLinker < 1) {
          LOG.warn("Failed to link horse with tournament: {}", horse);
        }
      }
      return createdTournament;

    } catch (Exception e) {
      LOG.error("Failed to insert a new tournament: {}", e.toString());
      throw new FailedToCreateException("Failed to insert a new tournament.");
    }
  }
}
