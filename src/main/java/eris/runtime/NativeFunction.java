package eris.runtime;

import java.util.List;

public interface NativeFunction {
    Object call(List<Object> args);
}
