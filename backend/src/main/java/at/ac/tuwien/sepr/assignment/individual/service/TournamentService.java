package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.stream.Stream;

/**
 * Service interface for managing tournaments.
 * Provides methods for searching tournaments, creating new tournaments, and retrieving detailed tournament information.
 */
public interface TournamentService {

  /**
   * Searches for tournaments in the persistent data store matching all provided fields.
   * The name is considered a match if the search string is a substring of the field in the tournament.
   *
   * @param searchParameters the search parameters to use in filtering
   * @return a stream of tournament list DTOs representing the tournaments where the given fields match
   * @throws FailedToRetrieveException if there is a failure in retrieving tournaments from the persistent data store
   */
  Stream<TournamentListDto> search(TournamentSearchDto searchParameters) throws FailedToRetrieveException;

  /**
   * Creates a new tournament with the data provided in {@code tournament} and stores it in the persistent data store.
   *
   * @param tournament the tournament to create
   * @return the created tournament
   * @throws ValidationException      if the data provided for the new tournament is incorrect (e.g., missing name, name too long, etc.)
   * @throws ConflictException        if there is a conflict with the existing data in the system (e.g., breed does not exist)
   * @throws NotFoundException       if any of the horses in the participants are not in the horses table
   * @throws FailedToCreateException if there is a failure in creating the tournament in the persistent data store
   */
  Tournament create(TournamentCreateDto tournament)
      throws ValidationException, ConflictException, NotFoundException, FailedToCreateException;
}
