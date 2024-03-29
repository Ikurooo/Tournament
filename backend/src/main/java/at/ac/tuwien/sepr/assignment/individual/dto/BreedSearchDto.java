package at.ac.tuwien.sepr.assignment.individual.dto;

/**
 * Data Transfer Object (DTO) for searching Breed entities.
 * Represents search parameters for filtering breeds.
 */
public record BreedSearchDto(
    String name,
    Integer limit
) {
}
