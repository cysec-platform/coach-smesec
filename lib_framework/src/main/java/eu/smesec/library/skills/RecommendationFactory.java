package eu.smesec.library.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class RecommendationFactory {

 /**
  * Represents the model of the Recommendation object.
  * This class overwrites equals() and hashCode() with custom behavior. A dedicated field "objStr",
  * composed of the string values of all attributes, is the input for the hashCode method.
  */
  public static class Recommendation {
    private String id;
    private int order = 0;
    private String urlImg;
    private String altImg = "";
    private String title = "";
    private String description = "";
    private String textLink = "#";
    private String urlLink = "#";

    public Recommendation(String id, int order, String urlImg, String altImg, String title, String description, String textLink, String urlLink) {
      this.id = id;
      this.order = order;
      this.urlImg = urlImg;
      this.altImg = altImg;
      this.title = title;
      this.description = description;
      this.textLink = textLink;
      this.urlLink = urlLink;
    }

    public String getId() {
      return id;
    }

    public int getOrder() {
      return order;
    }

    public String getImgUrl() {
      return urlImg;
    }

    public String getImgAlt() {
      return altImg;
    }

    public String getTitle() {
      return title;
    }

    public String getDescription() {
      return description;
    }

    public String getLinkText() {
      return textLink;
    }

    public String getLink() {
      return urlLink;
    }

    private String objStr() {
      return "" + id + ":" + order + ":" + urlImg + ":" + altImg + ":" + title + ":" + description + ":" + textLink + ":" + urlLink + "|";
    }

    @Override
    public int hashCode() {
      return (objStr()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Recommendation)) {
        return false;
      }
      return objStr().equals(((Recommendation) (o)).objStr());
    }

  }

  // class uses this map to synchronize all access on the data structure
  private Map<String, Recommendation> recommendations = new HashMap<>();
  private RecommendationEventListener listener = null;

  /**
   * Register the given RecommendationListener for all ChangeTypes. This may overwrite
   * a previous value.
   * @param listener the new listener
   * @return the previous listener
   */
  public RecommendationEventListener setListener(RecommendationEventListener listener) {
    RecommendationEventListener ret = listener;
    this.listener = listener;
    return ret;
  }

  /**
   * Retrieve the Recommendation with the given ID from the data structure
   * @param id the ID of the recommendation
   * @return the entry with the matching ID
   */
  public Recommendation getRecommendation(String id) {
    synchronized (recommendations) {
      return recommendations.get(id);
    }
  }

  /**
   * Remove the recommendation with the given id and emit REMOVED changetype to listeners
   * if appropriate.
   * @param id the ID of the recommendation
   * @return the entry with the matching ID
   */
  public Recommendation removeRecommendation(String id) {
    synchronized (recommendations) {
      Recommendation ret = recommendations.remove(id);
      if (ret != null && listener != null) {
        listener.recommendationChanged(id, ChangeType.REMOVED);
      }
      return ret;
    }
  }

  /**
   * Add non-null recommendation to data structure and emit respective Changetype for the case of
   * a) ADDED: no entry with this id was found i.o.w the recommendation is new
   * b) CHANGED: there was already an entry with this id, i.o.w the recommendation changed
   * @param recommendation the new or updated recommendation
   * @return the previous recommendation
   */
  public Recommendation addRecommendation(Recommendation recommendation) {
    if (recommendation == null) {
      return null;
    }

    synchronized (recommendations) {
      Recommendation ret = recommendations.get(recommendation.getId());
      if (!recommendation.equals(ret)) {
        // Add recommendation to map before notifying listener
        recommendations.put(recommendation.getId(), recommendation);
        if (listener != null) {
          listener.recommendationChanged(recommendation.getId(), ret == null ? ChangeType.ADDED : ChangeType.CHANGED);
        }
      }
      return ret;
    }
  }

  /**
   * Aggregate all assigned recommendations in an array sorted by order.
   * @return An array of all assigned recommendations
   */
  public Recommendation[] getRecommendationList() {
    Map<Integer, Recommendation> ret = new TreeMap<>();
    synchronized (recommendations) {
      for (Recommendation r : recommendations.values()) {
        ret.put(r.getOrder(), r);
      }
    }
    return new ArrayList<>(ret.values()).toArray(new Recommendation[ret.size()]);
  }

  /**
   * Clears the entire internal data structure from recommendations.
   * Beware, there is no way of restoring the values.
   */
  public void reset() {
    synchronized (recommendations) {
      recommendations.clear();
    }
  }

}
