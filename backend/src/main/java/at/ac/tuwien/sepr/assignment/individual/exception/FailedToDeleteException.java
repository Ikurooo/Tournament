package at.ac.tuwien.sepr.assignment.individual.exception;

/**
 * Exception thrown when a delete operation fails.
 */
public class FailedToDeleteException extends RuntimeException {
  public FailedToDeleteException(String message) {
    super(message);
  }

  public FailedToDeleteException(String message, Throwable cause) {
    super(message, cause);
  }
}

