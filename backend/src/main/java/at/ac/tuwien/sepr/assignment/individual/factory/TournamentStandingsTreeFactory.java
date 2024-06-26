package at.ac.tuwien.sepr.assignment.individual.factory;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class to construct a tournament standings tree based on participant data.
 */
public class TournamentStandingsTreeFactory {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentDetailParticipantDto[] participants;
  private final TournamentStandingsTreeDto tree;
  private Map<Long, TournamentDetailParticipantDto> entryMap;
  private Long participantCounter;
  private final Long participantCount;

  /**
   * Constructor for TournamentStandingsTreeFactory.
   *
   * @param participants Array of tournament participants
   */
  public TournamentStandingsTreeFactory(TournamentDetailParticipantDto[] participants) {
    this.participants = participants;
    this.tree = new TournamentStandingsTreeDto();
    this.participantCounter = 0L;
    this.participantCount = (long) participants.length;
  }

  /**
   * Builds the tournament standings tree.
   *
   * @return The constructed tournament standings tree
   */
  public TournamentStandingsTreeDto buildTree() {
    LOG.trace("buildTree()");

    this.entryMap = new HashMap<>();
    for (TournamentDetailParticipantDto participant : this.participants) {
      if (participant.getEntryNumber() != 0) {
        this.entryMap.put(participant.getEntryNumber(), participant);
      }
    }

    int maxDepth = (int) Math.ceil(Math.log(this.participants.length) / Math.log(2)) + 1;
    this.buildEmptyTreeRecursively(1, this.tree, maxDepth);
    return this.tree;
  }

  /**
   * Recursively builds the empty tournament standings tree.
   * Child function of buildTree().
   *
   * @param depth     Current depth of the tree
   * @param branch    Current branch of the tree
   * @param maxDepth  Maximum depth of the tree
   */
  private void buildEmptyTreeRecursively(int depth, TournamentStandingsTreeDto branch, int maxDepth) {
    LOG.debug("buildEmptyTreeRecursively({})", depth);
    if (depth >= maxDepth) {
      TournamentDetailParticipantDto participant = this.entryMap.get(this.participantCounter % this.participantCount + 1);
      this.participantCounter += 1;
      if (participant != null) {
        branch.setThisParticipant(participant);
      }
      return;
    }
    branch.setBranches(new TournamentStandingsTreeDto[]{new TournamentStandingsTreeDto(), new TournamentStandingsTreeDto()});

    this.buildEmptyTreeRecursively(depth + 1, branch.getBranches()[0], maxDepth);
    this.buildEmptyTreeRecursively(depth + 1, branch.getBranches()[1], maxDepth);

    for (TournamentStandingsTreeDto subBranch : branch.getBranches()) {
      Long entryNumber = subBranch.getThisParticipant() != null ? subBranch.getThisParticipant().getEntryNumber() : null;
      Long roundReached = subBranch.getThisParticipant() != null ? subBranch.getThisParticipant().getRoundReached() : null;

      if (entryNumber == null || roundReached == null) {
        return;
      }

      TournamentDetailParticipantDto participant = this.entryMap.get(entryNumber);
      if (participant != null && roundReached <= depth) {
        branch.setThisParticipant(participant);
        break;
      }
    }
  }
}
