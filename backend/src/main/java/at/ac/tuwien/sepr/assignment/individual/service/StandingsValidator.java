package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

/**
 * Validator class for validating tournament standings.
 */
@Component
public class StandingsValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public StandingsValidator() {
  }

  public void validateStandings(TournamentStandingsTreeDto newTree, TournamentStandingsTreeDto existing) throws ValidationException {
    LOG.trace("ValidateStandings({}, {})", newTree, existing);
  }
}
