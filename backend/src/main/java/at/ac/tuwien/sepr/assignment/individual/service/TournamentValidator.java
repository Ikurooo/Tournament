package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
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

  // TODO: if there's time left
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
  public void validateForCreate(TournamentCreateDto tournament) throws ValidationException {
    LOG.trace("validateForCreate({})", tournament);
    ValidationContext context = new ValidationContext();

    validateName(tournament.name(), context);
    validateDates(tournament.startDate(), tournament.endDate(), context);
    validateParticipants(tournament, context);

    context.throwIfErrorsPresent("Validation of tournament for create failed");
  }

  /**
   * Validates the name of a tournament.
   *
   * @param name     The name of the tournament to validate
   * @param context  The validation context to accumulate errors
   */
  private void validateName(String name, ValidationContext context) {
    if (name == null || name.isEmpty()) {
      context.addError("Tournament name cannot be empty or null.");
    } else if (!name.matches("^[a-zA-Z0-9 ]*$")) {
      context.addError("Tournament name must contain only alphanumeric characters.");
    }
  }

  /**
   * Validates the start and end dates of a tournament.
   *
   * @param startDate  The start date of the tournament
   * @param endDate    The end date of the tournament
   * @param context    The validation context to accumulate errors
   */
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

  /**
   * Validates the participants of a tournament.
   *
   * @param tournament  The tournament create DTO containing participants to validate
   * @param context     The validation context to accumulate errors
   */
  private void validateParticipants(TournamentCreateDto tournament, ValidationContext context) {
    Set<Long> seenIds = new HashSet<>();

    if (tournament.participants().length != 8) {
      context.addError("Tournament must have exactly 8 participants.");
    }

    for (HorseSelectionDto horse : tournament.participants()) {
      if (horse.id() == null) {
        context.addError("Invalid horse ID found.");
      } else if (!seenIds.add(horse.id())) {
        context.addError("Duplicate participant found: Horse ID " + horse.id());
      } else if (!doesHorseExist(horse)) {
        context.addError("Horse does not exist: Horse ID " + horse.id());
      }
    }
  }

  /**
   * Checks if a horse with the given ID exists.
   *
   * @param horse  The horse selection DTO to check for existence
   * @return True if the horse exists, false otherwise
   */
  private boolean doesHorseExist(HorseSelectionDto horse) {
    try {
      horseService.getById(horse.id());
      return true;
    } catch (NotFoundException e) {
      return false;
    }
  }

  /**
   * Inner class representing the validation context used for accumulating errors during validation.
   */
  private static class ValidationContext {
    private final List<String> errors = new ArrayList<>();

    /**
     * Adds an error message to the list of errors.
     *
     * @param errorMessage The error message to add
     */
    public void addError(String errorMessage) {
      errors.add(errorMessage);
    }

    /**
     * Throws a ValidationException if errors are present in the context.
     *
     * @param exceptionMessage The message to include in the exception if errors are present
     * @throws ValidationException If errors are present in the context
     */
    public void throwIfErrorsPresent(String exceptionMessage) throws ValidationException {
      if (!errors.isEmpty()) {
        LOG.warn("Error during tournament validation: {}", errors);
        throw new ValidationException(exceptionMessage, errors);
      }
    }
  }
}