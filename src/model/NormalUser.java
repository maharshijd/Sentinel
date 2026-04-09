package model;

public class NormalUser extends User {

    public NormalUser(int userId, String email, int roleId, int deptId, String roleName) {
        super(userId, email, roleId, deptId, roleName);
    }

    @Override
    public void showRoleMessage() {
        System.out.println("Standard user access");
    }
}