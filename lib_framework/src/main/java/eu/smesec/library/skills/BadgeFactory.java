package eu.smesec.library.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Holds and manages the state of all created badges, its registered classes and the awarded instances.
 * Offers the common CRUD manipulation methods via set(), remove(), get() and clear() (which removes all badges).
 * There is always only once instance of the BadgeFactory within CysecExecutorContext.
 */
public class BadgeFactory {

  /**
   * Represents the "class", "level", "rank" or any other arbitrary noun to describe the progress on a Badge.
   */
  public static class BadgeClass {
    private String id;
    private int order = 0;
    private String urlImg;
    private String altImg = "";
    private String description = "";
    private String urlLink = "#";

    public BadgeClass(String id, int order, String urlImg, String altImg, String description, String urlLink) {
      this.id = id;
      this.order = order;
      this.urlImg = urlImg;
      this.altImg = altImg;
      this.description = description;
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

    public String getDescription() {
      return description;
    }

    public String getLink() {
      return urlLink;
    }

  }

    /**
     * Represents the model of the Badge object.
     * Saves all classes that may be assigned and also keeps track of the current class.
     * Emmits notifications to the registered listener upon changes on the BadgeClass value.
     */
  public static class Badge {
    private BadgeClass current = null;
    private Map<String, BadgeClass> classes = new HashMap<>();
    private BadgeClass unawarded;
    private BadgeEventListener listener = null;

    public Badge(String id, int order, String urlImg, String altImg, String description, String urlLink) {
      unawarded = new BadgeClass(id, order, urlImg, altImg, description, urlLink);
    }

    public String getId() {
      return unawarded.getId();
    }

      /**
       * Replaces the currently saved listener.
       * @param l the listener to replace the current listener.
       * @return the old listener object.
       */
    public BadgeEventListener setListener(BadgeEventListener l) {
      BadgeEventListener ret = this.listener;
      this.listener = l;
      return ret;
    }

      /**
       * Adds a new entry to the classes map, using ID as key and BadgeClass object for value.
       * @param badgeClass the new class to add
       */
    public void addBadgeClass(BadgeClass badgeClass) {
      classes.put(badgeClass.getId(), badgeClass);
    }

    public int getOrder() {
      return unawarded.getOrder();
    }

      /**
       * Overwrite the current class with the ID of the given class.
       * If the assigned class was not already assigned, listener is notified. This may
       * be either a ADDED or CHANGED event.
       * @param id The ID of the BadgeClass to overwrite the current.
       * @return The previously assigned class
       */
    public BadgeClass awardBadgeClass(String id) {
      BadgeClass newClass = classes.get(id);
      // Save old reference to compare with newly assigned
      BadgeClass previous = current;
      current = newClass;
      // suppress notification if class unchanged
      if(newClass != previous && listener!=null) {
        if( previous == null ) {
          listener.badgeChanged(getId(), id, ChangeType.ADDED);
        } else {
          listener.badgeChanged(getId(), id, ChangeType.CHANGED);
        }
      }
      return current;
    }

      /**
       * Return the BadgeClass of this badge
       * @return the BadgeClass that is currently assigned (may be null)
       */
    public BadgeClass getAwardedBadgeClass() {
      return current;
    }

      /**
       * Removes the currently assigned badge class and notifies listener.
       * Notification only takes place if a) listener is set b) the previous class was not null
       */
    public void revokeAwardedBadge() {
      if(listener!=null && current != null) {
        listener.badgeChanged(getId(),null, ChangeType.REMOVED);
      }
      current = null;
    }
  }

  private Map<String, Badge> badges = new HashMap<>();
  private BadgeEventListener listener = null;

    /**
     * Returns the Badge object with the given id in lowercase
     * @param id the ID of the badge
     * @return the badge object or null if not found
     */
  public Badge getBadge(String id) {
    synchronized (badges) {
      return badges.get(id.toLowerCase());
    }
  }

    /**
     * Sets the listener of all saved badges to the given parameter
     * @param l the listener to register
     * @return the previous listener (may be null)
     */
  public BadgeEventListener setListener(BadgeEventListener l) {
    synchronized (badges) {
      BadgeEventListener ret = this.listener;
      this.listener = l;
      for (Badge b : badges.values()) {
        b.setListener(l);
      }
    return ret;
    }
  }

    /**
     * Adds a new Badge and registers the listener of the BadgeFactory.
     * The id will be converted to lower-case.
     * @param b the Badge object to add
     */
  public void setBadge(Badge b) {
    synchronized (badges) {
      b.setListener(listener);
      badges.put(b.getId().toLowerCase(), b);
    }
  }

    /**
     * Removes a badge from the map
     * @param id the ID (lowercase) of the badge.
     * @return the removed object
     */
  public Badge removeBadge(String id) {
    synchronized (badges) {
      return badges.remove(id.toLowerCase());
    }
  }

    /**
     * Cummulates a tree of all badges, using the order attribute for insertion.
     * @return a Treemap with all badges.
     */
  public Badge[] getBadgeList() {
    Map<Integer, Badge> ret = new TreeMap<>();
    synchronized (badges) {
      for (Badge r : badges.values()) {
        ret.put(r.getOrder(), r);
      }
    }
    return new ArrayList<Badge>(ret.values()).toArray(new Badge[ret.size()]);
  }

    /**
     * Clears the map of all currently saved badges.
     */
  public void reset() {
    synchronized (badges) {
      badges.clear();
    }
  }

}
