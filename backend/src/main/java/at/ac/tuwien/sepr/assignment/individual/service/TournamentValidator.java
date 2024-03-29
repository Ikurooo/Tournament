package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Validator class for validating tournament details.
 */
@Component
public class TournamentValidator {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseService horseService;

  /**
   * Constructs a new TournamentValidator with the specified dependencies.
   *
   * @param horseService the HorseService instance to use for retrieving horse information
   */
  public TournamentValidator(HorseService horseService) {
    this.horseService = horseService;
  }

  /**
   * Validates the details of a tournament before creating it.
   *
   * @param tournament the tournament details to validate
   * @throws ValidationException if the validation fails
   */
  public void validateForCreate(TournamentDetailDto tournament) throws ValidationException {
    LOG.trace("validateForCreate({})", tournament);
    Set<Long> seenIds = new HashSet<>();
    List<String> validationErrors = new ArrayList<>();

    validateName(tournament.name(), validationErrors);
    validateDates(tournament.startDate(), tournament.endDate(), validationErrors);
    validateParticipants(tournament, seenIds, validationErrors);

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of tournament for create failed", validationErrors);
    }
  }

  private void validateName(String name, List<String> errors) {
    if (name == null || name.isEmpty()) {
      errors.add("Tournament name cannot be empty or null.");
    } else if (!name.matches("^[a-zA-Z0-9]*$")) {
      errors.add("Tournament name must contain only alphanumeric characters.");
    }
  }

  private void validateDates(LocalDate startDate, LocalDate endDate, List<String> errors) {
    if (startDate == null || endDate == null) {
      errors.add("Start date and end date cannot be null.");
    } else {
      LocalDate minDate = GlobalConstants.minDate;
      if (startDate.isAfter(endDate)) {
        errors.add("Start date cannot be after end date.");
      }
      if (startDate.isBefore(minDate) || endDate.isBefore(minDate)) {
        errors.add(String.format("Start date and end date must be after %s.", minDate));
      }
    }
  }

  private void validateParticipants(TournamentDetailDto tournament, Set<Long> seenIds, List<String> errors) {
    if (tournament.participants().length != 8) {
      errors.add("Tournament must have exactly 8 participants.");
    }

    for (Horse horse : tournament.participants()) {
      if (horse.getId() == null) {
        errors.add("Invalid horse ID found.");
      } else if (!seenIds.add(horse.getId())) {
        errors.add("Duplicate participant found: Horse ID " + horse.getId());
      } else if (!doesHorseExist(horse)) {
        errors.add("Horse does not exist: Horse ID " + horse.getId());
      }
    }
  }

  private boolean doesHorseExist(Horse horse) {
    try {
      horseService.getById(horse.getId());
      return true;
    } catch (NotFoundException e) {
      return false;
    }
  }
}
