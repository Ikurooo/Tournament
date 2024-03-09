package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class TournamentServiceImpl implements TournamentService {

  @Override
  public Stream<TournamentListDto> search(TournamentSearchDto searchParameters) {
    return Stream.empty();
  }
  @Override
  public TournamentDetailDto create(TournamentDetailDto tournament) throws ValidationException, ConflictException {
    return null;
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
