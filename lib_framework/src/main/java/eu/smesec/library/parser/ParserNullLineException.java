package eu.smesec.library.parser;

public class ParserNullLineException extends ParserException{

  public ParserNullLineException(ParserLine l) {
    super("Parser line input was <null>",l);
  }

}
