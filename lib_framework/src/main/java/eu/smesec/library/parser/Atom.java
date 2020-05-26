package eu.smesec.library.parser;

import java.util.List;
import java.util.Vector;

public class Atom {

  public static Atom NULL_ATOM = new Atom(AtomType.NULL, null, null);

  public enum AtomType {METHODE, INTEGER, FLOAT, BOOL, STRING, NULL;}

  private AtomType type = null;
  private String id = null;
  private List<Atom> parameters = new Vector<>();
  private int parentPointer = 0;

  public Atom(AtomType type, String id, List<Atom> parameters) {
    this.type = type;
    this.id = id;
    this.parameters = parameters;
  }

  public Atom(AtomType type, String id, List<Atom> parameters, int parent) {
    this(type,id,parameters);
    this.parentPointer = parent;
  }

  private ExecutorContext getExecutorContext(CoachContext cc) {
    int i = parentPointer;
    ExecutorContext context = cc.getContext();
    while (i != 0 && context.getParent() != null) {
      context = context.getParent();
      i--;
    }
    return context;
  }

  public Atom execute(CoachContext coachContext) throws ExecutorException {
    return execute(coachContext, getExecutorContext(coachContext));
  }

  private Atom execute(CoachContext coachContext, ExecutorContext context) throws ExecutorException {
    if (type == AtomType.METHODE) {
      // execute here
      Command command = Command.getCommand(id);
      if (command != null) {

        // normalize parameter list as far as we can
        List<Atom> pl = new Vector<>();
        int i = 0;
        for (Atom a : parameters) {
          i++;
          if (a.getType() == AtomType.METHODE && (i <= command.getNumberOfNormalizedParams() || command.getNumberOfNormalizedParams() == -1)) {
            a = a.execute(coachContext);
          }
          pl.add(a);
        }

        // execute command
        return command.execute(pl, coachContext, getExecutorContext(coachContext));
      } else {
        throw new ExecutorException("Found unknown methode \"" + id + "\"");
      }
    } else {
      return this;
    }
  }

  public static boolean validateCommand(String name) {
    return Command.getCommand(name) != null;
  }

  public int setParent(int parentPointer) {
    int ret = this.parentPointer;
    this.parentPointer = parentPointer;
    return ret;
  }

  public AtomType getType() {
    return type;
  }

  public String getId() {
    return id;
  }

  public String toString() {
    String ret = "";
    switch (type) {
      case METHODE:
        ret += id + "( ";
        for (Atom a : parameters) {
          ret += a.toString() + ", ";
        }
        if (parameters.size() >= 1) {
          ret = ret.substring(0, ret.length() - 2) + " )";
        } else {
          // in case of no atoms
          ret = ret.substring(0, ret.length() - 1) + ")";
        }
        break;
      case STRING:
        ret += "\"" + id + "\"";
        break;
      case NULL:
        ret = "NULL";
        break;
      case INTEGER:
      case FLOAT:
      case BOOL:
        ret += id;
        break;
      default:
        throw new NullPointerException("type " + type + " cannot be printed (Not implemented)");
    }
    return ret;
  }

  public boolean isTrue(CoachContext coachContext) throws ExecutorException {
    Atom eval = this;
    if (getType() == AtomType.METHODE) {
      eval = execute(coachContext);
    }
    if (eval.getType() != AtomType.BOOL) {
      throw new ExecutorException("condition \"" + toString() + "\" does not evaluate to BOOL (is:" + eval + ")");
    }
    if ("TRUE".equals(eval.getId())) {
      return true;
    } else if ("FALSE".equals(eval.getId())) {
      return false;
    } else {
      throw new ExecutorException("boolean value is illegal \"" + eval.toString() + "\" (OUCH! How did that happen)");
    }
  }

}
