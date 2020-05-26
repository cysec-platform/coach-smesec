package eu.smesec.library;

import eu.smesec.library.questions.LibQuestion;
import eu.smesec.library.questions.Modifier;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * Aims to unify the behaviour of the observing Objects within the Framework
 */
public interface Observer {
    /**
     * Pass the information from the observed object to the consumer function.
     * @param mod The modifier indicating (De)Selection/Update
     * @param data The changed question option
     */
    void update(Modifier mod, String data);

    /**
     * Register an action to be invoked on the observed question
     * @param action The action to perform upon update()
     * @param lib The lib to handle
     * @param questions The observed object
     */
    @Deprecated
    void init(BiConsumer<Modifier, String> action, AbstractLib lib, Collection<LibQuestion> questions);

    /**
     * Init method for Version two of the framework. This method is intended to be used for questions created
     * using reflection. It takes no lib parameter as this is passed via constructor of the question
     */
    void init(BiConsumer<Modifier, String> action, Collection<String> questions);
}
