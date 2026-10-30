package app.model;

public class ProgressStep {

    private final String trigger;
    private final String label;

    public ProgressStep(String trigger, String label) {
        this.trigger = trigger;
        this.label   = label;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getLabel() {
        return label;
    }
}