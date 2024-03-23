package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseTourneyLinkerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validator class for validating tournament standings.
 */
@Component
public class StandingsValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseTourneyLinkerDao horseTourneyLinkerDao;
  private Set<Long> horsesBelongingToTournament;
  private int entryNumber;

  /**
   * Constructor for StandingsValidator.
   *
   * @param horseTourneyLinkerDao DAO for accessing tournament data.
   */
  public StandingsValidator(HorseTourneyLinkerDao horseTourneyLinkerDao) {
    this.horseTourneyLinkerDao = horseTourneyLinkerDao;
    this.entryNumber = 0;
  }

  /**
   * Validates the tournament standings.
   *
   * @param standings The tournament standings to validate.
   * @throws ValidationException If the standings are invalid.
   */
  public void validateStandings(TournamentStandingsDto standings) throws ValidationException {
    LOG.trace("ValidateStandings({})", standings);
    ValidationContext context = new ValidationContext();

    this.horsesBelongingToTournament = horseTourneyLinkerDao.findParticipantsByTournamentId(standings.id())
        .stream()
        .map(TournamentDetailParticipantDto::getHorseId)
        .collect(Collectors.toSet());

    validateTree(standings.tree(), context);

    context.throwIfErrorsPresent("Validation of standings failed");
  }

  private void validateTree(TournamentStandingsTreeDto tree, ValidationContext context) {
    LOG.debug("validateTree()");
    if (tree == null) {
      context.addError("Provided tree cannot be null.");
      return;
    }

    Map<Long, Long> idToRoundReachedMap = new HashMap<>();
    this.entryNumber = 0;
    int maxDepth = 4; // Change this in case a tournament can have more than 8 participants.
    validateTreeRecursively(1, tree, maxDepth, context, idToRoundReachedMap);
  }

  // TODO: add map to see if the entry numbers / rounds reached are correct or differ in any place; Map<Long, Long>
  // TODO: check on the way down instead of on the way up ?
  // TODO: sum of hours

  private void validateTreeRecursively(int depth, TournamentStandingsTreeDto branch, int maxDepth, ValidationContext context, Map<Long, Long> numbers) {
    LOG.debug("validateTreeRecursively({})", depth);
    if (depth >= maxDepth) {
      this.entryNumber += 1;

      if (branch.getBranches() != null) {
        context.addError("Tree is too large.");
      }

      if (branch.getThisParticipant() == null || branch.getThisParticipant().getEntryNumber() == null) {
        // TODO uwu
        return;
      }

      if (branch.getThisParticipant().getEntryNumber() != entryNumber) {
        context.addError("Mismatched branch and entry number.");
      }

      if (!this.horsesBelongingToTournament.contains(branch.getThisParticipant().getHorseId())) {
        context.addError("Horse with id " + branch.getThisParticipant().getHorseId() + " is not registered for this tournament.");
      }

      return;
    }

    if (branch.getBranches() == null) {
      context.addError("Tree is too small.");
      return;
    }

    if (branch.getBranches().length != 2) {
      context.addError("There must be exactly 2 branches.");
      return;
    }

    validateTreeRecursively(depth + 1, branch.getBranches()[0], maxDepth, context, numbers);
    validateTreeRecursively(depth + 1, branch.getBranches()[1], maxDepth, context, numbers);

    Set<Long> previous = new HashSet<>();
    Arrays.stream(branch.getBranches())
        .filter(branches -> branches.getThisParticipant() != null
            && branches.getThisParticipant().getHorseId() != null)
        .forEach(branches -> previous.add(branches.getThisParticipant().getHorseId()));


    if (branch.getThisParticipant() != null
        && branch.getThisParticipant().getHorseId() != null
        && !previous.contains(branch.getThisParticipant().getHorseId())) {
      context.addError("Horse: " + branch.getThisParticipant().getName()
          + " at depth " + depth + " does not match any of the ones before it.");
    }

    if (branch.getThisParticipant() != null && previous.size() != 2) {
      context.addError("Not enough horses registered in the previous round at depth: " + depth);
    }
  }

  public static class ValidationContext {
    private final List<String> errors = new ArrayList<>();

    public void addError(String errorMessage) {
      errors.add(errorMessage);
    }

    public void throwIfErrorsPresent(String exceptionMessage) throws ValidationException {
      if (!errors.isEmpty()) {
        LOG.warn("Error during standings validation: {}", errors);
        throw new ValidationException(exceptionMessage, errors);
      }
    }
  }
}
