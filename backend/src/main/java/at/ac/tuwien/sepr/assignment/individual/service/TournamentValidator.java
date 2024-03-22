package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
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

  public void validateForCreate(TournamentDetailDto tournament) throws ValidationException {
    LOG.trace("validateForCreate({})", tournament);
    List<String> validationErrors = new ArrayList<>();

    if (tournament.name() == null || tournament.name().isEmpty()) {
      validationErrors.add("Tournament name cannot be empty or null.");
    }

    if (tournament.startDate() == null || tournament.endDate() == null) {
      validationErrors.add("Start date and end date cannot be null.");
      throw new ValidationException("Validation of tournament for create failed", validationErrors);
    }

    LocalDate minDate = LocalDate.of(1900, 1, 1);

    if (tournament.startDate().isBefore(minDate) || tournament.endDate().isBefore(minDate)) {
      validationErrors.add("Start date and end date must be after 1900-01-01.");
    }

    if (tournament.startDate().isAfter(tournament.endDate())) {
      validationErrors.add("Start date cannot be after end date.");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of tournament for create failed", validationErrors);
    }
  }
}
