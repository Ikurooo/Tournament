package at.ac.tuwien.sepr.assignment.individual.exception;

/**
 * Exception thrown when an error occurs while trying to retrieve an object or perform a retrieve operation.
 */
public class FailedToRetrieveException extends RuntimeException {
  public FailedToRetrieveException(String message) {
    super(message);
  }

  public FailedToRetrieveException(String message, Throwable cause) {
    super(message, cause);
  }
}

