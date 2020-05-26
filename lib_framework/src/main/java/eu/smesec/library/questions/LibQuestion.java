package eu.smesec.library.questions;

import eu.smesec.bridge.generated.Metadata;
import eu.smesec.bridge.generated.Mvalue;
import eu.smesec.bridge.generated.Option;
import eu.smesec.bridge.generated.Question;
import eu.smesec.bridge.md.MetadataUtils;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.INavigateable;
import eu.smesec.library.Observer;
import eu.smesec.library.Score;
import eu.smesec.library.parser.CySeCExecutorContextFactory;
import eu.smesec.library.parser.CySeCLineAtom;
import eu.smesec.library.parser.ExecutorContext;
import eu.smesec.library.parser.ExecutorException;
import eu.smesec.library.parser.ParserException;
import eu.smesec.library.parser.ParserLine;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class LibQuestion implements Observer, INavigateable {
    protected String nextQidDefault;
    protected String nextQid;
    protected boolean hide;
    private String id;
    private Set<Observer> observers = new HashSet<>(2);
    private Map<String, MetadataUtils.SimpleMvalue> mvalueMap = new HashMap<>();
    private BiConsumer<Modifier, String> action;
    private AbstractLib lib;

    /**
     * Version 2 constructor for reflection loading
     */
    public LibQuestion(Question question, AbstractLib lib) {
        this.id = question.getId();
        this.hide = question.isHidden();
        this.lib = lib;

        for (Metadata metadata : question.getMetadata()) {
            for (Mvalue mvalue : metadata.getMvalue()) {
                String fqdn = metadata.getKey() + "." + mvalue.getKey();
                MetadataUtils.SimpleMvalue val = MetadataUtils.parseMvalue(mvalue);
                lib.getLogger().fine(String.format("Adding metadata %s to %s", fqdn, question.getId()));
                mvalueMap.put(fqdn, val);
            }
        }
        Optional<MetadataUtils.SimpleMvalue> nextQidMvalue = Optional.ofNullable(mvalueMap.get("nextQid.default"));

        if (nextQidMvalue.isPresent()) {
            this.nextQidDefault = nextQidMvalue.get().getValue();
            this.nextQid = nextQidMvalue.get().getValue();
        }
    }

    LibQuestion(String id, String nextQid) {
        this(id, nextQid, false);
    }

    LibQuestion(String id, String nextQid, boolean hide) {
        this.id = id;
        this.nextQidDefault = nextQid;
        this.nextQid = nextQid;
        this.hide = hide;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public String getId() {
        return id;
    }

    public String getNextQid() {
        return this.nextQid;
    }

    public String getDefaultNextQid() {
        return this.nextQidDefault;
    }

    public MetadataUtils.SimpleMvalue searchMvalue(String fqdn) {
        Objects.requireNonNull(fqdn, "FQDN for mvalue must not be null");
        return mvalueMap.get(fqdn);
    }

    public void register(Observer recommendation) {
        observers.add(recommendation);
    }

    public void unregister(Observer recommendation) {
        observers.remove(recommendation);
    }

    /**
     * Called to notify observers about change. It also triggers the internal updateState() method.
     *
     * @param data the selected option
     * @param qid  the selected question
     */
    public void notify(String data, String qid) {
        Modifier mod = updateState(data, qid);
        for (Observer observer : observers) {
            observer.update(mod, data);
        }
    }



    public Optional<MetadataUtils.SimpleMvalue> getLogic(String logicProperty) {
        MetadataUtils.SimpleMvalue logic = mvalueMap.get(logicProperty);
        return Optional.ofNullable(logic);
    }

    /**
     * Hook method used to modify the questions state, that's to say, mark options as selected/deselected
     *
     * @param data the selected option
     * @param qid  the selected question
     * @return
     */
    protected abstract Modifier updateState(String data, String qid);

    /**
     * Implemented by sub classes as different types evaluate answers differently.
     *
     * @return true if there is an answer, false otherwise
     */
    public abstract boolean isAnswered();

    @Override
    public void update(Modifier mod, String data) {
        this.action.accept(mod, data);
    }

    @Override
    public void init(BiConsumer<Modifier, String> action, AbstractLib lib, Collection<LibQuestion> questions) {
        this.lib = lib;
        // Call init method that doesnt take lib parameter
        init(action, questions.stream().map(question -> question.getId()).collect(Collectors.toList()));
    }

    @Override
    public void init(BiConsumer<Modifier, String> action, Collection<String> questionIds) {
        this.action = action;
        for (String id : questionIds) {
//            LibQuestion question = lib.getQuestion(id);
//            Objects.requireNonNull(question, "No question with given ID found");
//            question.register(this);
        }
    }
}
