package com.filecompressor.service;

import com.filecompressor.model.CompressionStats;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

@Service
public class DecompressService {

    private static final String OUTPUT_DIR =
            System.getProperty("user.home") + File.separator + "FileCompressorOutput" + File.separator;

    private static final int BUFFER_SIZE = 4096;

    public DecompressService() {
        new File(OUTPUT_DIR).mkdirs();
    }

    public CompressionStats decompress(MultipartFile file) {
        long startTime = System.currentTimeMillis();
        String zipName = new File(file.getOriginalFilename()).getName();

        System.out.println("Decompressing: " + zipName);

        if (!zipName.toLowerCase().endsWith(".zip")) {
            return new CompressionStats(zipName, 0, 0, 0, null, false,
                    "Only .zip files can be decompressed.");
        }

        String extractFolder = OUTPUT_DIR + "extracted_" + System.currentTimeMillis() + File.separator;
        new File(extractFolder).mkdirs();

        try {
            long originalZipSize = file.getSize();
            long totalExtracted = 0;
            int fileCount = 0;

            try (ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(file.getInputStream()))) {

                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    File destFile = new File(extractFolder + entry.getName());

                    // Zip slip protection
                    String destPath = destFile.getCanonicalPath();
                    String basePath = new File(extractFolder).getCanonicalPath();
                    if (!destPath.startsWith(basePath)) {
                        throw new IOException("Zip slip blocked: " + entry.getName());
                    }

                    if (entry.isDirectory()) {
                        destFile.mkdirs();
                    } else {
                        destFile.getParentFile().mkdirs();
                        byte[] buffer = new byte[BUFFER_SIZE];
                        try (FileOutputStream fos = new FileOutputStream(destFile);
                             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                bos.write(buffer, 0, len);
                                totalExtracted += len;
                            }
                        }
                        fileCount++;
                    }
                    zis.closeEntry();
                }
            }

            if (fileCount == 0) {
                return new CompressionStats(zipName, originalZipSize, 0, 0, null, false,
                        "The zip file appears to be empty.");
            }

            String outputZipName = "decompressed_" + zipName.replace(" ", "_") + "_" + System.currentTimeMillis() + ".zip";
            String outputZipPath = OUTPUT_DIR + outputZipName;
            reZipExtractedFiles(extractFolder, outputZipPath);

            long timeTaken = System.currentTimeMillis() - startTime;
            System.out.println("Decompressed: " + fileCount + " files in " + timeTaken + "ms -> " + outputZipName);

            return new CompressionStats(zipName, originalZipSize, totalExtracted,
                    timeTaken, outputZipName, true,
                    "Extracted " + fileCount + " file(s) successfully!");

        } catch (IOException e) {
            System.err.println("Decompression error: " + e.getMessage());
            e.printStackTrace();
            return new CompressionStats(zipName, 0, 0, 0, null, false,
                    "Decompression failed: " + e.getMessage());
        }
    }

    private void reZipExtractedFiles(String folderPath, String outputZipPath) throws IOException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            throw new IOException("Nothing was extracted.");
        }
        try (FileOutputStream fos = new FileOutputStream(outputZipPath);
             ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))) {
            for (File f : files) {
                addToZip(f, f.getName(), zos);
            }
        }
    }

    private void addToZip(File file, String entryName, ZipOutputStream zos) throws IOException {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addToZip(child, entryName + "/" + child.getName(), zos);
                }
            }
        } else {
            zos.putNextEntry(new ZipEntry(entryName));
            Files.copy(file.toPath(), zos);
            zos.closeEntry();
        }
    }

    public byte[] getExtractedFile(String fileName) throws IOException {
        String safeName = new File(fileName).getName();
        Path path = Paths.get(OUTPUT_DIR + safeName);
        System.out.println("Looking for extracted file at: " + path.toAbsolutePath());
        if (!Files.exists(path)) {
            throw new IOException("File not found at path: " + path.toAbsolutePath().toString());
        }
        return Files.readAllBytes(path);
    }
}
