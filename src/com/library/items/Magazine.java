package com.library.items;

import java.util.*;

/**
 * Magazine is an immutable library item
 * @author A00325358 Pronoti Saha
 */
public final class Magazine implements LibraryItem {

    private final String title;
    private final Map<String, String> metaData;
    private final String[] optionalParams;

    /**
     * Magazine Constructor
     * @param title Title of the Magazine
     * @param metaData Metadata
     * @param optionalParams Varargs
     */
    public Magazine(String title, Map<String, String> metaData, String ...optionalParams) {
        this.title = title;
        this.metaData = Optional.ofNullable(metaData).orElse(Collections.emptyMap());
        this.optionalParams = optionalParams;
    }

    /**
     * Magazine Constructor
     * @param title Title of the Magazine
     * @param metaData Metadata
     */
    public Magazine(String title, Map<String, String> metaData) {
        this(title, metaData, null);
    }

    /**
     * Magazine Constructor
     * @param title Title of the Magazine
     */
    public Magazine(String title) {
        this(title, Collections.emptyMap());
    }

    /**
     * Get Title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get Metadata
     * @return metadata
     */
    public Map<String, String> getMetadata() {
        return new HashMap<>(metaData);
    }

    /**
     * Get Optional Parameters
     * @return Otional Parameters
     */
    public String[] getOptionalParams() {
        return optionalParams;
    }

    /**
     * Get info
     */
    @Override
    public void info() {
        System.out.println("Magazine: " + title + ", Metadata: " + metaData);
    }
}



