package eu.smesec.library;

import eu.smesec.bridge.FQCN;
import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.execptions.CacheException;
import eu.smesec.bridge.Library;
import eu.smesec.bridge.generated.Answer;
import eu.smesec.bridge.generated.Metadata;
import eu.smesec.bridge.generated.Mvalue;
import eu.smesec.bridge.md.MetadataUtils;
import eu.smesec.library.parser.CySeCExecutorContextFactory;
import eu.smesec.library.parser.ExecutorContext;
import eu.smesec.library.skills.BadgeEventListener;
import eu.smesec.library.skills.BadgeFactory;
import eu.smesec.library.skills.ChangeType;
import eu.smesec.library.skills.RecommendationEventListener;
import eu.smesec.library.skills.RecommendationFactory;
import eu.smesec.library.skills.ScoreFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static eu.smesec.bridge.md.MetadataUtils.MV_ENDURANCE;
import static eu.smesec.bridge.md.MetadataUtils.MV_ENDURANCE_STATE;
import static eu.smesec.bridge.md.MetadataUtils.MV_MICRO_SCORE;

/**
 * Manages calls to the ILibCal interface to persist data in the platforms intenal cache.
 */
public class PersistanceManager implements BadgeEventListener, RecommendationEventListener {
    private ILibCal cal;
    private Logger logger;
    private AbstractLib library;
    private CySeCExecutorContextFactory.CySeCExecutorContext context;
    // Necessary because badgeChanged/recommendationChanged do not have access to FQCN from platform
    private final String ROOT_COACH = "lib-company";

    /**
     * Construct new PersistenceManager object and automatically register new listeners at CysecExeCutorContext
     * @param cal the cal interface
     * @param logger the logger of the platform
     * @param lib the library that owns this manager
     */
    public PersistanceManager(ILibCal cal, Logger logger, AbstractLib lib) {
        this.cal = cal;
        this.logger = logger;
        library = lib;
        context = CySeCExecutorContextFactory.getExecutorContext(library.getQuestionnaire().getId());
        context.setBadgeListener(this);
        context.setRecommendationListener(this);
    }

    /**
     * Resolves the fully-qualified-coach-name to a given length.
     *
     * <p>Assume a coach as follows : lib-company.lib-backup.lib-subcoach.A</p></p>
     * <p>E.g: resolveFQCN(1) = lib-company.lib-backup.lib-subcoach.A
     * <p>E.g: resolveFQCN(2) = lib-company.lib-backup
     * @param trimLength Determines how many levels should be taken from the left
     * @return A {@link String} that contains the fqcn
     */
    public String resolveFQCN(int trimLength) {
        String fqcn = fqcnLoop();
        String[] parts = fqcn.split("\\.");
        String customFqcn = "";
//        if (trimLength == 0) return parts[0];
        //String s = Arrays.stream(parts).limit(trimLength).reduce("", (s1, s2) -> s1 + "." + s2);
        for(int i = 0; i < trimLength; i++) {
            customFqcn += parts[i];
            if(i != (trimLength - 1)) customFqcn += ".";
        }
        return customFqcn;
    }

    /**
     * Resolves the fully-qualified-coach-name
     *
     * <p>Loops through the executor parent hierachy and concatenates the FQCN.</p>
     * @return A {@link String} that
     */
    private String fqcnLoop() {
        // Set context ID in case coach is root level
        String fqcn = context.getContextId();
        ExecutorContext parent = context.getParent();
        if(context.getParent() != null) {
            // coach is on top level
            while (parent != null) {
                fqcn = parent.getContextId() + "." + fqcn;
                parent = parent.getParent();
            }
        }
        return fqcn;
    }

    /**
     * Resolves the complete fqcn
     * @return A string with the complete fqcn
     */
    public String resolveFQCN() {
        return fqcnLoop();
    }

    /**
     * Helper method to create metadata. Reduce code in init() method
     *
     * @param coachId the id of the coach where the metadata should be saved
     * @param key The key for the metadata object
     * @param name The name attribute of the mvalue
     * @param description The description of the mvalue
     * @param order The order of the mvalue
     * @param general The general attribute of the mvalue
     * @param link The link attribute of the mvalue
     */
    /*public void createMetadata(String coachId, String key, String name, String description, String order, String general, String link) {
        logger.info(String.format("Creating metadata: %s", key));
        try {
            cal.setMetadata(FQCN.fromString(coachId), MetadataUtils.createMetadata(key,
                    Arrays.asList(MetadataUtils.createMvalueStr(MetadataUtils.MV_DESCRIPTION, description),
                            MetadataUtils.createMvalueStr(MetadataUtils.MV_NAME, name),
                            MetadataUtils.createMvalueStr(MetadataUtils.MV_ORDER, String.valueOf(order)),
                            MetadataUtils.createMvalueStr(MetadataUtils.MV_GENERAL, general),
                            MetadataUtils.createMvalueStr(MetadataUtils.MV_LINK, link)
                    ))
            );
        } catch (CacheException e) {
            logger.severe("Error creating metadata " + e.getMessage());
        }
    }*/

    /**
     * Overloaded method to use Metadata instead of individual parameters to create metadata.
     *
     * @param fqcn the fqcn of the coach where the metadata should be saved
     * @param metadata The metadata object to save/update
     */
    public void createMetadata(FQCN fqcn, Metadata metadata) {
        logger.info(String.format("Creating metadata: %s on %s", metadata.getKey(), fqcn.toString()));
        try {
            cal.setMetadata(fqcn, metadata);
        } catch (CacheException e) {
            logger.severe("Error creating metadata " + e.getMessage());
        }
    }

    /**
     * Removes the metadata with the given key from the company
     *
     * @param fqcn the fqcn of the coach where the metadata should be saved
     * @param key the fqdn of the metadata object
     */
    public void deleteMetadata(FQCN fqcn, String key) {
        logger.info(String.format("Removing metadata: %s from %s", key, fqcn.toString()));
        try {
            Metadata metadata = cal.getMetadata(fqcn, key);
            if (metadata != null) {
                logger.info(String.format("Found metadata: %s", metadata.getKey()));
                cal.deleteMetadata(fqcn, metadata.getKey());
            }
        } catch (CacheException e) {
            logger.severe("Error deleting metadata " + e.getMessage());
        }
    }

    /**
     * Helper method to avoid boiler plate code for exception handling.
     * Returns an optional containing the answer for a given question id.
     * @param questionId The ID of the question
     * @param fqcn The fqcn of coach
     * @return an Optional with the Answer object
     */
    public Optional<Answer> getAnswer(FQCN fqcn, String questionId) {
        logger.info(String.format("Looking for answer: %s in %s", questionId, fqcn.toString()));
        Answer answer;
        try {
            // always retrieve answers from your own answer file
            answer = cal.getAnswer(fqcn.toString(), questionId);
        } catch (CacheException e) {
            logger.severe("Error accessing answer to question" + e.getMessage());
            return Optional.empty();
        }
        return Optional.ofNullable(answer);
    }


    @Override
    public void badgeChanged(String badgeId, String classId, ChangeType change) {
        if(change.equals(ChangeType.ADDED)) {
            BadgeFactory.Badge baseBadge = context.getBadge(badgeId);
            baseBadge.setListener(this);
            BadgeFactory.BadgeClass badgeClass = baseBadge.getAwardedBadgeClass();
            createMetadata(FQCN.fromString(ROOT_COACH), buildBadge(badgeId, badgeClass));
        } else if(change.equals(ChangeType.CHANGED)) {
            // delete old and create new one
            deleteMetadata(FQCN.fromString(ROOT_COACH), MetadataUtils.MD_BADGES+ "." + badgeId);
            BadgeFactory.Badge baseBadge = context.getBadge(badgeId);
            BadgeFactory.BadgeClass badge = baseBadge.getAwardedBadgeClass();
            Metadata metadata = buildBadge(badgeId, badge);
            createMetadata(FQCN.fromString(ROOT_COACH), metadata);
        }  else {
            deleteMetadata(FQCN.fromString(ROOT_COACH), MetadataUtils.MD_BADGES + "." + badgeId);
        }
    }

    /**
     * Helper method for saving Skills of coach
     */
    public void saveSkills(FQCN fqcn) {
        // persist Skills to XML
        List<Mvalue> scoreMvalues = new ArrayList<>();
        for(ScoreFactory.Score score : context.getScores()) {
            String value = String.valueOf(score.getValue());
            scoreMvalues.add(MetadataUtils.createMvalueStr(score.getId(), value));
            logger.info(String.format("Saving Skill ([%s]: %s to Metadata", score.getId(), value));
        }
        // don't save uu score in skills, belongs to rating
        scoreMvalues.remove(context.getScore(library.prop.getProperty("library.skills.uu")));
        scoreMvalues.remove(context.getScore(library.prop.getProperty("library.skills.uuMax")));
        logger.info(String.format("Saving Skill state [Endurance]: %s)to file", library.endurance.toString()));
        logger.info(String.format("Saving Skill [Endurance]: %d)to file", library.endurance.get()));
        // manually add endurance, as it is tracked separately
        scoreMvalues.add(MetadataUtils.createMvalueStr(MV_ENDURANCE, String.valueOf(library.endurance.get())));
        scoreMvalues.add(MetadataUtils.createMvalueStr(MV_ENDURANCE_STATE, library.endurance.toString()));

        createMetadata(fqcn, MetadataUtils.createMetadata(MetadataUtils.MD_SKILLS, scoreMvalues));
    }

    /**
     * Helper method for saving rating of coach
     */
    public void saveRating(FQCN fqcn) {
        // Score/micro_score equals the UU score
        String scoreValue = String.valueOf(context.getScore(library.prop.getProperty("library.skills.uu")).getValue());
        String scoreValueMax = String.valueOf(context.getScore(library.prop.getProperty("library.skills.uuMax")).getValue());
        // Update Score and Grade
        try {
            // always save rating (score, grade) in own answer file
            cal.setMetadata(fqcn, MetadataUtils.createMetadata(MetadataUtils.MD_RATING, Arrays.asList(
                    MetadataUtils.createMvalueStr(MV_MICRO_SCORE, String.valueOf(scoreValue)),
                    MetadataUtils.createMvalueStr(MetadataUtils.MV_MICRO_GRADE, library.getGrade())
            )));
        } catch (CacheException e) {
            logger.log(Level.SEVERE, "Error saving rating", e);
        }
        logger.info(String.format("Saving Rating ([Score]: %s, [Grade]: %s)to file", scoreValue, library.getGrade()));
    }

    @Override
    public void recommendationChanged(String recommendationId, ChangeType change) {
        if(change.equals(ChangeType.ADDED)) {
            RecommendationFactory.Recommendation recommendation = context.getRecommendation(recommendationId);
            Metadata metadata = buildRecommendation(recommendation);
            createMetadata(FQCN.fromString(ROOT_COACH), metadata);
        } else if(change.equals(ChangeType.CHANGED)) {
            // delete old and create new one
            deleteMetadata(FQCN.fromString(ROOT_COACH), MetadataUtils.MD_RECOMMENDED + "." + recommendationId);
            RecommendationFactory.Recommendation recommendation = context.getRecommendation(recommendationId);
            Metadata metadata = buildRecommendation(recommendation);
            createMetadata(FQCN.fromString(ROOT_COACH), metadata);
        }  else {
            deleteMetadata(FQCN.fromString(ROOT_COACH), MetadataUtils.MD_RECOMMENDED + "." + recommendationId);
        }
    }

    /**
     * Helper method to create metadata from a recommendation
     * @param recommendation the recommendation object to transform into Metadata.
     * @return the serialized recommendation metadata object
     */
    private Metadata buildRecommendation(RecommendationFactory.Recommendation recommendation) {
        return MetadataBuilder.newInstance(library)
                .setId(recommendation.getId())
                .setName(recommendation.getTitle())
                .setGeneral("true")
                .setOrder(String.valueOf(recommendation.getOrder()))
                .setDescription(recommendation.getDescription())
                .setLink(recommendation.getLink())
                .setImage(recommendation.getImgUrl())
                .buildRecommendation();
    }

    /**
     * Helper method to create metadata from a badge
     * @param badgeId the id of the assigned badge
     * @param badgeClass the badgeClass object to transform into Metadata.
     * @return the serialized badge metadata object
     */
    private Metadata buildBadge(String badgeId, BadgeFactory.BadgeClass badgeClass) {
        return MetadataBuilder.newInstance(library)
                .setId(badgeId)
                .setOrder(String.valueOf(badgeClass.getOrder()))
                .setDescription(badgeClass.getDescription())
                .setClazz(badgeClass.getId())
                .setLink(badgeClass.getLink())
                .setImage(badgeClass.getImgUrl())
                .buildBadge();
    }
}
