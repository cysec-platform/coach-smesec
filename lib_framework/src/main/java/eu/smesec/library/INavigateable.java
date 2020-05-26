package eu.smesec.library;

import java.util.Optional;

public interface INavigateable {

    /**
     * Returns the ID of the question which is next in sequence. Different question types determine this in a different way.
     * @return a string containing the ID of the question that should come next.
     */
    Optional<String> getSuccessor();
}
