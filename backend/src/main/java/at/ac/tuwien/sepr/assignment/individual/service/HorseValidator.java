package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants;
import at.ac.tuwien.sepr.assignment.individual.service.BreedService;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
   * Constructs a new instance of HorseValidator with the specified BreedService dependency.
   *
   * @param breedService the breed service to use for breed validation
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

  private void validateName(HorseDetailDto horse, ValidationContext context) {
    if (horse.name() == null || horse.name().isEmpty()) {
      context.addError("Horse name cannot be empty or null.");
    } else if (!horse.name().matches("^[a-zA-Z0-9 ]*$")) {
      context.addError("Horse name must contain only alphanumeric characters.");
    }
  }

  private void validateDateOfBirth(HorseDetailDto horse, ValidationContext context) {
    if (horse.dateOfBirth() == null) {
      context.addError("Date of birth cannot be null.");
    } else if (horse.dateOfBirth().isBefore(minDate)) {
      context.addError(String.format("Date of birth cannot be before %s.", minDate));
    }
  }

  private void validateHeight(HorseDetailDto horse, ValidationContext context) {
    if (horse.height() <= 0) {
      context.addError("Height must be greater than zero.");
    }
  }

  private void validateWeight(HorseDetailDto horse, ValidationContext context) {
    if (horse.weight() <= 0) {
      context.addError("Weight must be greater than zero.");
    }
  }

  private void validateBreed(HorseDetailDto horse, ValidationContext context) {
    var breeds = breedService.allBreeds();
    if (horse.breed() != null && breeds.noneMatch(b -> b.equals(horse.breed()))) {
      context.addError("Invalid breed specified.");
    }
  }

  private void validateSex(HorseDetailDto horse, ValidationContext context) {
    if (horse.sex() == null || !(horse.sex().equals(Sex.FEMALE) || horse.sex().equals(Sex.MALE))) {
      context.addError("Invalid horse sex specified.");
    }
  }

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

  private static class ValidationContext {
    private final List<String> errors = new ArrayList<>();

    public void addError(String errorMessage) {
      errors.add(errorMessage);
    }

    public void throwIfErrorsPresent(String exceptionMessage) throws ValidationException {
      if (!errors.isEmpty()) {
        LOG.warn("Error during horse validation: {}", errors);
        throw new ValidationException(exceptionMessage, errors);
      }
    }
  }
}
