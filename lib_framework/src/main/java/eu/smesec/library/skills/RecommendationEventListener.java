package eu.smesec.library.skills;

public interface RecommendationEventListener {

  void recommendationChanged(String recommendationId, ChangeType change);

}
