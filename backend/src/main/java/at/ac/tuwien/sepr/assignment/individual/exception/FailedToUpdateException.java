package at.ac.tuwien.sepr.assignment.individual.exception;


/**
 * Exception thrown when an update operation fails.
 */
public class FailedToUpdateException extends RuntimeException {
  public FailedToUpdateException(String message) {
    super(message);
  }

  public FailedToUpdateException(String message, Throwable cause) {
    super(message, cause);
  }
}

