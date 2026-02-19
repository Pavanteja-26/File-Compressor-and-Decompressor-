package com.filecompressor.controller;

import com.filecompressor.model.CompressionStats;
import com.filecompressor.service.CompressService;
import com.filecompressor.service.DecompressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class FileController {

    @Autowired
    private CompressService compressService;

    @Autowired
    private DecompressService decompressService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping(value = "/compress", consumes = "multipart/form-data")
    public String compressFile(@RequestParam("file") MultipartFile file, Model model) {
        if (file == null || file.isEmpty()) {
            model.addAttribute("error", "Please select a file to compress.");
            return "index";
        }
        System.out.println("Compress request: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)");
        CompressionStats stats = compressService.compress(file);
        model.addAttribute("stats", stats);
        model.addAttribute("mode", "compress");
        return "result";
    }

    @PostMapping(value = "/decompress", consumes = "multipart/form-data")
    public String decompressFile(@RequestParam("file") MultipartFile file, Model model) {
        if (file == null || file.isEmpty()) {
            model.addAttribute("error", "Please select a .zip file to decompress.");
            return "index";
        }
        System.out.println("Decompress request: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)");
        CompressionStats stats = decompressService.decompress(file);
        model.addAttribute("stats", stats);
        model.addAttribute("mode", "decompress");
        return "result";
    }

    /**
     * Download endpoint — streams the file directly to the browser.
     * FIX: catches all exceptions and returns a clear error message instead of crashing.
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam("file") String fileName,
            @RequestParam("mode") String mode) {

        System.out.println("Download request — file: " + fileName + ", mode: " + mode);

        try {
            // FIX: sanitize fileName — strip any directory path from it
            String safeName = new File(fileName).getName();

            byte[] data;
            if ("compress".equals(mode)) {
                data = compressService.getCompressedFile(safeName);
            } else {
                data = decompressService.getExtractedFile(safeName);
            }

            // FIX: encode filename for Content-Disposition header so special chars don't break it
            String encodedName = URLEncoder.encode(safeName, StandardCharsets.UTF_8)
                    .replace("+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + safeName + "\"; filename*=UTF-8''" + encodedName);
            headers.setContentLength(data.length);

            System.out.println("Sending file: " + safeName + " (" + data.length + " bytes)");
            return ResponseEntity.ok().headers(headers).body(data);

        } catch (IOException e) {
            // FIX: print full stack trace so you can see the real error in IntelliJ console
            System.err.println("Download error: " + e.getMessage());
            e.printStackTrace();
            // Return proper error response body instead of empty 404
            String errorMsg = "Download failed: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(errorMsg.getBytes(StandardCharsets.UTF_8));
        }
    }
}
