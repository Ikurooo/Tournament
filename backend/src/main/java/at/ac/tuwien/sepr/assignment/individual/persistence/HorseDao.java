package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToDeleteException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToUpdateException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.Collection;

/**
 * Data Access Object interface for managing horse entities.
 * Provides methods to interact with the persistent data store for horse-related operations.
 */
public interface HorseDao {

  /**
   * Retrieves a collection of horses that match the given search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match if the given parameter is a substring of the horse's name.
   *
   * @param searchParameters the parameters to use in the search
   * @return a collection of horses that match the search criteria
   * @throws FailedToRetrieveException if there is a failure in retrieving horses from the persistent data store
   */
  Collection<Horse> search(HorseSearchDto searchParameters) throws FailedToRetrieveException;

  /**
   * Updates the horse with the ID provided in the {@code horseDetailDto} parameter
   * with the data specified in the {@code horseDetailDto} parameter.
   *
   * @param horseDetailDto the DTO containing the updated horse data
   * @return the updated horse
   * @throws NotFoundException       if the horse with the given ID does not exist in the persistent data store
   * @throws FailedToUpdateException if there is a failure in updating the horse in the persistent data store
   */
  Horse update(HorseDetailDto horseDetailDto) throws NotFoundException, FailedToUpdateException;

  /**
   * Retrieves a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to retrieve
   * @return the retrieved horse
   * @throws NotFoundException         if the horse with the given ID does not exist in the persistent data store
   * @throws FailedToRetrieveException if there is a failure in retrieving the horse from the persistent data store
   */
  Horse getById(long id) throws NotFoundException, FailedToRetrieveException;

  /**
   * Creates a new horse with the provided data and stores it in the persistent data store.
   *
   * @param horse the DTO containing the data for the horse to be created
   * @return the created horse
   * @throws FailedToCreateException if there is a failure in creating the horse in the persistent data store
   */
  Horse create(HorseDetailDto horse) throws FailedToCreateException;

  /**
   * Deletes the horse with the specified ID from the persistent data store.
   *
   * @param id the ID of the horse to delete
   * @throws NotFoundException       if the horse with the given ID does not exist in the persistent data store
   * @throws FailedToDeleteException if there is a failure in deleting the horse from the persistent data store
   */
  void delete(long id) throws NotFoundException, FailedToDeleteException;
}
