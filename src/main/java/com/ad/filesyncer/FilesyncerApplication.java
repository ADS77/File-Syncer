package com.ad.filesyncer;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@SpringBootApplication
public class FilesyncerApplication {

    public static void main(String[] args) throws IOException {

        Path fileAPath = Paths.get("src/main/resources/asset/files/fileA.txt");
        Path fileBPath = Paths.get("src/main/resources/asset/files/fileB.txt");

        byte[] fileA  = Files.readAllBytes(fileAPath);
        byte[] fileB  = Files.readAllBytes(fileBPath);

        byte[] syncedFile = FileSyncer.sync(fileA, fileB);
        System.out.println("Synced file: " + new String(syncedFile));
    }

}
