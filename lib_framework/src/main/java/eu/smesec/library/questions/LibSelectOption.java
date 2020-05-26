package eu.smesec.library.questions;

public class LibSelectOption {
    private String id;
    private int score;
    private String nextQid;
    private boolean selected;

    public LibSelectOption(String id, int score) {
        this(id, score, null);
    }

    public LibSelectOption(String id, int score, String nextQid) {
        this.id = id;
        this.score = score;
        this.nextQid = nextQid;
        this.selected = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getNextQid() {
        return nextQid;
    }

    public void setNextQid(String nextQid) {
        this.nextQid = nextQid;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
