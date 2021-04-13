package org.basetools.util.io;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils extends org.apache.commons.io.FileUtils {

    /**
     * Reads the contents of a file into a String.
     *
     * @param inputStream
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String readAsString(InputStream inputStream, String encoding) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, encoding);
        return writer.toString();
    }

    /**
     * Reads the contents of a file into a String.
     *
     * @param reader the file to read, must not be {@code null}
     * @return the file contents
     * @throws IOException in case of an I/O error
     */
    public static String readAsString(Reader reader) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(reader, writer);
        return writer.toString();
    }

    /**
     * Creates a File if the file does not exist, or returns a
     * reference to the File if it already exists.
     */
    public static File createOrRetrieve(final String target) throws IOException {

        final Path path = Paths.get(target);

        if (Files.notExists(path)) {
            //LOG.info("Target file \"" + target + "\" will be created.");

            if (!Files.isDirectory(path) && path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            File aFile = Files.createFile(path).toFile();
            aFile.mkdirs();
            return aFile;
        }
        // LOG.info("Target file \"" + target + "\" will be retrieved.");
        return path.toFile();
    }

    /**
     * Reads the contents of a file into a String from the clazz.getClassLoader().getResourceAsStream.
     *
     * @param clazz    providing the classloader to use
     * @param filename
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String readAsStringFromClassloader(Class clazz, String filename, String encoding) throws IOException {
        try (InputStream inputStream = clazz.getClassLoader().getResourceAsStream(filename)) {
            if (inputStream == null) {
                return null;
            }
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer, encoding);
            return writer.toString();
        }
    }

    /**
     * Reads the contents of a file into a String from the clazz.getResourceAsStream.
     * As fallback it tries to load it from clazz.getClassLoader().getResourceAsStream.
     *
     * @param clazz    providing the classloader to use
     * @param filename
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String readAsStringFromClass(Class clazz, String filename, String encoding) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = clazz.getResourceAsStream(filename);

            if (inputStream == null) {
                return readAsStringFromClassloader(clazz, filename, encoding);
            }
            if (inputStream == null) {
                return null;
            }
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer, encoding);
            return writer.toString();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public static URL getLoctionFromClass(Class clazz, String filename) {
        URL inputURL = clazz.getResource(filename);

        if (inputURL == null) {
            inputURL = clazz.getClassLoader().getResource(filename);
        }
        return inputURL;
    }

    public static final String getClassLoadingLocation(Class clazz) {
        final File f = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());
        return f.getPath();
    }

    /**
     * Streams the InputStream into the opened outputstream.
     *
     * @param in
     * @throws IOException
     */
    public static void writeIntoFile(File dest, InputStream in) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            StreamHelper.write(in, fos);
        }
    }

    /**
     * Write's the byte sequence into the opened outputstream.
     *
     * @param in
     * @throws IOException
     */
    public static void writeIntoFile(File dest, byte[] in) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ByteArrayInputStream bis = new ByteArrayInputStream(in);
            StreamHelper.write(bis, bos);
        }
    }

    public static void convertAll(String rootPath, Charset sourceCharset, Charset destCharset) throws IOException {
        List<Path> allFiles = Files.walk(Paths.get(rootPath))
                .filter(Files::isRegularFile).collect(Collectors.toList());
        allFiles.forEach(p -> {
            try {
                String data = org.apache.commons.io.FileUtils.readFileToString(p.toFile(), sourceCharset);
                if (p.getFileName().toString().endsWith(".java")) {
                    Files.write(p, data.getBytes(destCharset));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
