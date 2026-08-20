package eris.module;

import eris.util.BinaryOutputStream;
import jflex.logging.Out;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModuleWriter {
    private final Module module;

    public ModuleWriter(Module module) {
        this.module = module;
    }

    public void write(Path path) throws IOException {
        Files.createDirectories(path.getParent());

        try (BinaryOutputStream stream = new BinaryOutputStream(new FileOutputStream(path.toFile()))) {
            writeHeader(stream);
        }
    }

    private void writeHeader(BinaryOutputStream stream) throws IOException {
        stream.writeUTF8("Eris", false);
        stream.writeU16(module.constants.size());
        stream.writeU16(module.functions.size());
    }
}
