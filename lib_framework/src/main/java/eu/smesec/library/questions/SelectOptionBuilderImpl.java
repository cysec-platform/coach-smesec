package eu.smesec.library.questions;

public final class SelectOptionBuilderImpl {
    private String id;
    private int score;
    private String nextQid;

    // hide default constructor
    private SelectOptionBuilderImpl() {}

    public static SelectOptionBuilderImpl newInstance() {
        return new SelectOptionBuilderImpl();
    }

    public SelectOptionBuilderImpl setId(String id) {
        this.id = id;
        return this;
    }

    public SelectOptionBuilderImpl setNext(String next) {
       this.nextQid = next;
       return this;
    }

    public SelectOptionBuilderImpl setScore(int score) {
        this.score = score;
        return this;
    }

    public LibSelectOption build() {
        return new LibSelectOption(id, score, nextQid);
    }
}