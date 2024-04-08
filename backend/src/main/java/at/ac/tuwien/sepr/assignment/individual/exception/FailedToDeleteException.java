package at.ac.tuwien.sepr.assignment.individual.exception;

/**
 * Exception thrown when an error occurs while trying to delete an object or perform a delete operation.
 */
public class FailedToDeleteException extends RuntimeException {
  public FailedToDeleteException(String message) {
    super(message);
  }

  public FailedToDeleteException(String message, Throwable cause) {
    super(message, cause);
  }
}

