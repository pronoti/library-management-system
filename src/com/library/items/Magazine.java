package com.library.items;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Magazine is an immutable library item
 * @author A00325358 Pronoti Saha
 */
public final class Magazine implements LibraryItem {

    private final String title;
    private final Map<String, String> metaData;
    private final String[] optionalParams;

    /**
     * Constructs a Magazine with title only.
     * @param title magazine title
     */
    public Magazine(String title) {
        this(title, Collections.emptyMap());
    }

    /**
     * Constructs a Magazine with title and metadata.
     * @param title magazine title
     * @param metaData key-value metadata map
     */
    public Magazine(String title, Map<String, String> metaData) {
        this(title, metaData, new String[0]);
    }

    /**
     * Constructs a Magazine with title, metadata, and optional parameters.
     * @param title magazine title
     * @param metaData key-value metadata map
     * @param optionalParams additional varargs parameters (e.g. frequency)
     */
    public Magazine(String title, Map<String, String> metaData, String... optionalParams) {
        this.title = Optional.ofNullable(title).orElse("Untitled Magazine").trim();
        this.metaData = Map.copyOf(Optional.ofNullable(metaData).orElse(Collections.emptyMap()));
        this.optionalParams = optionalParams == null ? new String[0] : optionalParams.clone();
    }

    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Returns a defensive copy of the metadata map.
     * @return metadata map
     */
    public Map<String, String> getMetadata() {
        return new HashMap<>(metaData);
    }

    /**
     * Returns a defensive copy of the optional parameters array.
     * @return optional parameters
     */
    public String[] getOptionalParams() {
        return optionalParams.clone();
    }

    @Override
    public void info() {
        System.out.printf("%-3s | %-25s | %-20s | %-12s | %s %n",
                "-",
                title,
                "Magazine",
                "PERIODICAL",
                "Reference only");
    }
}
