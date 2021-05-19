package org.basetools.util.io;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

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

    public static InputStream streamFromClassloader(Class clazz, String filename) throws IOException {
       return clazz.getClassLoader().getResourceAsStream(filename);
    }
    public static Reader readerFromClassloader(Class clazz, String filename) throws IOException {
        URL input = clazz.getClassLoader().getResource(filename);
        if (input == null) {
            return null;
        }
        return new FileReader(input.getFile());
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

    /**
     * for all elements of java.class.path get a Collection of resources Pattern
     * pattern = Pattern.compile(".*"); gets all resources
     *
     * @param pattern the pattern to match
     * @return the resources in the order they are found
     */
    public static Collection<String> getResources(
            final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
        for (final String element : classPathElements) {
            retval.addAll(getResources(element, pattern));
        }
        return retval;
    }

    private static Collection<String> getResources(
            final String element,
            final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final File file = new File(element);
        if (file.isDirectory()) {
            retval.addAll(getResourcesFromDirectory(file, pattern));
        } else {
            retval.addAll(getResourcesFromJarFile(file, pattern));
        }
        return retval;
    }

    private static Collection<String> getResourcesFromJarFile(
            final File file,
            final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        ZipFile zf;
        try {
            zf = new ZipFile(file);
        } catch (final ZipException e) {
            throw new Error(e);
        } catch (final IOException e) {
            throw new Error(e);
        }
        final Enumeration e = zf.entries();
        while (e.hasMoreElements()) {
            final ZipEntry ze = (ZipEntry) e.nextElement();
            final String fileName = ze.getName();
            final boolean accept = pattern.matcher(fileName).matches();
            if (accept) {
                retval.add(fileName);
            }
        }
        try {
            zf.close();
        } catch (final IOException e1) {
            throw new Error(e1);
        }
        return retval;
    }

    private static Collection<String> getResourcesFromDirectory(
            final File directory,
            final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final File[] fileList = directory.listFiles();
        for (final File file : fileList) {
            if (file.isDirectory()) {
                retval.addAll(getResourcesFromDirectory(file, pattern));
            } else {
                try {
                    final String fileName = file.getCanonicalPath();
                    final boolean accept = pattern.matcher(fileName).matches();
                    if (accept) {
                        retval.add(fileName);
                    }
                } catch (final IOException e) {
                    throw new Error(e);
                }
            }
        }
        return retval;
    }
}

