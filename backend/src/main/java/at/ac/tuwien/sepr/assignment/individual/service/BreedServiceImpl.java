package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.mapper.BreedMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.BreedDao;

import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link BreedService} interface that provides methods for managing breeds.
 */
@Service
public class BreedServiceImpl implements BreedService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final BreedDao dao;
  private final BreedMapper mapper;

  /**
   * Constructor for the BreedServiceImpl class
   *
   * @param dao    the BreedDao instance to use for database operations
   * @param mapper the BreedMapper instance to use for mapping entities to DTOs
   */
  public BreedServiceImpl(BreedDao dao, BreedMapper mapper) {
    this.dao = dao;
    this.mapper = mapper;
  }

  @Override
  public Stream<BreedDto> allBreeds() {
    LOG.trace("allBreeds()");
    return dao.allBreeds()
        .stream()
        .map(mapper::entityToDto);
  }

  @Override
  public Stream<BreedDto> findBreedsByIds(Set<Long> breedIds) {
    LOG.trace("findBreedsByIds({})", breedIds);
    return dao.findBreedsById(breedIds)
        .stream()
        .map(mapper::entityToDto);
  }

  @Override
  public Stream<BreedDto> search(BreedSearchDto searchParams) {
    LOG.trace("search({})", searchParams);
    return dao.search(searchParams)
        .stream()
        .map(mapper::entityToDto);
  }
}
