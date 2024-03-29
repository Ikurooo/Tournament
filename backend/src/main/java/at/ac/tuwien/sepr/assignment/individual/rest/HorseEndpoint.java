package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.DeletionResponseDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToDeleteException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToUpdateException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for handling horse-related requests.
 */
@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  static final String BASE_PATH = "/horses";
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseService service;

  public HorseEndpoint(HorseService service) {
    this.service = service;
  }

  /**
   * Handles GET requests to retrieve horses based on search criteria.
   *
   * @param searchParameters the search parameters for filtering horses
   * @return a stream of horse DTOs matching the search criteria
   */
  @GetMapping
  public Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    try {
      return service.search(searchParameters);
    } catch (FailedToRetrieveException e) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      logClientError(status, "Internal server error. ", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (Exception e) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      throw new ResponseStatusException(status, "Internal server error.");
    }
  }

  /**
   * Handles GET requests to retrieve details of a specific horse.
   *
   * @param id the ID of the horse to retrieve details for
   * @return the detailed information of the horse with the specified ID
   */
  @GetMapping("{id}")
  public HorseDetailDto getById(@PathVariable("id") long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    try {
      return service.getById(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to get details of not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (FailedToRetrieveException e) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      logClientError(status, "Internal server error. ", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (Exception e) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      throw new ResponseStatusException(status, "Internal server error.");
    }
  }

  /**
   * Handles PUT requests to update details of a specific horse.
   *
   * @param id       the ID of the horse to update
   * @param toUpdate the updated details of the horse
   * @return the updated information of the horse with the specified ID
   */
  @PutMapping("{id}")
  public HorseDetailDto update(@PathVariable("id") long id, @RequestBody HorseDetailDto toUpdate) {
    LOG.info("PUT " + BASE_PATH + "/{}", toUpdate);
    LOG.debug("Body of request:\n{}", toUpdate);
    try {
      return service.update(toUpdate.withId(id));
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to update not found.", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Conflict issue during creation.", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (FailedToUpdateException e) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      logClientError(status, "Failed to update horse.", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (Exception e) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      throw new ResponseStatusException(status, "Internal server error.");
    }
  }

  /**
   * Handles POST requests to create a new horse.
   *
   * @param toCreate the details of the horse to be created
   * @return the created horse details
   */
  @PostMapping
  public HorseDetailDto create(@RequestBody HorseDetailDto toCreate) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("Body of request:\n{}", toCreate);

    try {
      return service.create(toCreate);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      logClientError(status, "Validation issue during creation.", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Conflict issue during creation.", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (FailedToCreateException e) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      logClientError(status, "Failed to insert horse.", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (Exception e) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      throw new ResponseStatusException(status, "Internal server error.");
    }
  }

  /**
   * Handles DELETE requests to delete a horse with the specified ID.
   *
   * @param id the ID of the horse to delete
   * @return the deletion response
   */
  @DeleteMapping("/{id}")
  public DeletionResponseDto deleteHorse(@PathVariable("id") long id) {
    try {
      service.deleteHorseById(id);
      return new DeletionResponseDto("Horse deleted successfully.", true);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to delete not found.", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (FailedToDeleteException e) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      logClientError(status, "Error deleting horse.", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (Exception e) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      logClientError(status, "Error deleting horse", e);
      throw new ResponseStatusException(status, "Error deleting horse.");
    }
  }

  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}
