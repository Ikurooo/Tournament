package at.ac.tuwien.sepr.assignment.individual.exception;


/**
 * Exception thrown when an error occurs while trying to update an object or perform an update operation.
 */
public class FailedToUpdateException extends RuntimeException {
  public FailedToUpdateException(String message) {
    super(message);
  }

  public FailedToUpdateException(String message, Throwable cause) {
    super(message, cause);
  }
}

