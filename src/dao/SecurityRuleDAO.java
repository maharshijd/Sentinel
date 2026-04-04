package dao;

import config.DBconnection;
import model.SecurityRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SecurityRuleDAO {

    public SecurityRuleDAO() {
        ensureRuleTableExists();
    }

    private void ensureRuleTableExists() {
        String query = "CREATE TABLE IF NOT EXISTS security_rules ("
                + "rule_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "rule_name VARCHAR(120) NOT NULL, "
                + "metric_type VARCHAR(40) NOT NULL, "
                + "operator_type VARCHAR(20) NOT NULL, "
                + "threshold_value VARCHAR(100) NOT NULL, "
                + "severity VARCHAR(20) NOT NULL, "
                + "is_active BOOLEAN DEFAULT TRUE, "
                + "created_by INT NOT NULL, "
                + "created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "CONSTRAINT fk_rule_creator FOREIGN KEY (created_by) REFERENCES user(user_id)"
                + ")";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Warning: Could not ensure security_rules table exists.");
            e.printStackTrace();
        }
    }

    public boolean createRule(String ruleName, String metricType, String operatorType, String thresholdValue,
            String severity, int createdBy) {
        String query = "INSERT INTO security_rules "
                + "(rule_name, metric_type, operator_type, threshold_value, severity, is_active, created_by) "
                + "VALUES (?, ?, ?, ?, ?, TRUE, ?)";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, ruleName);
            stmt.setString(2, metricType.toUpperCase());
            stmt.setString(3, operatorType.toUpperCase());
            stmt.setString(4, thresholdValue);
            stmt.setString(5, severity.toUpperCase());
            stmt.setInt(6, createdBy);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating security rule.");
            e.printStackTrace();
            return false;
        }
    }

    public void viewAllRules() {
        String query = "SELECT rule_id, rule_name, metric_type, operator_type, threshold_value, severity, is_active "
                + "FROM security_rules ORDER BY rule_id";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n====================================================================================================");
            System.out.println("                                     SECURITY RULES                                                 ");
            System.out.println("====================================================================================================");
            System.out.printf("%-8s | %-20s | %-12s | %-10s | %-15s | %-10s | %-7s%n",
                    "RULE ID", "RULE NAME", "METRIC", "OPERATOR", "THRESHOLD", "SEVERITY", "ACTIVE");
            System.out.println("----------------------------------------------------------------------------------------------------");

            boolean hasRules = false;
            while (rs.next()) {
                hasRules = true;
                System.out.printf("%-8d | %-20s | %-12s | %-10s | %-15s | %-10s | %-7s%n",
                        rs.getInt("rule_id"),
                        rs.getString("rule_name"),
                        rs.getString("metric_type"),
                        rs.getString("operator_type"),
                        rs.getString("threshold_value"),
                        rs.getString("severity"),
                        rs.getBoolean("is_active") ? "YES" : "NO");
            }

            if (hasRules == false) {
                System.out.println("No security rules configured.");
            }
            System.out.println("====================================================================================================\n");

        } catch (SQLException e) {
            System.out.println("Error fetching security rules.");
            e.printStackTrace();
        }
    }

    public boolean setRuleStatus(int ruleId, boolean active) {
        String query = "UPDATE security_rules SET is_active = ? WHERE rule_id = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, active);
            stmt.setInt(2, ruleId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating rule status.");
            e.printStackTrace();
            return false;
        }
    }

    public List<SecurityRule> getActiveRules() {
        List<SecurityRule> rules = new ArrayList<>();
        String query = "SELECT rule_id, rule_name, metric_type, operator_type, threshold_value, severity, is_active "
                + "FROM security_rules WHERE is_active = TRUE";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rules.add(new SecurityRule(
                        rs.getInt("rule_id"),
                        rs.getString("rule_name"),
                        rs.getString("metric_type"),
                        rs.getString("operator_type"),
                        rs.getString("threshold_value"),
                        rs.getString("severity"),
                        rs.getBoolean("is_active")));
            }
        } catch (SQLException e) {
            System.out.println("Error loading active security rules.");
            e.printStackTrace();
        }

        return rules;
    }
}
