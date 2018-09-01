package com.wolfpeng.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import com.wolfpeng.exception.MediaServerException;
import com.wolfpeng.model.MetadataDO;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Created by penghao on 2018/9/1.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public class CUEParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(CUEParser.class);

    private static Long getSecendsFromTime(String time) {
        String[] times = time.split(":");
        Long sec = 0L;
        Integer[] secConfig = {60000, 1000, 10};
        for (int i = 0; i < 3 && i < times.length; i++) {
            String t = times[i];
            sec += Integer.valueOf(t) * secConfig[i];
        }
        return sec;
    }

    public static CueFileBean parseCueFile(File cueFile) {
        LineNumberReader reader = null;

        MetadataDO metadataDO = new MetadataDO();

        List<MetadataDO> songs = new ArrayList<>();
        CueFileBean cueFileBean = new CueFileBean();
        boolean parseSong = false;
        int songIndex = 0;
        try {
            reader = new LineNumberReader( new InputStreamReader(new FileInputStream(cueFile),"UTF-8"));
            while (true) {
                String s = reader.readLine();
                if (s != null)
                {
                    if(!parseSong && s.trim().toUpperCase().startsWith("PERFORMER")){
                        metadataDO.setPerformer(s.substring(s.indexOf("\"")+1, s.lastIndexOf("\"")));
                    }
                    if(!parseSong && s.trim().toUpperCase().startsWith("TITLE")){
                        metadataDO.setAblum(s.substring(s.indexOf("\"")+1, s.lastIndexOf("\"")));
                    }
                    if(s.trim().toUpperCase().startsWith("FILE")){
                        cueFileBean.setFileName(s.substring(s.indexOf("\"")+1, s.lastIndexOf("\"")));
                    }
                    if(s.trim().toUpperCase().startsWith("TRACK")){
                        parseSong = true;
                        songIndex ++;
                    }
                    if(parseSong && s.trim().toUpperCase().startsWith("TITLE")){
                        metadataDO.setTitle(s.substring(s.indexOf("\"")+1, s.lastIndexOf("\"")));
                    }
                    if(parseSong && s.trim().toUpperCase().startsWith("PERFORMER")){
                        metadataDO.setPerformer(s.substring(s.indexOf("\"")+1, s.lastIndexOf("\"")));
                    }
                    if(songIndex == 1 && s.trim().toUpperCase().startsWith("INDEX")){
                        String time = s.trim().split(" 01 ")[1].trim();
                        metadataDO.setStartTime(getSecendsFromTime(time));
                    }
                    if(songIndex > 1 && s.trim().toUpperCase().startsWith("INDEX")){
                        if(s.trim().contains(" 00 ")){
                            MetadataDO prev = songs.get(songIndex - 2);
                            Long startTime = getSecendsFromTime(s.trim().split(" 00 ")[1].trim());
                            prev.setDuration(startTime - prev.getStartTime());
                        }
                        if(s.trim().contains(" 01 ")){
                            metadataDO.setStartTime(getSecendsFromTime(s.trim().split(" 01 ")[1].trim()));
                        }
                    }
                    if(songIndex >= 1 && s.trim().toUpperCase().startsWith("INDEX") && s.trim().contains(" 01 ")){
                        songs.add(metadataDO);
                        metadataDO = new MetadataDO();
                    }
                }else{
                    cueFileBean.setSongs(songs);
                    break;
                }
            }

        } catch (Exception e) {
            LOGGER.error("parser cue file error, msg = {}, e = {}", e.getMessage(), e);
        }finally{
            try{
                if(reader!=null ){
                    reader.close();
                }
            }
            catch(Exception e){
                LOGGER.error("close reader error, msg = {}, e = {}", e.getMessage(), e);
            }
        }

        return cueFileBean;
    }
    /**
     * cue bean
     * @author xuweilin
     *
     */
    @Data
    public static class CueFileBean{
        private String performer;
        private String albumName;
        private String fileName;
        private List<MetadataDO> songs = null;
    }
}
