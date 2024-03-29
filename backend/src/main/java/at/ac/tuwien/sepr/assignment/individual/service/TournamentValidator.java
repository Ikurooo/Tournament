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
    ValidationContext context = new ValidationContext();

    validateName(tournament.name(), context);
    validateDates(tournament.startDate(), tournament.endDate(), context);
    validateParticipants(tournament, context);

    context.throwIfErrorsPresent("Validation of tournament for create failed");
  }

  private void validateName(String name, ValidationContext context) {
    if (name == null || name.isEmpty()) {
      context.addError("Tournament name cannot be empty or null.");
    } else if (!name.matches("^[a-zA-Z0-9 ]*$")) {
      context.addError("Tournament name must contain only alphanumeric characters.");
    }
  }

  private void validateDates(LocalDate startDate, LocalDate endDate, ValidationContext context) {
    if (startDate == null || endDate == null) {
      context.addError("Start date and end date cannot be null.");
    } else {
      LocalDate minDate = GlobalConstants.minDate;
      if (startDate.isAfter(endDate)) {
        context.addError("Start date cannot be after end date.");
      }
      if (startDate.isBefore(minDate) || endDate.isBefore(minDate)) {
        context.addError(String.format("Start date and end date must be after %s.", minDate));
      }
    }
  }

  private void validateParticipants(TournamentDetailDto tournament, ValidationContext context) {
    Set<Long> seenIds = new HashSet<>();

    if (tournament.participants().length != 8) {
      context.addError("Tournament must have exactly 8 participants.");
    }

    for (Horse horse : tournament.participants()) {
      if (horse.getId() == null) {
        context.addError("Invalid horse ID found.");
      } else if (!seenIds.add(horse.getId())) {
        context.addError("Duplicate participant found: Horse ID " + horse.getId());
      } else if (!doesHorseExist(horse)) {
        context.addError("Horse does not exist: Horse ID " + horse.getId());
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

  private static class ValidationContext {
    private final List<String> errors = new ArrayList<>();

    public void addError(String errorMessage) {
      errors.add(errorMessage);
    }

    public void throwIfErrorsPresent(String exceptionMessage) throws ValidationException {
      if (!errors.isEmpty()) {
        LOG.warn("Error during tournament validation: {}", errors);
        throw new ValidationException(exceptionMessage, errors);
      }
    }
  }
}