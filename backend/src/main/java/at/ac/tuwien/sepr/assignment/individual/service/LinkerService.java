package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseTournamentHistoryRequestDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToUpdateException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.List;
import java.util.stream.Stream;

/**
 * Service interface for managing the actions related both to horses and tournaments.
 * Provides methods for searching, creating, updating, and deleting both horses and tournaments.
 */
public interface LinkerService {

  /**
   * Updates the tournament standings with the provided data.
   *
   * @param id         The ID of the tournament to update standings for
   * @param standings  The updated tournament standings data
   * @return The updated tournament standings
   * @throws NotFoundException     If the tournament with the specified ID is not found
   * @throws ValidationException   If there is an issue with the validation of the standings data
   * @throws FailedToUpdateException If there is an issue with updating the standings
   */
  TournamentStandingsDto updateTournamentStandings(long id, TournamentStandingsDto standings) throws NotFoundException, ValidationException,
      FailedToUpdateException;

  /**
   * Retrieves horse details for the past year.
   * This method retrieves detailed information about horses, including their participation in tournaments,
   * for the past year based on the provided list of horse details.
   *
   * @param request a stream of horse details for which past year details need to be retrieved and the date
   * @return a stream of tournament detail participant DTOs containing horse details for the past year
   */
  Stream<TournamentDetailParticipantDto> getHorseDetailsForPastYear(HorseTournamentHistoryRequestDto request)
                                                                    throws FailedToRetrieveException;

  /**
   * Retrieves the tournament with the given ID, including more detailed information such as standings.
   *
   * @param id the ID of the tournament to retrieve
   * @return the tournament with the specified ID, including detailed standings
   * @throws NotFoundException        if the tournament with the given ID does not exist in the persistent data store
   * @throws FailedToRetrieveException if there is a failure in retrieving the tournament from the persistent data store
   */
  TournamentStandingsDto getById(long id) throws NotFoundException, FailedToRetrieveException;

  /**
   * Retrieves the list of tournaments associated with the horse with the specified ID.
   *
   * @param id the ID of the horse
   * @return the list of tournaments associated with the horse
   * @throws FailedToRetrieveException if there is a failure in retrieving the tournaments from the persistent data store
   */
  List<Tournament> getTournamentsAssociatedWithHorseId(long id) throws FailedToRetrieveException;
}
