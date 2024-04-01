package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseTourneyLinkerDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
  public TournamentDetailDto getById(long id) throws NotFoundException, FailedToRetrieveException {
    LOG.trace("getById({})", id);
    var tournament = tournamentDao.getById(id);
    var participants = horseTourneyLinkerDao.findParticipantsByTournamentId(id);
    return new TournamentDetailDto(tournament.getId(),
                                   tournament.getName(),
                                   tournament.getStartDate(),
                                   tournament.getEndDate(),
                                   participants.toArray(new TournamentDetailParticipantDto[0]));
  }


  @Override
  public List<Tournament> getTournamentsAssociatedWithHorseId(long id) throws FailedToRetrieveException {
    LOG.trace("getTournamentsAssociatedWithHorseId({})", id);
    return horseTourneyLinkerDao.getTournamentsAssociatedWithHorseId(id);
  }
}
