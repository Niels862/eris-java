package eris.module.constant;

import org.apache.commons.text.StringEscapeUtils;

public class StringConstant extends Constant {
    public final String value;

    public StringConstant(String value) {
        this.value = value;
    }

    public String toString() {
        return "'" + StringEscapeUtils.escapeJava(value) + "'";
    }
}
