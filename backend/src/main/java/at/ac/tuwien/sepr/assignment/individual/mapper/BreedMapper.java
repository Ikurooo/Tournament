package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

/**
 * Mapper class to convert between {@link Breed} entity and {@link BreedDto} DTO.
 */
@Component
public class BreedMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Converts a {@link Breed} entity to a {@link BreedDto} DTO.
   *
   * @param breed The {@link Breed} entity to convert.
   * @return The converted {@link BreedDto} DTO.
   */
  public BreedDto entityToDto(Breed breed) {
    LOG.trace("entityToDto({})", breed);
    return new BreedDto(breed.getId(), breed.getName());
  }
}
