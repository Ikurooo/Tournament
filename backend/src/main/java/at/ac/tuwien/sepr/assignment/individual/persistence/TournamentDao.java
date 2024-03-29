package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.Collection;

/**
 * Data Access Object interface for managing tournaments.
 * Provides methods to search for tournaments and retrieve tournaments by ID.
 */
public interface TournamentDao {

  /**
   * Retrieves the tournaments that match the given search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match if the given parameter is a substring of the tournament's name.
   *
   * @param searchParameters the parameters to use in searching
   * @return the tournaments where all given parameters match
   * @throws FailedToRetrieveException if there is a failure in retrieving tournaments from the persistent data store
   */
  Collection<Tournament> search(TournamentSearchDto searchParameters) throws FailedToRetrieveException;

  /**
   * Retrieves a tournament by its ID from the persistent data store.
   *
   * @param id the ID of the tournament to retrieve
   * @return the tournament with the specified ID
   * @throws NotFoundException        if the tournament with the given ID does not exist in the persistent data store
   * @throws FailedToRetrieveException if there is a failure in retrieving the tournament from the persistent data store
   */
  Tournament getById(long id) throws NotFoundException, FailedToRetrieveException;
}
