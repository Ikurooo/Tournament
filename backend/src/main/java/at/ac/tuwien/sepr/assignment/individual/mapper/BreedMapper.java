package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import org.springframework.stereotype.Component;

/**
 * Mapper class to convert between {@link Breed} entity and {@link BreedDto} DTO.
 */
@Component
public class BreedMapper {

  /**
   * Converts a {@link Breed} entity to a {@link BreedDto} DTO.
   *
   * @param breed The {@link Breed} entity to convert.
   * @return The converted {@link BreedDto} DTO.
   */
  public BreedDto entityToDto(Breed breed) {
    return new BreedDto(breed.getId(), breed.getName());
  }

  /**
   * Converts a {@link BreedDto} DTO to a {@link Breed} entity.
   *
   * @param breedDto The {@link BreedDto} DTO to convert.
   * @return The converted {@link Breed} entity.
   */
  public Breed dtoToBreed(BreedDto breedDto) {
    return new Breed().setId(breedDto.id());
  }
}
