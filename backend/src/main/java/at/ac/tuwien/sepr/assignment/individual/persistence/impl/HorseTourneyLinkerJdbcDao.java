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
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;

public class HorseTourneyLinkerJdbcDao implements HorseTourneyLinkerDao {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String HORSE_TABLE_NAME = "horse";
  private static final String TOURNAMENT_TABLE_NAME = "tournament";
  private static final String LINKER_TABLE_NAME = "horse_tourney_linker";
  private JdbcTemplate jdbcTemplate;
  private NamedParameterJdbcTemplate jdbcNamed;

  public void horseTourneyLinkerDao(
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
      var rowsAffectedTournament = jdbcTemplate.update("INSERT INTO " + TOURNAMENT_TABLE_NAME
              + " (name, start_date, end_date)"
              + " VALUES (?, ?, ?)",
          tournament.name(),
          tournament.startDate(),
          tournament.endDate());

      if (rowsAffectedTournament < 1) {
        LOG.warn("Failed to create tournament {}", tournament);
      }

      for (Horse horse : tournament.horses()) {
        var rowsAffectedLinker = jdbcTemplate.update("INSERT INTO " + LINKER_TABLE_NAME
                + " (id, id)"
                + " VALUES ( ?, ?)",
            horse);

        if (rowsAffectedLinker < 1) {
          LOG.warn("Failed to link horse with tournament: {}", horse);
        }
      }

    } catch (Exception e) {
      LOG.error("Failed to insert a new horse. Rows affected: {}", e.toString());
      throw new FailedToCreateException("Failed to insert a new horse.");
    }

    return null;
  }
}