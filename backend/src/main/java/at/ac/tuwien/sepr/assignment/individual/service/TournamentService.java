package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.util.stream.Stream;

/**
 * Service interface for managing tournaments.
 */
public interface TournamentService {

  /**
   * Search for tournaments in the persistent data store matching all provided fields.
   * The name is considered a match if the search string is a substring of the field
   * in the tournament.
   *
   * @param searchParameters the search parameters to use in filtering.
   * @return the tournaments where the given fields match.
   */
  Stream<TournamentListDto> search(TournamentSearchDto searchParameters);

  /**
   * Creates a new tournament with the data provided in {@code tournament}
   * and stores it in the persistent data store.
   *
   * @param tournament the tournament to create
   * @return the created tournament
   * @throws ValidationException if the data provided for the new tournament is incorrect
   *                             (e.g., missing name, name too long, etc.)
   * @throws ConflictException   if there is a conflict with the existing data in the system
   *                             (e.g., breed does not exist)
   * @throws NotFoundException   if any of the horses in the participants are not in the horses
   *                             table
   */
  Tournament create(TournamentDetailDto tournament)
      throws ValidationException, ConflictException, NotFoundException;


  /**
   * Get the tournament with the given ID, with more detailed information.
   * This includes additional details about the tournament.
   *
   * @param id the ID of the tournament to get
   * @return the tournament with ID {@code id}
   * @throws NotFoundException if the tournament with the given
   *                           ID does not exist in the persistent data store
   */
  TournamentStandingsDto getById(long id) throws NotFoundException;
}
