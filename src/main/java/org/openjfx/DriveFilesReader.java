package org.openjfx;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DriveFilesReader {
    public static List<File> retrieveAllFiles(String type) throws IOException {
        List<File> result = new ArrayList<File>();
        Drive.Files.List request;
        switch (type) {
            case "Folders": {
                request = DriveController.service.files().list().setQ("mimeType = 'application/vnd.google-apps.folder'");
                break;
            }
            case "Files": {
                request = DriveController.service.files().list().setQ("mimeType != 'application/vnd.google-apps.folder' and mimeType != 'application/vnd.google-apps.document'");
                break;
            }
            case "Photos": {
                request = DriveController.service.files().list().setQ("mimeType != 'application/vnd.google-apps.photo' and mimeType != 'application/vnd.google-apps.document'");
                break;
            }
            default: {
                request = DriveController.service.files().list();
            }
        }
        do {
            try {
                FileList files = request.execute();
                result.addAll(files.getFiles());
                request.setPageToken(files.getNextPageToken());
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0 && result.size() < 100);

        return result;
    }
}
