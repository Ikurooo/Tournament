package at.ac.tuwien.sepr.assignment.individual.exception;

/**
 * Exception thrown when an error occurs while trying to create an object or perform a creation operation.
 */
public class FailedToCreateException extends RuntimeException {

  public FailedToCreateException(String message) {
    super(message);
  }

  public FailedToCreateException(String message, Throwable cause) {
    super(message, cause);
  }
}

