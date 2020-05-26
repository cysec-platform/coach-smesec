package eu.smesec.library;

import eu.smesec.bridge.generated.Metadata;

/**
 * Interface for the helper class to provide concise access to create (Badge and Recommendation ) Metadata.
 * May be obsolete in the future because MetadataUtils offers annotations for Badges
 * which define how to serialize objects to metadata.
 * Checkout {@link eu.smesec.library.parser.CommandAddBadge#execute CommandAddBadge} for
 * a list of supported fields.
 * @see eu.smesec.bridge.utils.MetadataUtils
 */
public interface IMetadataBuilder {

    /**
     * Create a metadata object with the usual mvalues for badges
     * @return A metadta object containing a badge
     */
    Metadata buildBadge();

    /**
     * Create a metadata object with the usual mvalues for recommendations
     * @return A metadata object containing a recommendation
     */
    Metadata buildRecommendation();
}
