package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.print.attribute.HashDocAttributeSet;

/**
 * Validator class for validating tournament details.
 */
@Component
public class TournamentValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final LocalDate minDate = GlobalConstants.minDate;
  private final HorseService horseService;

  public TournamentValidator(HorseService horseService) throws ValidationException {
    this.horseService = horseService;
  }

  /**
   * Validates tournament details for creation.
   *
   * @param tournament The tournament details to validate
   * @throws ValidationException if validation fails
   */
  public void validateForCreate(TournamentDetailDto tournament) throws ValidationException, NotFoundException {
    LOG.trace("validateForCreate({})", tournament);
    Set<Long> seenIds = new HashSet<>();
    List<String> validationErrors = Stream.of(
            validate(tournament.name(), "Tournament name cannot be empty or null.", s -> s == null || s.isEmpty()),
            validate(tournament.name(), "Tournament name must contain only alphanumeric characters.", s -> !s.matches("^[a-zA-Z0-9]*$")),
            validateDates(tournament.startDate(), tournament.endDate()),
            hasEightParticipants(tournament),
            validateHorseIds(tournament),
            validateHorseExistence(tournament),
            validateDuplicates(tournament, seenIds)
        )
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of tournament for create failed", validationErrors);
    }
  }

  private Optional<String> validate(String value, String message, Predicate<String> condition) {
    return condition.test(value) ? Optional.of(message) : Optional.empty();
  }

  private Optional<String> validateDates(LocalDate startDate, LocalDate endDate) {
    List<String> errors = new ArrayList<>();
    if (startDate == null || endDate == null) {
      errors.add("Start date and end date cannot be null.");
    }
    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
      errors.add("Start date cannot be after end date.");
    }
    if (startDate != null && endDate != null && (startDate.isBefore(minDate) || endDate.isBefore(minDate))) {
      errors.add(String.format("Start date and end date must be after %s.", minDate));
    }
    return errors.isEmpty() ? Optional.empty() : Optional.of(String.join(" ", errors));
  }

  private Optional<String> validateHorseIds(TournamentDetailDto tournament) {
    boolean hasInvalidIds = Arrays.stream(tournament.participants())
        .anyMatch(horse -> horse.getId() == null);
    return hasInvalidIds ? Optional.of("Invalid horse ID found.") : Optional.empty();
  }

  private Optional<String> validateDuplicates(TournamentDetailDto tournament, Set<Long> seenIds) {
    return Arrays.stream(tournament.participants())
        .map(Horse::getId)
        .filter(Objects::nonNull) // Filter out null IDs
        .filter(id -> !seenIds.add(id))
        .findAny()
        .map(duplicateId -> "Duplicate participant found: Horse ID " + duplicateId);
  }

  private Optional<String> validateHorseExistence(TournamentDetailDto tournament) {
    return Arrays.stream(tournament.participants())
        .filter(horse -> !doesHorseExist(horse))
        .map(Horse::getId)
        .findAny()
        .map(horse -> "Horse does not exist: Horse ID " + horse);
  }

  private Optional<String> hasEightParticipants(TournamentDetailDto tournament) {
    return tournament.participants().length == 8 ? Optional.empty() : Optional.of("Tournament must have exactly 8 participants.");
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
