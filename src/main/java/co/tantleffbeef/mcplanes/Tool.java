package co.tantleffbeef.mcplanes;

import java.io.File;
import java.util.Objects;

public final class Tool {
    private Tool() {
        // STATIC ONLY!!!! NO INSTANCES!!!!
        throw new UnsupportedOperationException();
    }

    public static void clearFolder(File folder) {
        if (folder.isFile())
            return;

        for (File f : Objects.requireNonNull(folder.listFiles())) {
            // if f is a subdirectory we need to clear it first too
            if (f.isDirectory())
                clearFolder(f);
            f.delete();
        }
    }
}
