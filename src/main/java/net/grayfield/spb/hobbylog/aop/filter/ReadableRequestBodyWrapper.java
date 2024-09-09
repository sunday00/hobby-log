package net.grayfield.spb.hobbylog.aop.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadableRequestBodyWrapper extends HttpServletRequestWrapper {
    class ServletInputStreamImpl extends ServletInputStream {
        private InputStream inputStream;

        public ServletInputStreamImpl(final InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public boolean isFinished() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isReady() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public int read() throws IOException {
            return this.inputStream.read();
        }

        @Override
        public int read(final byte[] b) throws IOException {
            return this.inputStream.read(b);
        }

        @Override
        public void setReadListener(final ReadListener listener) {
            // TODO Auto-generated method stub
        }
    }

    private byte[] bytes;
    private String requestBody;

    public ReadableRequestBodyWrapper(final HttpServletRequest request) throws IOException {
        super(request);

        InputStream in = super.getInputStream();
        this.bytes = in.readAllBytes();
        this.requestBody = new String(this.bytes);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.bytes);
        return new ServletInputStreamImpl(byteArrayInputStream);
    }

    public String getRequestBody() {
        return this.requestBody;
    }
}
