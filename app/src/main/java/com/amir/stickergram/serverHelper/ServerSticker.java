package com.amir.stickergram.serverHelper;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class ServerSticker {
    public static final String NUM = "num";
    public static final String EN_NAME = "en_name";
    public static final String PER_NAME = "per_name";
    public static final String MODE = "mode";
    public static final String HAS_LINK = "has_link";
    public static final String LINK_NAME_EN = "link_name_en";
    public static final String LINK_NAME_PER = "link_name_per";
    public static final String LINK = "link";

    private int num;
    private String enName;
    private String perName;
    private int mode;
    private boolean hasLink;
    private String linkNameEn;
    private String linkNamePer;
    private String link;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public ServerSticker(int num, String enName, String perName, int mode, boolean hasLink, String linkNameEn, String linkNamePer, String link) {
        this.num = num;
        this.enName = enName;
        this.perName = perName;
        this.mode = mode;
        this.hasLink = hasLink;
        this.linkNameEn = linkNameEn;
        this.linkNamePer = linkNamePer;
        this.link = link;

//        Log.e(getClass().getSimpleName(), "link: " + link);

    }

    /**
     * @return The num
     */
    public int getNum() {
        return num;
    }

    /**
     * @param num The num
     */
    public void setNum(int num) {
        this.num = num;
    }

    /**
     * @return The enName
     */
    public String getEnName() {
        return enName;
    }

    /**
     * @param enName The en_name
     */
    public void setEnName(String enName) {
        this.enName = enName;
    }

    /**
     * @return The perName
     */
    public String getPerName() {
        return perName;
    }

    /**
     * @param perName The per_name
     */
    public void setPerName(String perName) {
        this.perName = perName;
    }

    /**
     * @return The mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * @param mode The mode
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * @return The hasLink
     */
    public boolean getHasLink() {
        return hasLink;
    }

    /**
     * @param hasLink The has_link
     */
    public void setHasLink(boolean hasLink) {
        this.hasLink = hasLink;
    }

    /**
     * @return The linkNameEn
     */
    public String getLinkNameEn() {
        return linkNameEn;
    }

    /**
     * @param linkNameEn The link_name_en
     */
    public void setLinkNameEn(String linkNameEn) {
        this.linkNameEn = linkNameEn;
    }

    /**
     * @return The linkNamePer
     */
    public String getLinkNamePer() {
        return linkNamePer;
    }

    /**
     * @param linkNamePer The link_name_per
     */
    public void setLinkNamePer(String linkNamePer) {
        this.linkNamePer = linkNamePer;
    }

    /**
     * @return The link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link The link
     */
    public void setLink(String link) {
        this.link = link;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}