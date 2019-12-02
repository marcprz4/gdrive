package org.openjfx;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriveController {
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    public static Drive service;
    private static List<File> files;

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = DriveController.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void run(String... args) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void listFiles(String type) throws IOException {
        files = DriveFilesReader.retrieveAllFiles(type);
    }

    public static List<String> fileToString() {
        List<String> resultList = new ArrayList<>();
        for (File f : files) {
            resultList.add(f.getName());
        }
        return resultList;
    }

    public static List<File> getFiles() {
        return files;
    }

    public static boolean download(int index) throws IOException {
        boolean good = true;
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(files.get(index).getName());
        } catch (IOException e) {
            e.printStackTrace();
            good = false;
        }
        service.files().get(files.get(index).getId())
                .executeMediaAndDownloadTo(outputStream);
        return good;
    }

    public static void upload(String filename) throws IOException {
        String ext = NameCutter.cutExtension(filename);
        File fileMetadata = new File();
        fileMetadata.setName(NameCutter.cut(filename));
        java.io.File filePath = new java.io.File(filename);
        String ext2 = null;
        if (ext.equals("png") || ext.equals("jpg"))
            ext2 = "image/" + ext;
        else if (ext.equals("pdf") || ext.equals("docx"))
            ext2 = "text/" + ext;
        FileContent mediaContent = new FileContent(ext2, filePath);
        File file = DriveController.service.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
    }
}
