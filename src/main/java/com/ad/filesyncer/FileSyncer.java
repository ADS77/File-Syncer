package com.ad.filesyncer;

import com.ad.filesyncer.model.ChunkInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Adler32;

public class FileSyncer {

    private static final int CHUNK_SIZE = 1024;
    private static Logger logger = LoggerFactory.getLogger(FileSyncer.class);

    public static byte[] sync(byte[] fileA, byte[] fileB) throws IOException {
        logger.info("fileA len : " + fileA.length);
        logger.info("fileB len : " + fileB.length);

        List<ChunkInfo> fileChunks_A = generateCheckSums(fileA);
        List<ChunkInfo> fileChunks_B = generateCheckSums(fileB);
        ByteArrayOutputStream mergedFile = new ByteArrayOutputStream();
        int toalsChunks = Math.max(fileChunks_A.size(), fileChunks_B.size());
        try {
            for (int i=0 ;i<fileChunks_A.size(); i++){
                if(i >= fileChunks_B.size() || ! fileChunks_A.get(i).equals(fileChunks_B.get(i))){
                    logger.info("Chunkdiff found : " + "chunkA: " + fileChunks_A.get(i).toString() + " chunkB: " + fileChunks_B.get(i).toString());

                    int start = i* CHUNK_SIZE;
                    int end = Math.min(start+CHUNK_SIZE, fileA.length);
                    byte[] chunk = Arrays.copyOfRange(fileA, start, end);
                    mergedFile.write(chunk);
                } else {
                    int start = i*CHUNK_SIZE;
                    int end = Math.min(start+CHUNK_SIZE, fileB.length);
                    byte[] chunk = Arrays.copyOfRange(fileB,start, end);
                    mergedFile.write(chunk);
                }
            }
        }catch (IOException e) {
            throw new RuntimeException(e.getCause());
        }

        if (fileChunks_B.size() > fileChunks_A.size()) {
            for (int i = fileChunks_A.size(); i < fileChunks_B.size(); i++) {
                int start = i * CHUNK_SIZE;
                int end = Math.min(start + CHUNK_SIZE, fileB.length);
                byte[] chunk = Arrays.copyOfRange(fileB, start, end);

                mergedFile.write(chunk);
            }
        }
        mergedFile.close();

        return mergedFile.toByteArray();
    }


    public static List<ChunkInfo> generateCheckSums(byte[] file) {
        List<ChunkInfo> checkSums = new ArrayList<>();
        int totalChunks = (int) Math.ceil((double) file.length /CHUNK_SIZE);

        for (int i = 0; i< totalChunks; i++){
            int start = i*CHUNK_SIZE;
            int end = Math.min((i+1)*CHUNK_SIZE, file.length);
            byte[] chunk = Arrays.copyOfRange(file, start, end);
            long weakCheckSum  = computeWakCheakSums(chunk);
            String strongCheckSum = computeStrongCheckSum(chunk);
            checkSums.add(new ChunkInfo(weakCheckSum, strongCheckSum));
        }
        return checkSums;
    }

    private static long computeWakCheakSums(byte[] chunk) {
        Adler32 adler = new Adler32();
        adler.update(chunk);
        return adler.getValue();
    }

    private static String computeStrongCheckSum(byte[] chunk) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(chunk);

            for (byte b : digest){
                sb.append(String.format("%02x", b));
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No Such Algorithms");
        }
        return sb.toString();
    }
}
