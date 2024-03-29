package at.ac.tuwien.sepr.assignment.individual.dto;

/**
 * Data Transfer Object (DTO) representing the response for a deletion operation.
 */
public record DeletionResponseDto(
    String message,
    boolean success
) {
}
