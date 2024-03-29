package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToDeleteException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToUpdateException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.List;
import java.util.stream.Stream;

/**
 * Service interface for managing horses.
 * Provides methods for searching, creating, updating, and deleting horses.
 */
public interface HorseService {

  /**
   * Searches for horses in the persistent data store matching all provided fields.
   * The name is considered a match if the search string is a substring of the field in the horse.
   *
   * @param searchParameters the search parameters to use in filtering
   * @return a stream of horse list DTOs representing the horses where the given fields match
   * @throws FailedToRetrieveException if there is a failure in retrieving horses from the persistent data store
   */
  Stream<HorseListDto> search(HorseSearchDto searchParameters) throws FailedToRetrieveException;

  /**
   * Creates a new horse with the data provided in {@code horse} and stores it in the persistent data store.
   *
   * @param horse the horse to create
   * @return the created horse
   * @throws ValidationException      if the data provided for the new horse is incorrect (e.g., missing name, name too long, etc.)
   * @throws ConflictException        if there is a conflict with the existing data in the system (e.g., breed does not exist)
   * @throws FailedToCreateException if there is a failure in creating the horse in the persistent data store
   */
  HorseDetailDto create(HorseDetailDto horse) throws ValidationException, ConflictException, FailedToCreateException;

  /**
   * Updates the horse with the given ID with the data provided in {@code horse} in the persistent data store.
   *
   * @param horse the horse with updated data
   * @return the updated horse
   * @throws NotFoundException       if the horse with the given ID does not exist in the persistent data store
   * @throws ValidationException     if the updated data for the horse is incorrect (e.g., missing name, name too long, etc.)
   * @throws ConflictException       if there is a conflict with the updated data (e.g., breed does not exist)
   * @throws FailedToUpdateException if there is a failure in updating the horse in the persistent data store
   */
  HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException, FailedToUpdateException;

  /**
   * Retrieves the horse with the given ID, including more detailed information such as breed.
   *
   * @param id the ID of the horse to retrieve
   * @return the horse with the specified ID, including detailed information
   * @throws NotFoundException        if the horse with the given ID does not exist in the persistent data store
   * @throws FailedToRetrieveException if there is a failure in retrieving the horse from the persistent data store
   */
  HorseDetailDto getById(long id) throws NotFoundException, FailedToRetrieveException;

  /**
   * Deletes the horse with the given ID from the persistent data store.
   *
   * @param id the ID of the horse to delete
   * @throws NotFoundException       if the horse with the given ID does not exist in the persistent data store
   * @throws FailedToDeleteException if there is a failure in deleting the horse from the persistent data store
   */
  void deleteHorseById(long id) throws NotFoundException, FailedToDeleteException, ValidationException;
}
