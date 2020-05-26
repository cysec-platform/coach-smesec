package eu.smesec.library.parser;

public class ParserUnexpectedTokenException extends ParserException{

  public ParserUnexpectedTokenException(ParserLine l) {
    super("Got unexpected token",l);
  }

}
