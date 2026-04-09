package model;

public class Admin extends User {

    public Admin(int userId, String email, int roleId, int deptId, String roleName) {
        super(userId, email, roleId, deptId, roleName);
    }

    @Override
    public void showRoleMessage() {
        System.out.println("Admin privileges granted");
    }
}