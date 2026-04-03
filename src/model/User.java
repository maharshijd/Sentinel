package model;

public class User {
    private int userId;
    private String email;
    private int roleId;
    private int deptId;
    private String roleName;

    public User(int userId, String email, int roleId, int deptId, String roleName) {
        this.userId = userId;
        this.email = email;
        this.roleId = roleId;
        this.deptId = deptId;
        this.roleName = roleName;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public int getRoleId() {
        return roleId;
    }

    public int getDeptId() {
        return deptId;
    }

    public String getRoleName() {
        return roleName;
    }
}