package org.leber.util.io;

import java.io.*;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.CRC32;

public class StreamHelper {
    public static final void closeStream(InputStream inStream) {
        if (inStream != null) {
            try {
                inStream.close();
            } catch (IOException e) {
                // forget about
            }
        }
    }

    /**
     * Provides a stream based on a given iterator.
     *
     * @param sourceIterator the iterator we want to use for streaming.
     * @return the stream based on the specified iterator.
     */
    public static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        return asStream(sourceIterator, false);
    }

    /**
     * Provides a stream based on a given iterator.
     *
     * @param sourceIterator the iterator we want to use for streaming.
     * @param parallel       if {@code true} then the returned stream is a parallel
     *                       stream; if {@code false} the returned stream is a sequential
     *                       stream.
     * @return the stream based on the specified iterator.
     */
    public static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    public static OutputStream write(InputStream inStream, OutputStream outstream) throws IOException {
        return write(inStream, outstream, true);
    }

    public static OutputStream write(InputStream inStream, OutputStream outstream, boolean doFlush) throws IOException {
        return write(inStream, outstream, doFlush, true);
    }

    public static OutputStream write(InputStream inStream, OutputStream outstream, boolean doFlush, boolean useBuffedOut) throws IOException {
        return write(inStream, outstream, doFlush, useBuffedOut, null);
    }

    public static OutputStream write(InputStream inStream, OutputStream outstream, boolean doFlush, boolean useBuffedOut, CRC32 crc) throws IOException {
        if (useBuffedOut) {
            outstream = new BufferedOutputStream(outstream);
        }
        BufferedInputStream bis = new BufferedInputStream(inStream);
        byte[] rgb = new byte[10240];
        int readCounter = 0;
        while ((readCounter = bis.read(rgb)) > -1) {
            if (crc != null) {
                crc.update(rgb);
            }
            outstream.write(rgb, 0, readCounter);
        }
        if (doFlush) {
            outstream.flush();
        }
        return outstream;
    }
}
