package service;

import dao.FilePermissionsDAO;
import dao.EventDAO;

public class FileSecurityService {

    FilePermissionsDAO dao = new FilePermissionsDAO();

    EventDAO eventDAO = new EventDAO();

    public void accessFile(

            int sessionId,

            int userId,

            String fileName) {

        if (sessionId <= 0 || userId <= 0) {
            System.out.println("Invalid session. Please login again.");
            return;
        }

        boolean allowed = dao.isallowed(
                userId,
                fileName,
                "read");

        if (allowed) {

            System.out.println("File Opened");

            int eventId = eventDAO.logEventAndGetId(

                    sessionId,

                    "FILE_ACCESS_SUCCESS",

                    1,

                    "LOW");

            if (eventId == -1) {
                System.out.println("Event logging failed.");
            }

        }

        else {

            System.out.println("Access Denied");

            int eventId = eventDAO.logEventAndGetId(

                    sessionId,

                    "UNAUTHORIZED_ACCESS",

                    8,

                    "HIGH");

            if (eventId == -1) {
                System.out.println("Event logging failed.");
            }

        }
    }
}