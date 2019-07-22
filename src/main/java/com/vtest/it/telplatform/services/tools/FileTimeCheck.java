package com.vtest.it.telplatform.services.tools;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileTimeCheck {
    public boolean fileTimeCheck(File file){
        long now=System.currentTimeMillis();
        File[] datas=file.listFiles();
        for (File data : datas) {
            long fileLastModifyTime=data.lastModified();
            if (((now-fileLastModifyTime)/1000)<60){
                return false;
            }
        }
        return true;
    }
}
