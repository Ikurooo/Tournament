package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.LinkerService;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentService;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for handling tournament-related requests.
 */
@RestController
@RequestMapping(path = TournamentEndpoint.BASE_PATH)
public class TournamentEndpoint {
  static final String BASE_PATH = "/tournaments";
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentService tournamentService;
  private final LinkerService linkerService;

  public TournamentEndpoint(TournamentService tournamentService, LinkerService linkerService) {
    this.tournamentService = tournamentService;
    this.linkerService = linkerService;
  }

  /**
   * Handles GET requests to retrieve detailed information of a specific tournament.
   *
   * @param id the ID of the tournament to retrieve details for
   * @return the detailed information of the tournament with the specified ID
   */
  @GetMapping("{id}")
  public TournamentStandingsDto getById(@PathVariable("id") long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    LOG.info("Tournament ID: {}", id);
    try {
      return linkerService.getById(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Tournament not found", e);
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
   * Handles GET requests to search tournaments based on the provided search criteria.
   *
   * @param searchParameters the search parameters for filtering tournaments
   * @return a stream of tournament list DTOs matching the search criteria
   */
  @GetMapping
  public Stream<TournamentListDto> searchTournaments(TournamentSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    try {
      return tournamentService.search(searchParameters);
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
   * Handles POST requests to create a new tournament.
   *
   * @param toCreate the details of the tournament to be created
   * @return the created tournament details
   */
  @PostMapping
  public Tournament create(@RequestBody TournamentDetailDto toCreate) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("Body of request:\n{}", toCreate);

    try {
      return tournamentService.create(toCreate);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      logClientError(status, "Validation issue during creation.", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (FailedToCreateException e) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      logClientError(status, "Failed to insert tournament.", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (Exception e) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      logClientError(status, "Internal server error. ", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}
