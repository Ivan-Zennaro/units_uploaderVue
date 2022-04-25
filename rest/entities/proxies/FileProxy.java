package org.example.rest.entities.proxies;

import org.example.rest.entities.storage.File;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class FileProxy {

    private String uploader;
    private String consumer;
    private String url;
    private String name;
    private String hashTags;
    private String ipView;
    private String viewedDate;

    public FileProxy() {}

    public FileProxy(String uploader, String consumer, String url, String name, String hashTags) {
        this.uploader = uploader;
        this.consumer = consumer;
        this.url = url;
        this.name = name;
        this.hashTags = hashTags;
        this.ipView = "Not visualized";
        this.viewedDate = "Not visualized";
    }

    public FileProxy (File file){
        this.uploader = file.getUploader();
        this.consumer = file.getConsumer();
        this.url = file.getUrl();
        this.name = file.getName().replace("_"," ");
        this.hashTags = file.getHashTags();
        if (file.getIpView() == null)
            this.ipView = "Not visualized";
        else this.ipView = file.getIpView();
        if (file.getViewedDate() == null)
            this.viewedDate = "Not visualized";
        else {
            String pattern = "dd/MM/yyyy HH:mm:ss";
            DateFormat df = new SimpleDateFormat(pattern);
            this.viewedDate = df.format(file.getViewedDate());
        }

    }

    public String getUploader() {
        return uploader;
    }
    public void setUploader(String uploader) {
        this.uploader = uploader;
    }
    public String getName() {
        return name;
    }
    public String getConsumer() {
        return consumer;
    }
    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setHashTags(String hashTags) {
        this.hashTags = hashTags;
    }
    public String getIpView() {
        return ipView;
    }
    public void setIpView(String ipView) {
        this.ipView = ipView;
    }
    public String getViewedDate() {
        return viewedDate;
    }
    public void setViewedDate(String viewedDate) {
        this.viewedDate = viewedDate;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHashTags() {
        return hashTags;
    }
}
