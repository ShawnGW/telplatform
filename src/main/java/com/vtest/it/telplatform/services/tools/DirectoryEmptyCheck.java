package com.vtest.it.telplatform.services.tools;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class DirectoryEmptyCheck {
    public boolean directoryCheckAndDeal(File file) {
        if (file.isFile()) {
            try {
                FileUtils.forceDelete(file);
                return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (file.listFiles().length == 0) {
                try {
                    FileUtils.forceDelete(file);
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
