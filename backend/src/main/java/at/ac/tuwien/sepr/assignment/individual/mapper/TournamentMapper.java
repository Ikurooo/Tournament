package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Mapper class responsible for converting Tournament entities to DTOs.
 */
@Component
public class TournamentMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Convert a tournament entity object to a {@link TournamentListDto}.
   *
   * @param tournament the tournament to convert
   * @return the converted {@link TournamentListDto}
   */
  public TournamentListDto entityToListDto(Tournament tournament) {
    LOG.trace("entityToListDto({})", tournament);
    if (tournament == null) {
      return null;
    }

    return new TournamentListDto(
        tournament.getId(),
        tournament.getName(),
        tournament.getStartDate(),
        tournament.getEndDate()
    );
  }

  /**
   * Converts tournament standings data into a collection of participant details.
   *
   * @param standings The tournament standings data.
   * @return A collection of participant details.
   */
  public Collection<TournamentDetailParticipantDto> standingsToCollection(TournamentStandingsDto standings) {
    LOG.trace("standingsToCollection({})", standings);
    Map<Long, TournamentDetailParticipantDto> participants = new HashMap<>();
    var tree = standings.tree();
    processTree(tree, participants);
    return participants.values();
  }

  /**
   * Recursively processes the tournament standings tree to populate participant details.
   * Child function of standingsToCollection()
   *
   * @param branch The current branch in the standings tree.
   * @param participants The map to store participant details.
   */
  private void processTree(TournamentStandingsTreeDto branch, Map<Long, TournamentDetailParticipantDto> participants) {
    LOG.debug("processTree({})", participants);
    if (branch == null) {
      return;
    }

    if (branch.getThisParticipant() != null && branch.getThisParticipant().getHorseId() != null) {
      long participantId = branch.getThisParticipant().getHorseId();
      participants.computeIfAbsent(participantId, id -> branch.getThisParticipant());
    }

    if (branch.getBranches() == null) {
      return;
    }

    for (TournamentStandingsTreeDto child : branch.getBranches()) {
      processTree(child, participants);
    }
  }

}
