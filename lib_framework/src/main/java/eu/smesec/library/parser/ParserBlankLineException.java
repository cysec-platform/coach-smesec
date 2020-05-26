package eu.smesec.library.parser;

public class ParserBlankLineException extends ParserException{

  public ParserBlankLineException(ParserLine l) {
    super("Got unexpected blank line",l);
  }

}
