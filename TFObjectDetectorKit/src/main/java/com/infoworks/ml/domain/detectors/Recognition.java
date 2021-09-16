package com.infoworks.ml.domain.detectors;

import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.entity.Ignore;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * An immutable result returned by a Classifier describing what was recognized.
 */
public class Recognition extends Entity {
    /**
     *
     */
    @Ignore
    public static final String NONE_ITEM = "item {\n" +
                                            "\tid: 0, \n" +
                                            "\tname: 'none' \n" +
                                            "}";
    /**
     * A unique identifier for what has been recognized. Specific to the class, not the instance of
     * the object.
     */
    private final String id;

    /**
     * Display name for the recognition.
     */
    private final String title;

    /**
     * A sortable score for how good the recognition is relative to others. Higher should be better.
     */
    private final Float confidence;

    /** Optional location within the source image for the location of the recognized object. */
    @Ignore
    private Rectangle2D location;

    public Recognition(final String id, final String title, final Float confidence, Rectangle2D location) {
        this.id = id;
        this.title = title;
        this.confidence = confidence;
        this.location = location;
    }

    public Recognition(final String objDetectLabel, final Float confidence) {
        this(objDetectLabel, confidence, null);
    }

    public Recognition(final String objDetectLabel, final Float confidence, Rectangle2D location){
        this.id = parseId(objDetectLabel);
        this.title = parseTitle(objDetectLabel);
        this.confidence = confidence;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Float getConfidence() {
        return confidence;
    }

    public Rectangle2D getLocation() {
        return location;
    }

    public void setLocation(Rectangle2D location) {
        this.location = location;
    }

    @Override
    public String toString() {
        String resultString = "";
        if (id != null) {
            resultString += "[" + id + "] ";
        }

        if (title != null) {
            resultString += title + " ";
        }

        if (confidence != null) {
            resultString += String.format("(%.1f%%) ", confidence * 100.0f);
        }

        if (location != null) {
            resultString += location + " ";
        }

        return resultString.trim();
    }

    protected final String parseTitle(String objDetectLabel){
        //item {id: 11, name: 'rocket_logo' }
        String[] splits = objDetectLabel.split("name:");
        if (splits.length >= 2){
            return splits[1].trim()
                    .replace("}", "")
                    .trim()
                    .replace("'", "");
        }
        return "";
    }

    protected final String parseId(String objDetectLabel){
        //item {id: 11, name: 'rocket_logo' }
        String[] splits = objDetectLabel.split(", name:");
        if (splits.length >= 1){
            return splits[0].trim()
                    .replace("item {id:", "")
                    .trim();
        }
        return "";
    }

    public Map<String, Double> convertLocation() {
        if (getLocation() != null){
            Map<String, Double> map = new HashMap<>();
            map.put("x", getLocation().getMinX());
            map.put("y", getLocation().getMinY());
            map.put("width", getLocation().getWidth());
            map.put("height", getLocation().getHeight());
            return map;
        }
        return null;
    }
}
