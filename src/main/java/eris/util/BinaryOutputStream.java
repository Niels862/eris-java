package eris.util;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BinaryOutputStream extends OutputStream {
    private final OutputStream out;

    public BinaryOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    public void write(@Nonnull byte[] b) throws IOException {
        out.write(b);
    }

    public void write(@Nonnull byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    public void writeUTF8(String s, boolean withNullTerminator) throws IOException {
        out.write(s.getBytes(StandardCharsets.UTF_8));
        if (withNullTerminator) {
            write(0);
        }
    }

    public void writeU8(int val) throws IOException {
        writeLittleEndian(val, 1);
    }

    public void writeU16(int val) throws IOException {
        writeLittleEndian(val, 2);
    }

    public void writeU32(int val) throws IOException {
        writeLittleEndian(val, 4);
    }

    public void writeLittleEndian(int val, int numBytes) throws IOException {
        for (int i = 0; i < numBytes; i++) {
            out.write(val & 0xFF);
            val >>>= 8;
        }
        assert val == 0;
    }

    public void flush() throws IOException {
        out.flush();
    }

    public void close() throws IOException {
        out.close();
    }
}
