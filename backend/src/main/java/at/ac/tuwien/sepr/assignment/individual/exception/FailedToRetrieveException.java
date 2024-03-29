package at.ac.tuwien.sepr.assignment.individual.exception;

/**
 * Exception thrown when a retrieve operation fails.
 */
public class FailedToRetrieveException extends RuntimeException {
  public FailedToRetrieveException(String message) {
    super(message);
  }

  public FailedToRetrieveException(String message, Throwable cause) {
    super(message, cause);
  }
}

