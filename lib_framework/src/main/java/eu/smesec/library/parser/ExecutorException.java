package eu.smesec.library.parser;

import java.io.IOException;

/**
 * The parser throws this question while executing a command
 *
 * <p>This is different from ParserException in that the syntax of the command is correct,
 * however, it tries to do something that can't be executed, e.g hiding a question with invalid ID.</p>
 * @see ParserException
 */
public class ExecutorException extends IOException {
  // reason is a detailed string that marks the spot that caused the parser to throw the exception
  private String reason;
  private ExecutorException daisy = null;

  public ExecutorException(String reason) {
    super(reason);
    this.reason = reason;
  }

  public ExecutorException(String reason, ExecutorException daisy) {
    this(reason);
    this.daisy = daisy;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (daisy != null) {
      sb.append(daisy.toString());
      sb.append(reason + System.lineSeparator());
    }
    sb.append("Executor throws exception: " + reason);
    return sb.toString();
  }

  public String getReason() {
    return reason;
  }
}
