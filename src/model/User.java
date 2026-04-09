package model;

public class User implements UserRole {

    protected int userId;
    protected String email;
    protected int roleId;
    protected int deptId;
    protected String roleName;

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

    @Override
    public void showRoleMessage() {
        System.out.println("User logged in");
    }
}