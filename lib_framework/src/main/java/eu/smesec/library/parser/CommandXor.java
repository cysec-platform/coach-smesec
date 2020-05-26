package eu.smesec.library.parser;

import java.util.List;

public class CommandXor extends CommandAbstractBoolOp {

  @Override
  boolean evaluate(List<Boolean> list,ExecutorContext context) {
    int ret = 0;
    for ( boolean b:list ) {
      if(b) {
        ret++;
      }
    }
    return ret==1;
  }

}
