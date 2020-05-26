package eu.smesec.library.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Command {

  private static Map<String, Command> commands = new HashMap<>();

  static {
    registerCommands();
  }

  public static void registerCommands() {
    registerCommand("null", new CommandBlank());
    registerCommand("addScore", new CommandAddScore());
    registerCommand("capScore", new CommandCapScore());
    registerCommand("and", new CommandAnd());
    registerCommand("or", new CommandOr());
    registerCommand("not", new CommandNot());
    registerCommand("xor", new CommandXor());
    registerCommand("if", new CommandIf());
    registerCommand("concat", new CommandConcat());
    registerCommand("set", new CommandSetVar());
    registerCommand("get", new CommandGetVar());
    registerCommand("setHidden", new CommandSetHidden());
    registerCommand("setNext", new CommandNext());
    registerCommand("isSelected", new CommandIsSelected());
    registerCommand("isAnswered", new CommandIsAnswered());
    registerCommand("addBadge", new CommandAddBadge());
    registerCommand("addBadgeClass", new CommandAddBadgeClass());
    registerCommand("awardBadge", new CommandAwardBadge());
    registerCommand("revokeBadge", new CommandRevokeBadge());
    registerCommand("addRecommendation", new CommandAddRecommendation());
    registerCommand("revokeRecommendation", new CommandRevokeRecommendation());
    registerCommand("createSubcoach", new CommandCreateSubcoach());
    registerCommand("print", new CommandPrint());
  }

  public static void registerCommand(String commandName, Command command) {
    commands.put(commandName, command);
  }

  public static Command getCommand(String commandName) {
    return commandName == null ? null : commands.get(commandName);
  }

  protected int numberOfNormalizedParams = -1; /* -1 denotes "all" */

  public int getNumberOfNormalizedParams() {
    return numberOfNormalizedParams;
  }

  public Atom execute(List<Atom> list, CoachContext coachContext, ExecutorContext eContext) throws ExecutorException {
    CoachContext cc = coachContext.copy();
    cc.setContext(eContext);
    return execute(list, cc);
  }

  public abstract Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException;

}
