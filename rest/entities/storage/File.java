package org.example.rest.entities.storage;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

@Entity
public class File {

    private static final String defaultPath = "https://storage.googleapis.com/progweb2020-272915.appspot.com/ActorFiles/";


    @Id
    private String id;
    @Index private String uploader;
    @Index private String consumer;
    private String url;
    private String name;
    private String hashTags;
    private String ipView;
    private Date uploadDate;
    private Date viewedDate;

    public File() {}

    public File(String uploader, String consumer, String name, String hashTags, String ext) {
        this.id = uploader + "_" + consumer + "_" + name;
        this.uploader = uploader;
        this.consumer = consumer;
        this.url = defaultPath + this.id + "." + ext;
        this.name = name;
        this.hashTags = hashTags;
        this.uploadDate = new Date(System.currentTimeMillis());
        this.viewedDate = null;
        this.ipView = null;
    }

    public static String getDefaultPath() {
        return defaultPath;
    }
    public String getId() {
        return id;
    }
    public String getUploader() {
        return uploader;
    }
    public String getConsumer() {
        return consumer;
    }
    public String getUrl() {
        return url;
    }
    public String getName() {
        return name;
    }
    public String getHashTags() {
        return hashTags;
    }
    public Date getUploadDate() {
        return uploadDate;
    }
    public Date getViewedDate() {
        return viewedDate;
    }
    public String getIpView() {
        return ipView;
    }
    public void setIpView(String ipView) {
        this.ipView = ipView;
    }
    public void setViewedDate(Date viewedDate) {
        this.viewedDate = viewedDate;
    }
}
