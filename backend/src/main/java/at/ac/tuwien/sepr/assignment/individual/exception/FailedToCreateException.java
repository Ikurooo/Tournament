package at.ac.tuwien.sepr.assignment.individual.exception;

public class FailedToCreateException extends RuntimeException {

  public FailedToCreateException(String message) {
    super(message);
  }

  public FailedToCreateException(String message, Throwable cause) {
    super(message, cause);
  }
}
