package eu.smesec.library.parser;

import java.util.List;

public class CommandNot extends CommandAbstractBoolOp {

  @Override
  boolean evaluate(List<Boolean> list, ExecutorContext context) throws ExecutorException {
    if(list.size()!=1) {
      throw new ExecutorException("NOT supports only one parameter");
    }
    return ! list.get(0);
  }

}
