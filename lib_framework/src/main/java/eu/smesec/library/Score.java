package eu.smesec.library;

import java.util.HashMap;
import java.util.Map;

public class Score {

  private int value;
  private int maxValue;
  private Map<Object, Integer> caps;
  private int cap;

  public Score(int maxValue) {
    this.value = 0;
    this.maxValue = maxValue;
    this.caps = new HashMap<>(5);
    this.cap = maxValue;
  }

  public void setValue(int value) {    this.value = value;  }

  public int getValue() {
    return Math.max(0, Math.min(cap, value));
  }

  public int getMaxValue() {
    return maxValue;
  }

  public void add(int value) {
    this.value += value;
  }

  public void sub(int value) {
    this.value -= value;
  }

  public void limit(Object id, int value) {
    caps.put(id, value);
    cap = caps.values().stream().min(Integer::compareTo).orElse(this.maxValue);
  }

  public void unlimit(Object id) {
    caps.remove(id);
    cap = caps.values().stream().min(Integer::compareTo).orElse(this.maxValue);
  }
}
