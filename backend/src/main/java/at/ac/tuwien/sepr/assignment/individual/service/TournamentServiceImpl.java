package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
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
 * Implementation of the TournamentService interface.
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
   * @param dao       The TournamentDao instance
   * @param mapper    The TournamentMapper instance
   * @param validator The TournamentValidator instance
   * @param horseTourneyLinkerDao The HorseTourneyLinkerDao instance
   */
  public TournamentServiceImpl(TournamentDao dao, TournamentMapper mapper,
                               TournamentValidator validator,
                               HorseTourneyLinkerDao horseTourneyLinkerDao) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.horseTourneyLinkerDao = horseTourneyLinkerDao;
  }

  @Override
  public Stream<TournamentListDto> search(TournamentSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var tournaments = dao.search(searchParameters);
    return tournaments.stream().map(mapper::entityToListDto);
  }

  @Override
  public TournamentDetailDto create(TournamentDetailDto tournament)
      throws ValidationException, ConflictException, NotFoundException {
    validator.validateForCreate(tournament);
    LOG.trace("create({})", tournament);
    var createdTournament = horseTourneyLinkerDao.create(tournament);
    return mapper.entityToDetailDto(createdTournament);
  }

  @Override
  public TournamentDetailDto update(TournamentDetailDto tournament)
      throws NotFoundException, ValidationException, ConflictException {
    // Implementation of update method
    return null;
  }

  @Override
  public TournamentDetailDto getById(long id) throws NotFoundException {
    // Implementation of getById method
    return null;
  }

  @Override
  public void deleteTournamentById(long id) throws NotFoundException {
    // Implementation of deleteTournamentById method
  }
}
