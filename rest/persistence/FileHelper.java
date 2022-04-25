package org.example.rest.persistence;

import org.example.rest.entities.proxies.FileProxy;
import org.example.rest.entities.storage.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class FileHelper extends AbstractHelper<File>{


    private static final int secondsInAMonth = 60 * 60 * 24 * 30;

    public static List<File> getFilesByUplCons (String uploader, String consumer){
        return ofy().load().type(File.class).filter("uploader",uploader).filter("consumer",consumer).list();
    }

    public static List<File> getFilesByUploader (String uploader){
        return ofy().load().type(File.class).filter("uploader",uploader).list();
    }

    public static List<File> getFilesByConsumer (String consumer){
        return ofy().load().type(File.class).filter("consumer",consumer).list();
    }

    public static int getNdocLastMonth (String uploader){
        Long now = System.currentTimeMillis()/1000;
        return getFilesPeriodByUploader(uploader, now - secondsInAMonth,now).size();
    }
    public static int getNdocPeriod (String uploader,Long startDate, Long endDate){
        return getFilesPeriodByUploader(uploader, startDate,endDate).size();
    }

    public static int getNDifConsLastMonth (String uploader){
        Long now = System.currentTimeMillis()/1000;
        List<File> files = getFilesPeriodByUploader(uploader,now - secondsInAMonth,now);
        List<String> consumers = new ArrayList<String>();

        for (File file: files) {
            if (!consumers.contains(file.getConsumer()))
                consumers.add(file.getConsumer());
        }
        return consumers.size();
    }

    public static int getNDifConsPeriod (String uploader,Long startDate, Long endDate){
        List<File> files = getFilesPeriodByUploader(uploader,startDate, endDate);
        List<String> consumers = new ArrayList<String>();

        for (File file: files) {
            if (!consumers.contains(file.getConsumer()))
                consumers.add(file.getConsumer());
        }
        return consumers.size();
    }

    public static void setViewFile(String fileId,String ip){
        ofy().transact(() -> {
            File file = getById(File.class, fileId);
            file.setIpView(ip);
            file.setViewedDate(new Date());
            saveDelayed(file);
        });
    }


    // startDate e endDate le voglio in secondi
    public static List<File> getFilesPeriodByUploader (String uploader, Long startDate, Long endDate){
        List<File> filebByUploader = getFilesByUploader(uploader);
        List<File> toRemove = new ArrayList<File>();

        for (File file: filebByUploader){
            if (!isFileBetween(file,startDate,endDate))
                toRemove.add(file);
        }
        filebByUploader.removeAll(toRemove);
        return filebByUploader;
    }

    // startDate e endDate le volgio in secondi
    public static boolean isFileBetween (File file, Long startDate, Long endDate) {
        Long uploadMs = file.getUploadDate().getTime() / 1000;
        if (uploadMs > startDate && uploadMs < endDate)
            return true;
        else return false;
    }

    public static List<String> getTagsByFiles (List<FileProxy> files){
        List<String> tags = new ArrayList<>();
        for (FileProxy f:files) {
            String[] values = f.getHashTags().split(",");
            for (int i = 0; i < values.length; i++){
                if (!tags.contains(values[i]))
                    tags.add(values[i]);
            }
        }
        return tags;
    }
}
