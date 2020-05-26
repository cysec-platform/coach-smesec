package eu.smesec.library.skills;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ScoreFactory {

  public enum ScoreType {
    VALUE, CAP;
  }

  public static class ScoreValue {

    private ScoreType t;
    private double value;

    public ScoreValue(ScoreType t, double value) {
      this.t = t;
      this.value = value;
    }

    public ScoreType getScoretype() {
      return t;
    }

    public double getValue() {
      return value;
    }
  }

  public static class Score {
    private Map<String, List<ScoreValue>> scores = new HashMap<>();
    private boolean hidden = false;
    private final String id;

    public Score(String id) {
      this.id = id;
    }

    public String getId() {
      return id;
    }

    public void setHidden(boolean hidden) {
      this.hidden = hidden;
    }

    public boolean isHidden() {
      return hidden;
    }

    public void reset() {
      synchronized (scores) {
        scores.clear();
      }
    }



    public void revertQuestion(String id) {
      scores.put(id.toLowerCase(), new Vector<>());
    }

    private void addQuestionScore(String id, ScoreValue v) {
      if(id==null) {
        id = "__NULL__";
      }
      synchronized (scores) {
        if (scores.get(id.toLowerCase()) == null) {
          revertQuestion(id);
        }
        List<ScoreValue> sv = scores.get(id.toLowerCase());
        synchronized (sv) {
          sv.add(v);
        }
      }
    }

    public void add(String questionId, double value) {
      addQuestionScore(questionId,new ScoreValue(ScoreType.VALUE,value));
    }

    public void cap(String questionId, double value) {
      addQuestionScore(questionId,new ScoreValue(ScoreType.CAP,value));
    }

    public double getValue() {
      synchronized (scores) {
        double totalValue = 0;
        double minCap = Double.MAX_VALUE;
        for (List<ScoreValue> svl : scores.values()) {
          synchronized (svl) {
            for (ScoreValue sv : svl) {
              switch (sv.getScoretype()) {
                case VALUE:
                  totalValue += sv.getValue();
                  break;
                case CAP:
                  minCap = Math.min(minCap, sv.getValue());
                  break;
                default:
                  new RuntimeException("Encountered unknown type of ScoreValue");
              }
            }
          }
        }
        return Math.min(minCap, totalValue);
      }
    }

  }

  private Map<String, Score> scores = new HashMap<>();

  public Score getIntScore(String id) {
    synchronized (scores) {
      if (scores.get(id.toLowerCase()) == null) {
        scores.put(id.toLowerCase(), new Score(id));
      }
      return scores.get(id.toLowerCase());
    }
  }

  public Score[] getScoreList(boolean includeHidden) {
    List<Score> ret = new Vector<>();
    synchronized (scores) {
      for( Map.Entry<String,Score> s: scores.entrySet()) {
        if (!s.getValue().isHidden() || includeHidden) {
          ret.add(s.getValue());
        }
      }
    }
    return ret.toArray(new Score[ret.size()]);
  }

  public double getScore(String id) {
    return getIntScore(id).getValue();
  }

  public double removeScore(String id) {
    double ret = getIntScore(id).getValue();
    scores.remove(id);
    return ret;
  }

  public void revertQuestion(String questionId) {
    synchronized (scores) {
      for (Score s : scores.values()) {
        s.revertQuestion(questionId);
      }
    }
  }

  public void addQuestionScore(String scoreId, String questionId, ScoreType t, double value) {
    getIntScore(scoreId).addQuestionScore(questionId, new ScoreValue(t, value));
  }

  public void reset() {
    reset(null);
  }

  public void reset(String scoreId) {
    if (scoreId == null) {
      synchronized (scores) {
        for (Score s : scores.values()) {
          s.reset();
        }
      }
    } else {
      getIntScore(scoreId).reset();
    }
  }
}
