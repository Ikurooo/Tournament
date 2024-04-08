package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToUpdateException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Data Access Object interface for managing the linkage between horses and tournaments.
 * Provides methods to create tournaments and retrieve participants for tournaments.
 */
public interface HorseTourneyLinkerDao {

  /**
   * Updates the standings of a tournament.
   *
   * @param horses the participants of the given tournament to be updated
   * @param tournamentId the id of the tournament you wish to update the standings of
   * @return on success the new standings of the tournament.
   * @throws FailedToUpdateException if there is a failure in updating the tournament in the persistent data store
   */
  Collection<TournamentDetailParticipantDto> updateStandings(Collection<TournamentDetailParticipantDto> horses,
                                                             long tournamentId) throws FailedToUpdateException;

  /**
   * Retrieves all the rounds reached by a given horse for the past year.
   *
   * @param horse the tournament detail participant DTO representing the horse for which past year details are to be retrieved
   * @return a list of tournament detail participant DTOs containing the rounds reached by the horse for the past year
   * @throws FailedToRetrieveException if there is a failure in retrieving the tournament from the persistent data store
   */
  Collection<TournamentDetailParticipantDto> getHorseDetailsForPastYear(TournamentDetailParticipantDto horse,
                                                                        LocalDate dateOfCurrentTournament)
                                                                        throws FailedToRetrieveException;

  /**
   * Creates a new tournament with the provided data and stores it in the persistent data store.
   *
   * @param tournament the DTO containing the data for the tournament to be created
   * @return the created tournament
   * @throws FailedToCreateException if there is a failure in creating the tournament in the persistent data store
   */
  Tournament create(TournamentCreateDto tournament) throws FailedToCreateException;

  /**
   * Retrieves the list of participants (horses) for the tournament with the specified ID.
   *
   * @param id the ID of the tournament
   * @return the list of participants (horses) for the tournament
   * @throws FailedToRetrieveException if there is a failure in retrieving the participants from the persistent data store
   */
  Collection<TournamentDetailParticipantDto> findParticipantsByTournamentId(long id) throws FailedToRetrieveException;

  /**
   * Retrieves the list of tournaments associated with the horse with the specified ID.
   *
   * @param id the ID of the horse
   * @return the list of tournaments associated with the horse
   * @throws FailedToRetrieveException if there is a failure in retrieving the tournaments from the persistent data store
   */
  List<Tournament> getTournamentsAssociatedWithHorseId(long id) throws FailedToRetrieveException;
}
