package eu.smesec.library.parser;

import java.io.IOException;

/**
 * The parser throws this exception if it encounters a syntax error in the coach logic.
 * <p>This is different from ParserException in that the syntax of the command is correct,
 * however, it tries to do something that can't be executed, e.g hiding a question with invalid ID.</p>
 * @see ParserBlankLineException
 * @see ParserNullLineException
 * @see ParserUnexpectedTokenException
 */
public class ParserException extends IOException {
  private ParserLine line;
  // reason is a detailed string that marks the spot that caused the parser to throw the exception
  private String reason;

  public ParserException(String reason, ParserLine line) {
    super("Parser throws exception: " + reason + (line!=null?" ("+line.getContext()+")":""));
    this.reason = reason;
    this.line = line;
  }

  /***
   * <p>Returns the ParserLine throowing the exception.</p>
   *
   * @return the requested parser line
   */
  public ParserLine getParserLine() {
    return line;
  }

  public String toString() {
    return "Parser throws exception: " + reason + (line!=null?" ("+line.getContext()+")":"");
  }
}
