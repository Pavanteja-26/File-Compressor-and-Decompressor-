# 🗜️ File Compressor and Decompressor
### Internship Project — ElevateLabs | Java Domain

---

## 📌 About the Project

This is my internship project built during the **ElevateLabs Internship Program (2025)**. The goal was to build a web-based tool that allows users to compress any file into a `.zip` archive and decompress any `.zip` file back to its original contents — all through a clean browser interface.

I chose to build this as a **Spring Boot web application** instead of a desktop JavaFX app because I wanted to learn how backend Java development works with a real frontend (HTML + CSS). The compression and decompression logic is handled entirely using Java's built-in `java.util.zip` package — no external libraries needed for the core functionality.

---

## 🎯 Objective

> Build a desktop/web tool to zip and unzip files with progress tracking and compression stats.

**Source:** ElevateLabs Internship Project List — Project #8

---

## 🛠️ Tools and Technologies Used

| Technology | Purpose |
|---|---|
| Java 17 | Core programming language |
| Spring Boot 3.2 | Backend web framework |
| Thymeleaf | Server-side HTML templating |
| HTML5 + CSS3 | Frontend UI |
| JavaScript (Vanilla) | Drag & drop, form interactions |
| `java.util.zip` | Built-in Java compression library |
| Maven | Project build and dependency management |
| VS Code / IntelliJ IDEA | IDE |

---

## 📁 Project Structure

```
FileCompressorFixed/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/filecompressor/
│       │       ├── FileCompressorApplication.java   ← App entry point
│       │       ├── controller/
│       │       │   └── FileController.java          ← Handles web requests
│       │       ├── service/
│       │       │   ├── CompressService.java         ← Zip logic
│       │       │   └── DecompressService.java       ← Unzip logic
│       │       └── model/
│       │           └── CompressionStats.java        ← Stats data model
│       └── resources/
│           ├── static/
│           │   ├── css/style.css                    ← Styling
│           │   └── js/main.js                       ← Frontend JS
│           ├── templates/
│           │   ├── index.html                       ← Home page
│           │   └── result.html                      ← Results page
│           └── application.properties               ← App configuration
└── pom.xml                                          ← Maven dependencies
```

---

## ⚙️ How to Run the Project

### Prerequisites

Make sure you have the following installed before running:

- **Java JDK 17** — [Download here](https://adoptium.net/temurin/releases/?version=17)
- **Apache Maven** — [Download here](https://maven.apache.org/download.cgi)
- **VS Code** with these extensions:
  - Extension Pack for Java *(by Microsoft)*
  - Spring Boot Extension Pack *(by VMware)*
  - Maven for Java *(by Microsoft)*

To verify your installations, open a terminal and run:
```bash
java -version
mvn -version
```

---

### Steps to Run

**1. Extract the project ZIP**

Right-click `FileCompressorFixed.zip` → Extract All → choose a folder like `C:\Projects\`

**2. Open in VS Code**

```
File → Open Folder → Select the FileCompressorFixed folder
```

**3. Wait for Java to load**

VS Code will show `Java: Loading...` at the bottom. Wait for it to finish (1–2 minutes first time).

**4. Run the application**

Open the terminal in VS Code (`Ctrl + backtick`) and run:

```bash
mvn spring-boot:run
```

**5. Open in browser**

Once you see `Started FileCompressorApplication` in the terminal, open:

```
http://localhost:8080
```

---

## 🚀 Features

- **File Compression** — Upload any file and compress it into a `.zip` archive
- **File Decompression** — Upload any `.zip` file and extract its contents
- **Compression Stats** — See original size, compressed size, space saved (%), and time taken
- **Animated Progress Bar** — Visual bar showing compression ratio
- **Drag and Drop** — Drag files directly onto the upload area
- **Download Button** — Download your compressed or extracted files instantly
- **Error Handling** — Clear messages if something goes wrong (wrong file type, empty file, etc.)
- **Security** — Zip slip attack prevention built into the decompression logic

---

## 📸 How It Works — Step by Step

### Compression Flow
```
User uploads file
       ↓
FileController receives MultipartFile
       ↓
CompressService reads file bytes
       ↓
ZipOutputStream wraps bytes with DEFLATE compression
       ↓
.zip saved to ~/FileCompressorOutput/
       ↓
Stats (size, ratio, time) returned to result page
       ↓
User clicks Download → file streamed to browser
```

### Decompression Flow
```
User uploads .zip file
       ↓
FileController receives MultipartFile
       ↓
DecompressService reads zip using ZipInputStream
       ↓
Each ZipEntry extracted to a temp folder
       ↓
Extracted files re-zipped for download
       ↓
Stats returned to result page
       ↓
User clicks Download → gets all extracted files
```

---

## 🔑 Key Java Concepts Used

**ZipOutputStream** — Used to write compressed data. Each file inside the zip is represented as a `ZipEntry`. The compression level is set to `Deflater.BEST_COMPRESSION` for maximum space saving.

**ZipInputStream** — Used to read and extract entries from an existing `.zip` file. Each `ZipEntry` is read in a loop until `getNextEntry()` returns null.

**MultipartFile** — Spring's interface for handling file uploads from HTML forms. The `getBytes()` method loads the file into memory for processing.

**ResponseEntity** — Used in the download endpoint to send the file as a byte array with the correct `Content-Disposition` header so the browser downloads it instead of displaying it.

---

## 🐛 Bugs I Fixed During Development

During development I ran into several issues that taught me a lot about how Spring Boot works:

1. **`multipart.enabled` was missing** — File uploads were silently ignored without this property in `application.properties`
2. **Relative output path** — Using `"output/"` as the save location failed on Windows because it resolved to a system folder. Fixed by using `System.getProperty("user.home")` to always get a writable path
3. **Missing `import java.io.File`** — Caused a runtime crash on the download endpoint
4. **Thymeleaf URL encoding** — `th:href` with `@{}` was encoding dots in filenames like `test.txt.zip` which broke the file lookup. Fixed by building the URL manually as a string
5. **Spaces in filenames** — Spaces in filenames were breaking URL parameters. Fixed by replacing spaces with underscores before saving

---

## 📊 Sample Output

When you compress a file, the result page shows stats like:

```
Original File   : report.pdf
Original Size   : 2.45 MB
Compressed Size : 1.12 MB
Space Saved     : 54.3%
Time Taken      : 243 ms
```

Compressed files are saved to:
```
C:\Users\YourName\FileCompressorOutput\
```

---

## 📝 Conclusion

This project helped me understand how backend web development works in Java using Spring Boot. I learned how to handle file uploads with `MultipartFile`, how Java's `java.util.zip` package works internally, and how to serve files for download using `ResponseEntity`. Building it as a web app instead of a desktop app also helped me learn how HTML forms interact with a Java backend through HTTP POST requests.

The biggest challenge was debugging the download feature — the file was being saved correctly but the download was failing due to path and encoding issues. Solving that taught me a lot about how file I/O and HTTP headers work together.

---

## 👨‍💻 Author

**Mangaraju Pavan Teja**
Java Intern — ElevateLabs, 2025
Domain: Java Development
Project: File Compressor and Decompressor

---

## 📄 License

This project was built for educational purposes as part of the ElevateLabs internship program.
