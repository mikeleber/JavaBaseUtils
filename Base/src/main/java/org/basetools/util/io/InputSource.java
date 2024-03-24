package org.basetools.util.io;

import org.basetools.util.StringUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class InputSource extends org.xml.sax.InputSource implements AutoCloseable {
    private org.basetools.util.StringUtils.ContentType _contentType;

    public InputSource() {
        super();
    }

    public InputSource(String systemId) {
        super(systemId);
    }

    public InputSource(InputStream byteStream) {
        super(byteStream);
    }

    public InputSource(Reader characterStream) {
        super(characterStream);
    }

    public StringUtils.ContentType getContentType() {
        if (_contentType != null) return _contentType;
        if (getSystemId() != null) {
            String ext = org.apache.commons.lang3.StringUtils.substringAfterLast(getSystemId(), ".");
            return StringUtils.ContentType.valueOf(ext, StringUtils.ContentType.unknown);
        }
        return StringUtils.ContentType.unknown;
    }

    @Override
    public void setPublicId(String publicId) {
        super.setPublicId(publicId);
    }

    @Override
    public String getPublicId() {
        return super.getPublicId();
    }

    @Override
    public void setSystemId(String systemId) {
        super.setSystemId(systemId);
    }

    @Override
    public String toString() {
        return getSystemId()+":"+getPublicId();
    }

    @Override
    public String getSystemId() {
        return super.getSystemId();
    }

    @Override
    public void setByteStream(InputStream byteStream) {
        super.setByteStream(byteStream);
    }

    @Override
    public InputStream getByteStream() {
        return super.getByteStream();
    }

    @Override
    public void setEncoding(String encoding) {
        super.setEncoding(encoding);
    }

    @Override
    public String getEncoding() {
        return super.getEncoding();
    }

    @Override
    public void setCharacterStream(Reader characterStream) {
        super.setCharacterStream(characterStream);
    }

    @Override
    public Reader getCharacterStream() {
        return super.getCharacterStream();
    }

    public URI getUriFromSystemId() {
        try {
            return new URI(getSystemId());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public File getFileFromSystemId() {
        return new File(getUriFromSystemId());
    }

    public InputSource withContentType(StringUtils.ContentType type) {
        _contentType = type;
        return this;
    }

    public InputSource withSystemId(String id) {
        setSystemId(id);
        return this;
    }

    public InputSource withPublicId(String id) {
        setPublicId(id);
        return this;
    }

    public Reader evalReader() {
        if (super.getCharacterStream() != null) return super.getCharacterStream();
        if (super.getByteStream() != null) {
            try {
                return new InputStreamReader(super.getByteStream(), super.getEncoding());
            } catch (UnsupportedEncodingException e) {
               e.printStackTrace();
            }
        }
        if (getUriFromSystemId()!=null){
            try {
                return new InputStreamReader(getUriFromSystemId().toURL().openStream(), super.getEncoding());
            } catch (IOException e) {
                try {
                    return new FileReader(new File(getSystemId()));
                } catch (FileNotFoundException ex) {
                    throw new ResourceNotFoundException(ex);
                }

            }
        }
        try {
            return new FileReader(getSystemId());
        } catch (FileNotFoundException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    public boolean hasReader() {
        if (super.getCharacterStream() != null) return true;
        if (super.getByteStream() != null) {
            return true;
        }
        return false;
    }

    public String readString() throws IOException {
        Reader input = evalReader();
        try {
            return input != null ? FileUtils.readAsString(input) : null;
        } finally {
            StreamHelper.close(input);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputSource that = (InputSource) o;
        return getSystemId() != null && getSystemId().equals(that.getSystemId());
    }

    @Override
    public int hashCode() {
        return getSystemId().hashCode();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public void close() {
        System.out.println("closing: "+getSystemId());
    }
}
