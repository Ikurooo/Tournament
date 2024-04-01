package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.List;

/**
 * Service interface for managing the actions related both to horses and tournaments.
 * Provides methods for searching, creating, updating, and deleting both horses and tournaments.
 */
public interface LinkerService {

  /**
   * Retrieves the tournament with the given ID, including more detailed information such as standings.
   *
   * @param id the ID of the tournament to retrieve
   * @return the tournament with the specified ID, including detailed standings
   * @throws NotFoundException        if the tournament with the given ID does not exist in the persistent data store
   * @throws FailedToRetrieveException if there is a failure in retrieving the tournament from the persistent data store
   */
  TournamentDetailDto getById(long id) throws NotFoundException, FailedToRetrieveException;

  /**
   * Retrieves the list of tournaments associated with the horse with the specified ID.
   *
   * @param id the ID of the horse
   * @return the list of tournaments associated with the horse
   * @throws FailedToRetrieveException if there is a failure in retrieving the tournaments from the persistent data store
   */
  List<Tournament> getTournamentsAssociatedWithHorseId(long id) throws FailedToRetrieveException;
}
