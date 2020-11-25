package com.bewsoftware.mdj.core;

public class HTMLToken {

    private final boolean isTag;
    private final String text;

    private HTMLToken(boolean tag, String value) {
        isTag = tag;
        text = value;
    }

    public static HTMLToken tag(String text) {
        return new HTMLToken(true, text);
    }

    public static HTMLToken text(String text) {
        return new HTMLToken(false, text);
    }

    /**
     * @return <code>true</code> if this is a tag, <code>false</code> if it's
     *         text.
     */
    public boolean isTag() {
        return isTag;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        String type;
        if (isTag()) {
            type = "tag";
        } else {
            type = "text";
        }
        return type + ": " + getText();
    }
}