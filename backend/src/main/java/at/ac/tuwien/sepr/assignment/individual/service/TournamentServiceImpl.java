package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@Service
public class TournamentServiceImpl implements TournamentService {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentDao dao;
  private final TournamentMapper mapper;
  // TODO: private final TournamentValidator validator;

  public TournamentServiceImpl(TournamentDao dao, TournamentMapper mapper) {
    this.dao = dao;
    this.mapper = mapper;
  }

  @Override
  public Stream<TournamentListDto> search(TournamentSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var tournaments = dao.search(searchParameters);
    return tournaments.stream().map(mapper::entityToListDto);
  }

  @Override
  public TournamentDetailDto create(TournamentDetailDto tournament) throws ValidationException, ConflictException {
    LOG.trace("create({})", tournament);
    // validator.validateForCreate(tournament); TODO: add backend validation
    var createdTournament = dao.create(tournament);
    return mapper.entityToDetailDto(createdTournament);
  }

  @Override
  public TournamentDetailDto update(TournamentDetailDto tournament) throws NotFoundException, ValidationException, ConflictException {
    return null;
  }

  @Override
  public TournamentDetailDto getById(long id) throws NotFoundException {
    return null;
  }

  @Override
  public void deleteTournamentById(long id) throws NotFoundException {

  }
}
