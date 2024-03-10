package at.ac.tuwien.sepr.assignment.individual.exception;

/**
 * Exception thrown when an error occurs while trying to create an object or perform a creation operation.
 * This exception extends the RuntimeException class, making it unchecked.
 */
public class FailedToCreateException extends RuntimeException {

  /**
   * Constructs a new FailedToCreateException with the specified detail message.
   *
   * @param message the detail message (which is saved for later retrieval by the getMessage() method)
   */
  public FailedToCreateException(String message) {
    super(message);
  }

  /**
   * Constructs a new FailedToCreateException with the specified detail message and cause.
   *
   * @param message the detail message (which is saved for later retrieval by the getMessage() method)
   * @param cause   the cause (which is saved for later retrieval by the getCause() method)
   */
  public FailedToCreateException(String message, Throwable cause) {
    super(message, cause);
  }
}

