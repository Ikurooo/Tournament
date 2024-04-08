package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Validator class for validating horse details.
 */
@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(HorseValidator.class);
  private final BreedService breedService;
  private final LocalDate minDate;
  private final LinkerService linkerService;

  /**
   * Constructs a new instance of HorseValidator with the specified BreedService and LinkerService dependencies.
   *
   * @param breedService  The breed service to use for breed validation
   * @param linkerService The linker service to use for additional operations
   */
  public HorseValidator(BreedService breedService, LinkerService linkerService) {
    this.breedService = breedService;
    this.linkerService = linkerService;
    this.minDate = GlobalConstants.minDate;
  }

  /**
   * Validates the provided horse details for deletion.
   *
   * @param horse the horse details to validate
   * @throws ValidationException if the validation fails
   */
  public void validateForDelete(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForDelete({})", horse);
    ValidationContext context = new ValidationContext();
    validateLinkedToTournament(horse, context);
    context.throwIfErrorsPresent("Validation of horse for delete failed");
  }

  /**
   * Validates the provided horse details for update.
   *
   * @param horse the horse details to validate
   * @throws ValidationException if the validation fails
   */
  public void validateForUpdate(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForUpdate({})", horse);
    ValidationContext context = new ValidationContext();

    validateName(horse, context);
    validateDateOfBirth(horse, context);
    validateHeight(horse, context);
    validateWeight(horse, context);
    validateBreed(horse, context);
    validateSex(horse, context);

    context.throwIfErrorsPresent("Validation of horse for update failed");
  }

  /**
   * Validates the provided horse details for creation.
   *
   * @param horse the horse details to validate
   * @throws ValidationException if the validation fails
   */
  public void validateForCreate(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForCreate({})", horse);
    ValidationContext context = new ValidationContext();

    validateName(horse, context);
    validateDateOfBirth(horse, context);
    validateHeight(horse, context);
    validateWeight(horse, context);
    validateBreed(horse, context);
    validateSex(horse, context);

    context.throwIfErrorsPresent("Validation of horse for create failed");
  }

  /**
   * Validates the name of the horse.
   *
   * @param horse    The horse detail DTO containing the name to validate
   * @param context  The validation context to accumulate errors
   */
  private void validateName(HorseDetailDto horse, ValidationContext context) {
    if (horse.name() == null || horse.name().isEmpty()) {
      context.addError("Horse name cannot be empty or null.");
    } else if (!horse.name().matches("^[a-zA-Z0-9 ]*$")) {
      context.addError("Horse name must contain only alphanumeric characters.");
    }
  }

  /**
   * Validates the date of birth of the horse.
   *
   * @param horse    The horse detail DTO containing the date of birth to validate
   * @param context  The validation context to accumulate errors
   */
  private void validateDateOfBirth(HorseDetailDto horse, ValidationContext context) {
    if (horse.dateOfBirth() == null) {
      context.addError("Date of birth cannot be null.");
    } else if (horse.dateOfBirth().isBefore(minDate)) {
      context.addError(String.format("Date of birth cannot be before %s.", minDate));
    }
  }

  /**
   * Validates the height of the horse.
   *
   * @param horse    The horse detail DTO containing the height to validate
   * @param context  The validation context to accumulate errors
   */
  private void validateHeight(HorseDetailDto horse, ValidationContext context) {
    if (horse.height() <= 0) {
      context.addError("Height must be greater than zero.");
    }
  }

  /**
   * Validates the weight of the horse.
   *
   * @param horse    The horse detail DTO containing the weight to validate
   * @param context  The validation context to accumulate errors
   */
  private void validateWeight(HorseDetailDto horse, ValidationContext context) {
    if (horse.weight() <= 0) {
      context.addError("Weight must be greater than zero.");
    }
  }

  /**
   * Validates the breed of the horse.
   *
   * @param horse    The horse detail DTO containing the breed to validate
   * @param context  The validation context to accumulate errors
   */
  private void validateBreed(HorseDetailDto horse, ValidationContext context) {
    var breeds = breedService.allBreeds();
    if (horse.breed() != null && breeds.noneMatch(b -> b.equals(horse.breed()))) {
      context.addError("Invalid breed specified.");
    }
  }

  /**
   * Validates the sex of the horse.
   *
   * @param horse    The horse detail DTO containing the sex to validate
   * @param context  The validation context to accumulate errors
   */
  private void validateSex(HorseDetailDto horse, ValidationContext context) {
    if (horse.sex() == null || !(horse.sex().equals(Sex.FEMALE) || horse.sex().equals(Sex.MALE))) {
      context.addError("Invalid horse sex specified.");
    }
  }

  /**
   * Validates if the horse is linked to any tournaments.
   *
   * @param horse    The horse detail DTO to check for tournament links
   * @param context  The validation context to accumulate errors
   */
  private void validateLinkedToTournament(HorseDetailDto horse, ValidationContext context) {
    try {
      List<Tournament> tournaments = linkerService.getTournamentsAssociatedWithHorseId(horse.id());
      if (!tournaments.isEmpty()) {
        context.addError("Horse is linked to (a) tournament(s): {}." + tournaments);
      }
    } catch (FailedToRetrieveException e) {
      context.addError("Failed to retrieve tournaments associated with the horse.");
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
        LOG.warn("Error during horse validation: {}", errors);
        throw new ValidationException(exceptionMessage, errors);
      }
    }
  }
}
