package eu.smesec.library.skills;

public interface BadgeEventListener {

  void badgeChanged(String badgeId, String classId, ChangeType change);

}
