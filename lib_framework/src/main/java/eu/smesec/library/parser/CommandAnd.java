package eu.smesec.library.parser;

import java.util.List;

public class CommandAnd extends CommandAbstractBoolOp {

  @Override
  boolean evaluate(List<Boolean> list, ExecutorContext context) {
    for ( boolean b:list ) {
      if(!b) {
        return false;
      }
    }
    return true;
  }

}
