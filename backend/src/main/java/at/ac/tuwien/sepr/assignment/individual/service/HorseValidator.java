package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants;
import at.ac.tuwien.sepr.assignment.individual.mapper.BreedMapper;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final Stream<BreedDto> breeds;
  private final LocalDate minDate = GlobalConstants.minDate;

  public HorseValidator(BreedService breedService) {
    this.breeds = breedService.allBreeds();
  }

  public void validateForUpdate(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (breeds.noneMatch(b -> b.equals(horse.breed()))) {
      validationErrors.add("Invalid breed specified.");
    }

    if (horse.id() == null) {
      validationErrors.add("No ID given");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  public void validateForCreate(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForCreate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.name() == null || horse.name().isEmpty()) {
      validationErrors.add("Horse name cannot be empty or null.");
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }

    if (!horse.name().matches("^[a-zA-Z0-9]*$")) {
      validationErrors.add("Horse name must contain only alphanumeric characters.");
    }

    if (horse.dateOfBirth() == null) {
      validationErrors.add("Date of birth cannot be null.");
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }

    if (horse.dateOfBirth().isBefore(minDate)) {
      validationErrors.add(String.format("Date of birth cannot be before %s.", minDate.toString()));
    }


    if (horse.height() <= 0) {
      validationErrors.add("Height must be greater than zero.");
    }

    if (horse.weight() <= 0) {
      validationErrors.add("Weight must be greater than zero.");
    }

    if (horse.breed() != null && breeds.noneMatch(b -> b.equals(horse.breed()))) {
      validationErrors.add("Invalid breed specified.");
    }

    if (horse.sex() == null) {
      validationErrors.add("Sex cannot be null.");
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }

    if (horse.sex() == Sex.MALE || horse.sex() == Sex.FEMALE) {
      validationErrors.add("Invalid horse sex specified.");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }
  }

}
