package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToRetrieveException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseTourneyLinkerDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link TournamentService} interface.
 */
@Service
public class TournamentServiceImpl implements TournamentService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentDao dao;
  private final TournamentMapper mapper;
  private final TournamentValidator validator;
  private final HorseTourneyLinkerDao horseTourneyLinkerDao;

  /**
   * Constructor for TournamentServiceImpl.
   *
   * @param dao                   The TournamentDao instance
   * @param mapper                The TournamentMapper instance
   * @param validator             The TournamentValidator instance
   * @param horseTourneyLinkerDao The HorseTourneyLinkerDao instance
   */
  public TournamentServiceImpl(TournamentDao dao, TournamentMapper mapper, TournamentValidator validator, HorseTourneyLinkerDao horseTourneyLinkerDao) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.horseTourneyLinkerDao = horseTourneyLinkerDao;
  }

  @Override
  public Stream<TournamentListDto> search(TournamentSearchDto searchParameters) throws FailedToRetrieveException {
    LOG.trace("search({})", searchParameters);
    var tournaments = dao.search(searchParameters);
    return tournaments.stream().map(mapper::entityToListDto);
  }

  @Override
  public Tournament create(TournamentCreateDto tournament)
      throws ValidationException, ConflictException, FailedToCreateException {
    LOG.trace("create({})", tournament);
    validator.validateForCreate(tournament);
    return horseTourneyLinkerDao.create(tournament);
  }
}
