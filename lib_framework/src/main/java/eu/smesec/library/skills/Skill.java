package eu.smesec.library.skills;

/**
 * Serves as the base class for all aspects of current und yet to come skills
 */
public abstract class Skill {

    protected int value;
    protected int maxValue;

    Skill(int maxValue) {
        this.value = 0;
        this.maxValue = maxValue;
    }

    public int get() {
        return Math.max(0, Math.min(this.maxValue, this.value));
    }
    public void setValue(int value) { this.value = value; }

    public void add(int value) {
        this.value += value;
    }

    public int getMaxValue() { return maxValue; }
}
