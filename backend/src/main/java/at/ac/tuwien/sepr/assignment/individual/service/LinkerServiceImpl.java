package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseTournamentHistoryRequest;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.factory.TournamentStandingsTreeFactory;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseTourneyLinkerDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Stream;

/**
 * Implementation of the {@link LinkerService} interface.
 */
@Component
public class LinkerServiceImpl implements LinkerService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentDao tournamentDao;
  private final HorseTourneyLinkerDao horseTourneyLinkerDao;



  /**
   * @param tournamentDao         The TournamentDao instance
   * @param horseTourneyLinkerDao The HorseTourneyLinkerDao instance
   */
  public LinkerServiceImpl(TournamentDao tournamentDao, HorseTourneyLinkerDao horseTourneyLinkerDao) {
    this.tournamentDao = tournamentDao;
    this.horseTourneyLinkerDao = horseTourneyLinkerDao;
  }

  @Override
  public TournamentStandingsDto updateTournamentStandings(long id, TournamentStandingsDto standings) {
    return null;
  }

  @Override
  public Stream<TournamentDetailParticipantDto> getHorseDetailsForPastYear(HorseTournamentHistoryRequest request)
      throws FailedToRetrieveException {
    LOG.trace("getHorseDetailsForPastYear{{}}", request);
    return request.getHorses().stream().flatMap(horse -> horseTourneyLinkerDao
        .getHorseDetailsForPastYear(horse, request.getDateOfCurrentTournament())
        .stream());
  }

  @Override
  public TournamentStandingsDto getById(long id) throws NotFoundException, FailedToRetrieveException {
    LOG.trace("getById({})", id);
    var tournament = tournamentDao.getById(id);
    var participants = horseTourneyLinkerDao.findParticipantsByTournamentId(id).toArray(new TournamentDetailParticipantDto[0]);
    var treeBuilder = new TournamentStandingsTreeFactory(participants);
    var tree = treeBuilder.buildTree();
    return TournamentStandingsDto.createFromTournament(tournament, participants, tree);
  }

  @Override
  public List<Tournament> getTournamentsAssociatedWithHorseId(long id) throws FailedToRetrieveException {
    LOG.trace("getTournamentsAssociatedWithHorseId({})", id);
    return horseTourneyLinkerDao.getTournamentsAssociatedWithHorseId(id);
  }
}
