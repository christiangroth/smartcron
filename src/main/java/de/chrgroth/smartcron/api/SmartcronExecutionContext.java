package de.chrgroth.smartcron.api;

/**
 * Context per {@link Smartcron} execution allowing to set the execution mode, block this execution in history (if trackng is enabled, @see
 * {@link Smartcron#executionHistory()}) or mark execution as failed.
 *
 * @author Christian Groth
 */
public class SmartcronExecutionContext {

    /**
     * Default execution mode.
     */
    public static final String MODE_DEFAULT = null;

    private String mode = MODE_DEFAULT;
    private String error;
    private boolean ignoreInHistory = false;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isIgnoreInHistory() {
        return ignoreInHistory;
    }

    public void setIgnoreInHistory(boolean ignoreInHistory) {
        this.ignoreInHistory = ignoreInHistory;
    }
}
