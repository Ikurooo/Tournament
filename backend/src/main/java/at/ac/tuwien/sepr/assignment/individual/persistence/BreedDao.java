package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;

import java.util.Collection;
import java.util.Set;

public interface BreedDao {

  /**
   * Retrieves all breeds stored in the database.
   *
   * @return Collection of all breeds
   */
  Collection<Breed> allBreeds();

  /**
   * Finds breeds by their unique identifiers.
   *
   * @param breedIds Set of breed IDs to search for
   * @return Collection of breeds matching the provided IDs
   */
  Collection<Breed> findBreedsById(Set<Long> breedIds);

  /**
   * Searches for breeds based on the provided search parameters.
   *
   * @param searchParams Object containing parameters for breed search
   * @return Collection of breeds matching the search criteria
   */
  Collection<Breed> search(BreedSearchDto searchParams);
}

