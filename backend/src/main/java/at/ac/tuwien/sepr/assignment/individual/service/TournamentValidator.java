package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.global.GlobalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class TournamentValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final LocalDate minDate = GlobalConstants.minDate;

  public void validateForCreate(TournamentDetailDto tournament) throws ValidationException {
    LOG.trace("validateForCreate({})", tournament);
    List<String> validationErrors = new ArrayList<>();

    if (tournament.name() == null || tournament.name().isEmpty()) {
      validationErrors.add("Tournament name cannot be empty or null.");
      throw new ValidationException("Validation of tournament for create failed", validationErrors);
    }

    if (!tournament.name().matches("^[a-zA-Z0-9]*$")) {
      validationErrors.add("Horse name must contain only alphanumeric characters.");
    }

    if (tournament.startDate() == null || tournament.endDate() == null) {
      validationErrors.add("Start date and end date cannot be null.");
      throw new ValidationException("Validation of tournament for create failed", validationErrors);
    }

    if (tournament.startDate().isAfter(tournament.endDate())) {
      validationErrors.add("Start date cannot be after end date.");
    }

    if (tournament.startDate().isBefore(minDate) || tournament.endDate().isBefore(minDate)) {
      validationErrors.add(String.format("Start date and end date must be after %s.", minDate));
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of tournament for create failed", validationErrors);
    }
  }
}