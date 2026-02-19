package com.filecompressor.model;

public class CompressionStats {
    private String originalFileName;
    private long originalSize;
    private long compressedSize;
    private long timeTakenMs;
    private String outputFileName;
    private String message;
    private boolean success;

    public CompressionStats() {}

    public CompressionStats(String originalFileName, long originalSize,
                             long compressedSize, long timeTakenMs,
                             String outputFileName, boolean success, String message) {
        this.originalFileName = originalFileName;
        this.originalSize = originalSize;
        this.compressedSize = compressedSize;
        this.timeTakenMs = timeTakenMs;
        this.outputFileName = outputFileName;
        this.success = success;
        this.message = message;
    }

    public double getCompressionRatio() {
        if (originalSize == 0) return 0;
        return Math.round(((1.0 - (double) compressedSize / originalSize) * 100) * 100.0) / 100.0;
    }

    public String getOriginalSizeFormatted() { return formatSize(originalSize); }
    public String getCompressedSizeFormatted() { return formatSize(compressedSize); }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        else if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        else return String.format("%.2f MB", bytes / (1024.0 * 1024));
    }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String v) { this.originalFileName = v; }
    public long getOriginalSize() { return originalSize; }
    public void setOriginalSize(long v) { this.originalSize = v; }
    public long getCompressedSize() { return compressedSize; }
    public void setCompressedSize(long v) { this.compressedSize = v; }
    public long getTimeTakenMs() { return timeTakenMs; }
    public void setTimeTakenMs(long v) { this.timeTakenMs = v; }
    public String getOutputFileName() { return outputFileName; }
    public void setOutputFileName(String v) { this.outputFileName = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { this.message = v; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean v) { this.success = v; }
}
