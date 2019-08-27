package com.vtest.it.telplatform.services.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;

@Service
public class FileTimeCheck {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileTimeCheck.class);
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    public boolean fileTimeCheck(File file){
        long now=System.currentTimeMillis();
        LOGGER.error(file.getName() + " time now: " + FORMAT.format(now));
        File[] datas=file.listFiles();
        for (File data : datas) {
            long fileLastModifyTime=data.lastModified();
            LOGGER.error(file.getName() + " & " + data.getName() + " last modify time: " + FORMAT.format(fileLastModifyTime));
            if (((now - fileLastModifyTime) / 1000) < 300) {
                LOGGER.error(file.getName() + " result: " + false);
                return false;
            }
        }
        LOGGER.error(file.getName() + " result: " + true);
        return true;
    }
}
