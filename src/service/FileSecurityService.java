package service;

import dao.FilePermissionsDAO;
import dao.EventDAO;

public class FileSecurityService {

    FilePermissionsDAO dao = new FilePermissionsDAO();
    EventDAO eventDAO = new EventDAO();

    public void accessFile(int sessionId, int userId, String fileName) {

        boolean allowed = dao.isallowed(userId, fileName, "read");

        if (allowed) {
            System.out.println("File Opened");

            eventDAO.logEventAndGetId(
                sessionId,
                "FILE_ACCESS_SUCCESS",
                1,              // low risk
                "LOW"
            );

        } else {
            System.out.println("Access Denied");

            eventDAO.logEventAndGetId(
                sessionId,
                "UNAUTHORIZED_ACCESS",
                8,              // high risk
                "HIGH"
            );
        }
    }
}

