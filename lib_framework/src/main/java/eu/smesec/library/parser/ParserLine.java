package eu.smesec.library.parser;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserLine {

  /***
   * <p>A micro parser/scanner.</p>
   *
   * @author Martin Gwerder
   *
   */
  /* specifies the context range which is given when an exception arises during scanning */
  private static final int MAX_CONTEXT = 30;

  /* These are character subsets specified in RFC3501 */
  private static final String ABNF_SP = " \t\r\n";
  private static final String ABNF_ATOM_SPECIALS = charlistBuilder(0, 31);
  private static final String ABNF_QUOTED_SPECIALS = "\"\\";
  private static final String ABNF_ATOM_CHAR = charlistDifferencer(
          charlistBuilder(1, 127),
          ABNF_ATOM_SPECIALS
  );
  private static final String ABNF_TEXT_CHAR = charlistDifferencer(
          charlistBuilder(1, 127),
          "\r\n"
  );
  private static final String ABNF_QUOTED_CHAR = charlistDifferencer(
          ABNF_TEXT_CHAR,
          ABNF_QUOTED_SPECIALS
  );
  private static final String ABNF_TAG = charlistDifferencer(charlistDifferencer(charlistDifferencer(charlistBuilder(48, 122), charlistBuilder(58, 64)), charlistBuilder(91, 94)), charlistBuilder(96, 96));

  /* a Logger  for logging purposes */
  private static final Logger LOGGER = Logger.getLogger(
          (new Throwable()).getStackTrace()[0].getClassName());

  /* this holds the past context for the case that an exception is risen */
  private String context = "";

  /* this is the buffer of read but unprocessed characters */
  private String buffer = "";

  /***
   * <p>Creates a parser line object with a parser for a command.</p>
   *
   * <p>A passed input stream is appended to line. Reading takes place according to the
   * ABNF-Rules.</p>
   *
   * @param line  The String which has already been read (as Read ahead)
   *
   * @throws ParserException if reading fails
   */
  public ParserLine(String line) throws ParserException {
    this.buffer = line;

    // check if nothing at all (no even an empty line) has been passed
    if (this.buffer == null) {
      throw new ParserNullLineException(this);
    }

    prepareStorage(line);

    checkEmptyLine();
  }

  /***
   * <p>Builds a set of chracters ranging from the ASCII code of start until the ASCII code
   * of end.</p>
   *
   * <p>This helper is mainly used to build ABNF strings.</p>
   *
   * @param start The first ASCII code to be used
   * @param end   The last ASCII code to be used
   * @return the generated character list
   */
  public static String charlistBuilder(int start, int end) {
    // reject chain building if start is not within 0..255
    if (start < 0 || start > 255) {
      return null;
    }

    // reject chain building if end is not within 0..255
    if (end < 0 || end > 255) {
      return null;
    }

    // reject chain building if start>end
    if (end < start) {
      return null;
    }

    // build the string
    StringBuilder ret = new StringBuilder();
    for (int i = start; i <= end; i++) {
      ret.append((char) i);
    }
    return ret.toString();
  }

  /***
   * <p>Removes a given set of characters from a superset.</p>
   *
   * @param superset    the set where character should be removed from
   * @param subset      the set of characters to be removed
   * @return the difference of the two given charsets
   */
  public static String charlistDifferencer(String superset, String subset) {
    String ret = superset;
    for (int i = 0; i < subset.length(); i++) {
      char c = subset.charAt(i);
      ret = ret.replace("" + c, "");
    }
    return ret;
  }

  private void prepareStorage(String line)
          throws ParserException {
    // make sure that a line is never null when reaching the parsing section
    if (buffer == null) {
      buffer = "";
      if (snoopBytes(1) == null) {
        throw new ParserBlankLineException(this);
      }
    }

  }

  private void checkEmptyLine() throws ParserException {
    if ("\r\n".equals(snoopBytes(2)) || "".equals(snoopBytes(1)) || snoopBytes(1) == null) {
      if ("\r\n".equals(snoopBytes(2))) {
        skipUntilLineEnd();
        throw new ParserBlankLineException(this);
      }
      throw new ParserNullLineException(this);
    }
  }

  /***
   * <p>Returns true if escaped quotes are present at the current position.</p>
   *
   * @return true if escaped quotes are present
   */
  public boolean snoopEscQuotes() {
    return "\\".contains(snoopBytes(1))
            && snoopBytes(2).length() == 2
            && ABNF_QUOTED_SPECIALS.contains(snoopBytes(2).substring(1, 2));
  }

  private boolean readBuffer(long num) {
    // make sure that we have sufficient bytes in the buffer

    // we have no additional source to read from

    return num <= buffer.length();
  }


  /***
   * <p>Get the specified number of characters without moving from the current position.</p>
   *
   * <p>if num is 0 or negative then null is returned. If the number
   * of available bytes is lower than the number of requested characters
   * then the buffer content is returned.</p>
   *
   * @param num the number of bytes to be snooped
   * @return the requested string
   */
  public String snoopBytes(long num) {
    if (num <= 0) {
      return null;
    }

    // build buffer
    readBuffer(num);

    if (buffer.length() == 0) {
      return null;
    }

    // if the string is too short -> return it
    if (buffer.length() < num) {
      return buffer;
    }

    return buffer.substring(0, (int) num);
  }

  private void addContext(String chunk) {
    context += chunk;
    if (context.length() > MAX_CONTEXT) {
      context = context.substring(context.length() - MAX_CONTEXT, context.length());
    }
  }

  /***
   * <p>Returns the current buffer (including position) and some of the already read characters.</p>
   *
   * @return String representation of the current context
   */
  public String getContext() {
    return context + "^^^" + buffer;
  }

  /***
   * <p>Skips the specified number of characters and adds them to the past context.</p>
   *
   * @param   num number of characters to be skipped
   * @return String representation of the skipped characters
   */
  public String skipBytes(long num) {
    return skipBytes(num, true);
  }

  /***
   * <p>Skips the specified number of bytes.</p>
   *
   * @param num        the number of bytes to be skipped
   * @param modContext if true the context is updated by the operation
   * @return the skipped bytes
   */
  public String skipBytes(long num, boolean modContext) {
    // make sure that we have sufficient bytes in the buffer
    readBuffer(num);

    // if the string is too short -> return it
    if (buffer.length() < num) {
      buffer = "";
      if (modContext) {
        addContext(buffer);
      }
      return buffer;
    }

    // prepare return result
    String ret = snoopBytes(num);

    // update context
    if (modContext) {
      addContext(ret);
    }

    // cut the buffer
    buffer = buffer.substring((int) num, buffer.length());

    return ret;
  }

  /***
   * <p>Skips whitespaces and line ends.</p>
   *
   * @return number of skipped spaces
   */
  public int skipWhitespaceOnly() {
    // count number of spaces found
    int count = 0;

    LOGGER.log(Level.FINER, "Skipping whitespaces");
    // loop thru skipper
    while (snoopBytes(1) != null && ABNF_SP.contains(snoopBytes(1))) {
      skipBytes(1);
      count++;
    }
    LOGGER.log(Level.FINER, "Skipped " + count + " spaces");

    // return count of spaces skipped
    return count;
  }

  /***
   * <p>Skips all non functional letters (whitespaces, line ends, and comments)</p>
   *
   * @return number of skipped characters
   */
  public int skipNoFunc() {
    // count number of spaces found
    int count = 0;

    // loop thru skipper
    while (snoopBytes(1) != null && (ABNF_SP.contains(snoopBytes(1)) || "//".equals(snoopBytes(2)))) {
      count += skipWhitespaceOnly();

      // skip comment (if any)
      if ("//".equals(snoopBytes(2))) {
        skipBytes(2);
        skipUntilLineEnd();
      }

      count += skipWhitespaceOnly();
    }
    LOGGER.log(Level.FINER, "Skipped " + count + " NoFunc letters");

    // return count of spaces skipped
    return count;
  }

  /***
   * <p>Skips a CRLF combo in the buffer.</p>
   *
   * @return True if a combo has been skipped
   */
  public boolean skipLineEnd() {
    LOGGER.log(Level.FINER, "Skipping CRLF");
    if (snoopBytes(2) != null && "\r\n".equals(snoopBytes(2))) {
      skipBytes(2);
      LOGGER.log(Level.FINER, "CRLF skipped");
      return true;
    }
    LOGGER.log(Level.FINER, "no CRLF found");
    return false;
  }

  /***
   * <p>Skips up to a CRLF combo in the buffer.</p>
   *
   * @return True if a combo has been skipped (false if buffer ended before a CRLF combo was read
   */
  public boolean skipUntilLineEnd() {
    while (snoopBytes(1) != null && !"\r\n".contains(snoopBytes(1))) {
      skipBytes(1, false);
    }

    if (snoopBytes(2) != null && "\r\n".contains(snoopBytes(2))) {
      skipBytes(2, false);
      return true;
    } else if (snoopBytes(1) != null && "\r\n".contains(snoopBytes(1))) {
      skipBytes(1, false);
      return true;
    }
    return false;
  }

  private long getLengthPrefix() {
    //skip curly brace
    skipBytes(1);

    // get number

    long num = 0;
    while (snoopBytes(1) != null && "0123456789".contains(snoopBytes(1)) && num < 4294967295L) {
      num = num * 10 + (int) (skipBytes(1).charAt(0)) - (int) ("0".charAt(0));
    }
    return num;
  }

  private String getQuotedString() {
    // get a quoted string
    skipBytes(1);
    StringBuilder ret = new StringBuilder();
    while (snoopBytes(1) != null && (ABNF_QUOTED_CHAR.contains(snoopBytes(1))
            || snoopEscQuotes())) {
      if ("\\".contains(snoopBytes(1))) {
        ret.append(skipBytes(2).substring(1, 2));
      } else {
        ret.append(skipBytes(1));
      }
    }
    if (snoopBytes(1) == null || !"\"".equals(skipBytes(1))) {
      return null;
    }

    return ret.toString();
  }

  /***
   * <p>Get an IMAP String from the buffer (quoted or prefixed).</p>
   *
   * @return The String or null if no string is at the current position
   */
  public String getString() {
    String start = snoopBytes(1);
    String ret = null;

    if ("\"".equals(start)) {

      return getQuotedString();

    }

    return ret;
  }

  /***
   * <p>Get an IMAP AString (direct, quoted or prefixed) from the current buffer position.</p>
   *
   * @return The String or null if no string at the current position
   */
  public String getAtomName() {

    String start = snoopBytes(1);
    String ret = null;

    // get a sequence of atom chars
    while (snoopBytes(1) != null && ABNF_TAG.contains(snoopBytes(1))) {
      if (ret == null) {
        ret = "";
      }
      ret += skipBytes(1);
    }

    return ret;
  }

  /***
   * <p>Get the tag at the current position.</p>
   *
   * @return the tag or null if no valid is found
   */
  public String getATag() {
    String ret = null;

    // get a sequence of atom chars
    while (snoopBytes(1) != null && ABNF_TAG.contains(snoopBytes(1))) {
      if (ret == null) {
        ret = "";
      }
      ret += skipBytes(1);
    }

    if ("".equals(ret)) {
      // empty tags are not allowed. At least one char is required
      return null;
    }

    return ret;
  }

  public Atom getNumericalAtom() throws ParserException {
    Matcher REGEX_INTEGER = Pattern.compile("^([-+]?\\d+)").matcher(buffer);
    Matcher REGEX_FLOAT = Pattern.compile("^([-+]?\\d*\\.\\d+)").matcher(buffer);
    if (REGEX_FLOAT.find()) {
      // get float
      String id = "" + Double.valueOf(REGEX_FLOAT.group(1));
      skipBytes(REGEX_FLOAT.group(1).length());
      return new Atom(Atom.AtomType.FLOAT, id, null);
    } else if (REGEX_INTEGER.find()) {
      // get integer
      String id = "" + Integer.valueOf(REGEX_INTEGER.group(1));
      skipBytes(REGEX_INTEGER.group(1).length());
      return new Atom(Atom.AtomType.INTEGER, id, null);
    } else {
      throw new ParserException("Exception while getting numerical atom", this);
    }
  }

  public Atom getAtom() throws ParserException {
    skipNoFunc();
    Atom ret = null;
    if ("\"".equals(snoopBytes(1))) {
      // get string
      String s = getQuotedString();
      // collate value
      ret = new Atom(Atom.AtomType.STRING, s, null);
    } else if ("TRUE".equals(snoopBytes(4)) || "FALSE".equals(snoopBytes(5))) {
      // get boolean
      ret = new Atom(Atom.AtomType.BOOL, getATag().equals("TRUE") ? "TRUE" : "FALSE", null);
    } else if ("-+.0123456789".contains(snoopBytes(1))) {
      // get numerical
      ret = getNumericalAtom();
    } else if (ABNF_TAG.contains(snoopBytes(1))) {
      int parentPointer = 0;
      if("root.".equals(snoopBytes(5))) {
        skipBytes(5);
        parentPointer = -1;
      } else {
        while("parent.".equals(snoopBytes(7))) {
          skipBytes(7);
          parentPointer++;
        }
      }
      // get Methode
      String commandName = getAtomName();
      if ("NULL".equals(commandName)) {
        ret = Atom.NULL_ATOM;
      } else {
        if (!Atom.validateCommand(commandName)) {
          throw new ParserException("got unknown methode \"" + commandName + "\"", this);
        }
        if (!"(".equals(snoopBytes(1))) {
          throw new ParserException("expected \"(\"", this);
        }
        skipBytes(1);
        skipNoFunc();
        List<Atom> parameters = new Vector<>();
        while (!")".equals(snoopBytes(1))) {
          skipNoFunc();
          parameters.add(getAtom());
          skipNoFunc();

          // expect ", [^)]" or ")"
          if (",".equals(snoopBytes(1))) {
            // skip atom delimiter
            skipBytes(1);
            skipNoFunc();
            if (")".equals(snoopBytes(1))) {
              throw new ParserUnexpectedTokenException(this);
            }
          }
        }
        skipBytes(1);
        skipNoFunc();

        ret = new Atom(Atom.AtomType.METHODE, commandName, parameters);
        ret.setParent(parentPointer);

      }
    } else {
      throw new ParserUnexpectedTokenException(this);
    }
    skipNoFunc();
    return ret;
  }

  public CySeCLineAtom getCySCStatement() throws ParserException {
    // get condition
    skipNoFunc();
    Atom cond = getAtom();

    // skip delimiter
    skipNoFunc();
    if (!":".equals(snoopBytes(1))) {
      throw new ParserException("expected \":\"", this);
    }
    skipBytes(1);
    skipNoFunc();

    // get group label
    String name = getAtomName();
    skipNoFunc();

    // skip delimiter
    if (!":".equals(snoopBytes(1))) {
      throw new ParserException("expected \":\"", this);
    }
    skipBytes(1);
    skipNoFunc();

    // get script
    if ("{".equals(snoopBytes(1))) {
      List<Atom> l = new Vector<>();

      // get braced statements
      skipBytes(1);
      skipNoFunc();

      while (!"".equals(snoopBytes(1)) && !"}".equals(snoopBytes(1))) {
        l.add(getAtom());
        skipNoFunc();
        if (!";".equals(snoopBytes(1))) {
          throw new ParserException("expected \";\"", this);
        }
        skipBytes(1);
        skipNoFunc();
      }

      if (!"}".equals(snoopBytes(1))) {
        throw new ParserException("expected \"}\"", this);
      }
      skipBytes(1);
      skipNoFunc();
      if (!";".equals(snoopBytes(1))) {
        throw new ParserException("expected \";\"", this);
      }
      skipBytes(1);
      skipNoFunc();

      return new CySeCLineAtom(cond, name, l.toArray(new Atom[l.size()]));
    } else {
      Atom statement = getAtom();

      // skip delimiter
      skipNoFunc();
      if (!";".equals(snoopBytes(1))) {
        throw new ParserException("expected \":\"", this);
      }
      skipBytes(1);
      skipNoFunc();

      return new CySeCLineAtom(cond, name, new Atom[]{statement});
    }
  }

  public List<CySeCLineAtom> getCySeCListing() throws ParserException {
    List<CySeCLineAtom> l = new Vector<>();
    skipNoFunc();
    while (snoopBytes(1) != null) {
      l.add(getCySCStatement());
      skipNoFunc();
    }
    return l;
  }

}
