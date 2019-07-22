package com.vtest.it.telplatform.advisor;

import com.vtest.it.telplatform.pojo.rawdataBean.DealWaferIdInformationBean;
import com.vtest.it.telplatform.services.tools.DirectoryEmptyCheck;
import com.vtest.it.telplatform.services.tools.FileTimeCheck;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Aspect
@Component
public class TelMappingBackup {
    @Value("${system.properties.tel.mapup}")
    private String source;
    @Autowired
    private FileTimeCheck fileTimeCheck;
    @Autowired
    private DirectoryEmptyCheck directoryEmptyCheck;

    @Around("execution(* com.vtest.it.telplatform.deal.TelPlatformDataDeal.deal(..))")
    public void MappingDeal(ProceedingJoinPoint proceedingJoinPoint) {
        ArrayList<DealWaferIdInformationBean> dealWaferIdInformationBeanArrayList =new ArrayList<>();
        File[] lots = new File(source).listFiles();
        for (File lot : lots) {
            if (directoryEmptyCheck.directoryCheckAndDeal(lot) && fileTimeCheck.fileTimeCheck(lot)) {
                System.out.println(lot.getName());
            }
        }
        try {
            proceedingJoinPoint.proceed(new Object[]{dealWaferIdInformationBeanArrayList});
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void smallDieBackup(File file) {
        File[] files = file.listFiles();
        File waferContFile = null;
        File lot1File = null;
        File lot2File = null;
        File formFile=null;
        ArrayList<File> mappingDaFileList=new ArrayList<>();
        ArrayList<File> mappingRmpFileList=new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()){
                try {
                    FileUtils.forceDelete(files[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }
            String fileName = files[i].getName();
            if (fileName.equals("WAFCONT.DAT")) {
                waferContFile = files[i];
                continue;
            }
            if (fileName.equals("LOT1.DA")) {
                lot1File = files[i];
                continue;
            }
            if (fileName.equals("LOT2.DA")) {
                lot2File = files[i];
                continue;
            }
            if (fileName.equals("FORM.DA")){
                formFile=files[i];
                continue;
            }
            if (fileName.endsWith(".DA")){
                mappingDaFileList.add(files[i]);
                continue;
            }
            if (fileName.endsWith(".RMP")){
                mappingRmpFileList.add(files[i]);
                continue;
            }
        }

    }

    public void normalDieBackup(File file) {
        File[] files = file.listFiles();
        File waferContFile = null;
        File lot1File = null;
        File lot2File = null;
        ArrayList<File> mappingDatFileList=new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()){
                try {
                    FileUtils.forceDelete(files[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }
            String fileName=files[i].getName();
            if (fileName.equals("WAFCONT.DAT")){
                waferContFile=files[i];
                continue;
            }
            if (fileName.equals("LOT1.DAT")){
                lot1File=files[i];
                continue;
            }
            if (fileName.equals("LOT2.DAT")){
                lot2File=files[i];
                continue;
            }
            if (fileName.contains(".DAT")){
                mappingDatFileList.add(files[i]);
            }
        }

    }
    @Data
    private class WaferIdInformation{
        private File daFile;
        private File rmpFile;
    }
}
