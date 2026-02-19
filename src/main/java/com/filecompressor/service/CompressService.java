package com.filecompressor.service;

import com.filecompressor.model.CompressionStats;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

@Service
public class CompressService {

    // FIX: static final so the SAME path is used by both compress() and getCompressedFile()
    // Previously, if two instances were created, they could use different paths
    private static final String OUTPUT_DIR =
            System.getProperty("user.home") + File.separator + "FileCompressorOutput" + File.separator;

    public CompressService() {
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            System.out.println("Created output directory: " + OUTPUT_DIR + " -> " + created);
        } else {
            System.out.println("Output directory exists: " + OUTPUT_DIR);
        }
    }

    public CompressionStats compress(MultipartFile file) {
        long startTime = System.currentTimeMillis();

        // Sanitize original filename — strip directory path if present
        String originalName = new File(file.getOriginalFilename()).getName();
        // FIX: replace spaces with underscores so file names work in URLs
        String safeOriginalName = originalName.replace(" ", "_");
        String zipFileName = safeOriginalName + ".zip";
        String outputPath = OUTPUT_DIR + zipFileName;

        System.out.println("Compressing: " + safeOriginalName);
        System.out.println("Output path: " + outputPath);

        try {
            byte[] fileBytes = file.getBytes();
            long originalSize = fileBytes.length;

            try (FileOutputStream fos = new FileOutputStream(outputPath);
                 ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))) {

                zos.setLevel(Deflater.BEST_COMPRESSION);
                ZipEntry entry = new ZipEntry(safeOriginalName);
                zos.putNextEntry(entry);
                zos.write(fileBytes);
                zos.closeEntry();
            }

            // Verify the file was actually written
            File outFile = new File(outputPath);
            if (!outFile.exists()) {
                throw new IOException("Output file was not created at: " + outputPath);
            }

            long compressedSize = outFile.length();
            long timeTaken = System.currentTimeMillis() - startTime;

            System.out.println("Compressed successfully: " + zipFileName + " (" + compressedSize + " bytes)");

            return new CompressionStats(safeOriginalName, originalSize, compressedSize,
                    timeTaken, zipFileName, true, "File compressed successfully!");

        } catch (IOException e) {
            System.err.println("Compression error: " + e.getMessage());
            e.printStackTrace();
            return new CompressionStats(originalName, 0, 0, 0, null, false,
                    "Compression failed: " + e.getMessage());
        }
    }

    public byte[] getCompressedFile(String fileName) throws IOException {
        String safeName = new File(fileName).getName();
        Path path = Paths.get(OUTPUT_DIR + safeName);
        System.out.println("Looking for file at: " + path.toAbsolutePath());
        if (!Files.exists(path)) {
            throw new IOException("File not found at path: " + path.toAbsolutePath().toString());
        }
        return Files.readAllBytes(path);
    }
}
