package model;

public class SecurityRule {
    private int ruleId;
    private String ruleName;
    private String metricType;
    private String operatorType;
    private String thresholdValue;
    private String severity;
    private boolean active;

    public SecurityRule(int ruleId, String ruleName, String metricType, String operatorType,
            String thresholdValue, String severity, boolean active) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.metricType = metricType;
        this.operatorType = operatorType;
        this.thresholdValue = thresholdValue;
        this.severity = severity;
        this.active = active;
    }

    public int getRuleId() {
        return ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getMetricType() {
        return metricType;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public String getThresholdValue() {
        return thresholdValue;
    }

    public String getSeverity() {
        return severity;
    }

    public boolean isActive() {
        return active;
    }
}
