package org.basetools.util.io;

import org.basetools.util.StringUtils;

import java.io.*;

public class InputSource extends org.xml.sax.InputSource {
    private org.basetools.util.StringUtils.ContentType _payloadType;

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

    public StringUtils.ContentType getPayloadType() {
        return _payloadType;
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

    public InputSource withPayloadType(StringUtils.ContentType type) {
        _payloadType = type;
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
        return null;
    }

    public String readString() throws IOException {
        Reader input = evalReader();
        return input != null ? FileUtils.readAsString(input) : null;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }
}
