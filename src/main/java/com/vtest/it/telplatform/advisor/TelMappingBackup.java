package com.vtest.it.telplatform.advisor;

import com.vtest.it.telplatform.pojo.mes.CustomerCodeAndDeviceBean;
import com.vtest.it.telplatform.pojo.mes.SlotAndSequenceConfigBean;
import com.vtest.it.telplatform.pojo.rawdataBean.DealWaferIdInformationBean;
import com.vtest.it.telplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.telplatform.services.mes.impl.GetMesInforImpl;
import com.vtest.it.telplatform.services.probermappingparsetools.TelOpusProberLotDaParse;
import com.vtest.it.telplatform.services.probermappingparsetools.TelOpusProberLotDatParse;
import com.vtest.it.telplatform.services.probermappingparsetools.TelOpusProberMappingDaParse;
import com.vtest.it.telplatform.services.tools.FileTimeCheck;
import com.vtest.it.telplatform.services.tools.PerfectCopy;
import org.apache.commons.io.FileUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Aspect
@Component
public class TelMappingBackup {
    @Value("${system.properties.tel.mapup}")
    private String source;
    @Value("${system.properties.tel.mapdown}")
    private String mapdown;
    @Value("${system.properties.tel.backup-path}")
    private String backupPath;
    @Autowired
    private FileTimeCheck fileTimeCheck;
    @Autowired
    private GetMesInforImpl getMesInfor;
    @Autowired
    private PerfectCopy perfectCopy;
    @Autowired
    private TelOpusProberLotDaParse telOpusProberLotDaParse;
    @Autowired
    private TelOpusProberLotDatParse telOpusProberLotDatParse;
    @Autowired
    private TelOpusProberMappingDaParse telOpusProberMappingDaParse;
    private static final Logger LOGGER = LoggerFactory.getLogger(TelMappingBackup.class);
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private SimpleDateFormat simpleDateFormatOthers = new SimpleDateFormat("yyMMddHHmm");

    @Around("execution(* com.vtest.it.telplatform.deal.TelPlatformDataDeal.deal(..))")
    public void MappingDeal(ProceedingJoinPoint proceedingJoinPoint) {
        ArrayList<DealWaferIdInformationBean> dealWaferIdInformationBeanArrayList = getDealList();
        try {
            proceedingJoinPoint.proceed(new Object[]{dealWaferIdInformationBeanArrayList});
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public ArrayList<DealWaferIdInformationBean> getDealList() {
        ArrayList<DealWaferIdInformationBean> dealWaferIdInformationBeanArrayList = new ArrayList<>();
        Map<File, Boolean> fileNeedDealMap = checkEmpty();
        for (Map.Entry<File, Boolean> entry : fileNeedDealMap.entrySet()) {
            // if the flag is true, it means should use normal-die parser
            if (entry.getValue()) {
                normalDieBackup(entry.getKey(), dealWaferIdInformationBeanArrayList);
            } else {
                smallDieBackup(entry.getKey(), dealWaferIdInformationBeanArrayList);
            }
        }
        return dealWaferIdInformationBeanArrayList;
    }

    public void smallDieBackup(File file, ArrayList<DealWaferIdInformationBean> dealWaferIdInformationBeanArrayList) {
        String lotName = file.getName();
        CustomerCodeAndDeviceBean customerCodeAndDeviceBean = getMesInfor.getCustomerAndDeviceByLot(lotName);
        String customerCode = customerCodeAndDeviceBean.getCustomerCode();
        String device = customerCodeAndDeviceBean.getDevice();
        File[] files = file.listFiles();
        File waferContFile = null;
        File lot1File = null;
        File lot2File = null;
        File formFile = null;
        ArrayList<File> mappingDaFileList = new ArrayList<>();
        ArrayList<File> mappingRmpFileList = new ArrayList<>();
        Set<String> mappingDaFileNameList = new HashSet<>();
        Set<String> mappingRmpFileNameList = new HashSet<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
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
            if (fileName.equals("FORM.DA")) {
                formFile = files[i];
                continue;
            }
            if (fileName.endsWith(".DA")) {
                mappingDaFileList.add(files[i]);
                mappingDaFileNameList.add(fileName);
                continue;
            }
            if (fileName.endsWith(".RMP")) {
                mappingRmpFileList.add(files[i]);
                mappingRmpFileNameList.add(fileName);
                continue;
            }
        }
        HashMap<String, String> resultMapLot2 = null;
        HashMap<String, String> resultMapLot1 = null;
        if (null != lot2File) {
            try {
                FileUtils.copyFile(lot2File, new File(mapdown + lotName + "/" + getFileNameAfterModify(lot2File.getName())));
                resultMapLot2 = telOpusProberLotDaParse.get(lot2File);
                String cpStep = resultMapLot2.get("cp");
                File destFile = new File(backupPath + customerCode + "/" + device + "/" + lotName + "/" + cpStep + "/LOT/" + lot2File.getName() + "_" + getDateString());
                FileUtils.copyFile(lot2File, destFile);
                FileUtils.forceDelete(lot2File);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != lot1File) {
                try {
                    resultMapLot1 = telOpusProberLotDaParse.get(lot1File);
                    String cpStep = resultMapLot1.get("cp");
                    File destFile = new File(backupPath + customerCode + "/" + device + "/" + lotName + "/" + cpStep + "/LOT/" + lot1File.getName() + "_" + getDateString());
                    FileUtils.copyFile(lot1File, destFile);
                    FileUtils.forceDelete(lot1File);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (null != lot1File) {
                try {
                    FileUtils.copyFile(lot1File, new File(mapdown + lotName + "/" + getFileNameAfterModify(lot1File.getName())));
                    resultMapLot1 = telOpusProberLotDatParse.Get(lot1File);
                    String cpStep = resultMapLot1.get("cp");
                    File destFile = new File(backupPath + customerCode + "/" + device + "/" + lotName + "/" + cpStep + "/LOT/" + lot1File.getName() + "_" + getDateString());
                    FileUtils.copyFile(lot1File, destFile);
                    FileUtils.forceDelete(lot1File);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (null != waferContFile) {
            String cpStep = "NA";
            try {
                cpStep = null == resultMapLot2 ? (null == resultMapLot1 ? cpStep : resultMapLot1.get("cp")) : (resultMapLot2.get("cp"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            File destFile = new File(backupPath + customerCode + "/" + device + "/" + lotName + "/" + cpStep + "/WAFCONT/" + waferContFile.getName() + "_" + getDateString());
            try {
                FileUtils.copyFile(waferContFile, destFile);
                FileUtils.forceDelete(waferContFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, String> waferIdCurrentCpStep = new HashMap<>();
        Map<String, Map<String, String>> waferIdTestTimeInformation = new HashMap<>();
        for (File daFile : mappingDaFileList) {
            try {
                Map<String, String> information = getFileInformaton(daFile.getName());
                if (information.get("time").equals("1")) {
                    if (mappingDaFileNameList.contains(information.get("waferId") + "2" + information.get("suffix"))) {
                        continue;
                    } else {
                        perfectCopy.copy(daFile, new File(mapdown + lotName + "/" + getFileNameAfterModify(daFile.getName())));
                    }
                } else {
                    perfectCopy.copy(daFile, new File(mapdown + lotName + "/" + getFileNameAfterModify(daFile.getName())));
                }
                String waferId = information.get("waferId");
                String currentCpStep = getMesInfor.getCurrentCpStep(waferId);
                waferIdCurrentCpStep.put(waferId, currentCpStep);
                HashMap<String, String> resultMap = telOpusProberMappingDaParse.GetResult(daFile);
                File destFile = new File(backupPath + customerCode + "/" + device + "/" + lotName + "/" + currentCpStep + "/" + daFile.getName() + "_" + getDateString());
                try {
                    FileUtils.copyFile(daFile, destFile);
                    FileUtils.forceDelete(daFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                waferIdTestTimeInformation.put(waferId, resultMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (File rmpFile : mappingRmpFileList) {
            DealWaferIdInformationBean dealWaferIdInformationBean = new DealWaferIdInformationBean();
            dealWaferIdInformationBean.setNormalDieFlag(false);
            Map<String, String> information = getFileInformaton(rmpFile.getName());
            boolean isFileLaterExist = false;
            if (information.get("time").equals("1")) {
                if (mappingRmpFileNameList.contains(information.get("waferId") + "2" + information.get("suffix"))) {
                    isFileLaterExist = true;
                    continue;
                } else {
                    perfectCopy.copy(rmpFile, new File(mapdown + lotName + "/" + getFileNameAfterModify(rmpFile.getName())));
                }
            } else {
                perfectCopy.copy(rmpFile, new File(mapdown + lotName + "/" + getFileNameAfterModify(rmpFile.getName())));
            }
            String waferId = information.get("waferId");
            String currentCpStep = waferIdCurrentCpStep.containsKey(waferId) ? waferIdCurrentCpStep.get(waferId) : getMesInfor.getCurrentCpStep(waferId);
            RawdataInitBean rawdataInitBean = new RawdataInitBean();

            String operator = "V888";
            if (information.get("time").equals("1") && null != lot1File) {
                operator = resultMapLot1.get("op");
            } else if (information.get("time").equals("2") && null != lot2File) {
                operator = resultMapLot2.get("op");
            }
            operator = operator.trim().equals("") ? "V888" : operator;
            File destFile = new File(backupPath + customerCode + "/" + device + "/" + lotName + "/" + currentCpStep + "/" + rmpFile.getName() + "_" + getDateString());
            try {
                FileUtils.copyFile(rmpFile, destFile);
                FileUtils.forceDelete(rmpFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!isFileLaterExist) {
                String testStartTime = waferIdTestTimeInformation.get(waferId).get("testStartTime");
                String testEndTime = waferIdTestTimeInformation.get(waferId).get("testEndTime");
                testStartTime = null == testStartTime ? simpleDateFormatOthers.format(new Date()) : testStartTime;
                testEndTime = null == testEndTime ? simpleDateFormatOthers.format(new Date()) : testEndTime;
                LinkedHashMap<String, String> dataProperties = new LinkedHashMap<>();
                dataProperties.put("Wafer ID", waferId);
                dataProperties.put("Operator", operator);
                dataProperties.put("CP Process", currentCpStep);
                dataProperties.put("Test Start Time", testStartTime);
                dataProperties.put("Test End Time", testEndTime);

                rawdataInitBean.setDataProperties(dataProperties);
                dealWaferIdInformationBean.setRawdataInitBean(rawdataInitBean);
                dealWaferIdInformationBean.setFile(destFile);
                dealWaferIdInformationBeanArrayList.add(dealWaferIdInformationBean);
            }
        }
    }

    public void normalDieBackup(File file, ArrayList<DealWaferIdInformationBean> dealWaferIdInformationBeanArrayList) {
        String lotName = file.getName();
        SlotAndSequenceConfigBean slotAndSequenceConfigBean = getMesInfor.getLotSlotConfig(lotName);
        boolean slotFlag = false;
        if (slotAndSequenceConfigBean.getReadType().toUpperCase().equals("SLOT")) {
            slotFlag = true;
        }
        CustomerCodeAndDeviceBean customerCodeAndDeviceBean = getMesInfor.getCustomerAndDeviceByLot(lotName);
        File[] files = file.listFiles();
        File waferContFile = null;
        File lot1File = null;
        File lot2File = null;
        ArrayList<File> mappingDatFileList = new ArrayList<>();
        Set<String> mappingDatFileNameList = new HashSet<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
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
            if (fileName.equals("LOT1.DAT")) {
                lot1File = files[i];
                continue;
            }
            if (fileName.equals("LOT2.DAT")) {
                lot2File = files[i];
                continue;
            }
            if (fileName.contains(".DAT")) {
                mappingDatFileList.add(files[i]);
                mappingDatFileNameList.add(fileName);
            }
        }
        HashMap<String, String> resultMapLot2 = null;
        HashMap<String, String> resultMapLot1 = null;
        if (null != lot2File) {
            try {
                FileUtils.copyFile(lot2File, new File(mapdown + lotName + "/" + getFileNameAfterModify(lot2File.getName())));
                resultMapLot2 = telOpusProberLotDatParse.Get(lot2File);
                String cpStep = resultMapLot2.get("cp");
                File destFile = new File(backupPath + customerCodeAndDeviceBean.getCustomerCode() + "/" + customerCodeAndDeviceBean.getDevice() + "/" + lotName + "/" + cpStep + "/LOT/" + lot2File.getName() + "_" + getDateString());
                FileUtils.copyFile(lot2File, destFile);
                FileUtils.forceDelete(lot2File);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != lot1File) {
                try {
                    resultMapLot1 = telOpusProberLotDatParse.Get(lot1File);
                    String cpStep = resultMapLot1.get("cp");
                    File destFile = new File(backupPath + customerCodeAndDeviceBean.getCustomerCode() + "/" + customerCodeAndDeviceBean.getDevice() + "/" + lotName + "/" + cpStep + "/LOT/" + lot1File.getName() + "_" + getDateString());
                    FileUtils.copyFile(lot1File, destFile);
                    FileUtils.forceDelete(lot1File);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (null != lot1File) {
                try {
                    FileUtils.copyFile(lot1File, new File(mapdown + lotName + "/" + getFileNameAfterModify(lot1File.getName())));
                    resultMapLot1 = telOpusProberLotDatParse.Get(lot1File);
                    String cpStep = resultMapLot1.get("cp");
                    File destFile = new File(backupPath + customerCodeAndDeviceBean.getCustomerCode() + "/" + customerCodeAndDeviceBean.getDevice() + "/" + lotName + "/" + cpStep + "/LOT/" + lot1File.getName() + "_" + getDateString());
                    FileUtils.copyFile(lot1File, destFile);
                    FileUtils.forceDelete(lot1File);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (null != waferContFile) {
            String cpStep = "NA";
            try {
                cpStep = null == resultMapLot2 ? (null == resultMapLot1 ? cpStep : resultMapLot1.get("cp")) : (resultMapLot2.get("cp"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            File destFile = new File(backupPath + customerCodeAndDeviceBean.getCustomerCode() + "/" + customerCodeAndDeviceBean.getDevice() + "/" + lotName + "/" + cpStep + "/WAFCONT/" + waferContFile.getName() + "_" + getDateString());
            try {
                FileUtils.copyFile(waferContFile, destFile);
                FileUtils.forceDelete(waferContFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (File datFile : mappingDatFileList) {
            DealWaferIdInformationBean dealWaferIdInformationBean = new DealWaferIdInformationBean();
            dealWaferIdInformationBean.setNormalDieFlag(true);
            Map<String, String> information = getFileInformaton(datFile.getName());
            boolean isFileLaterExist = false;
            if (information.get("time").equals("1")) {
                if (mappingDatFileNameList.contains(information.get("waferId") + "2" + information.get("suffix"))) {
                    isFileLaterExist = true;
                    continue;
                } else {
                    perfectCopy.copy(datFile, new File(mapdown + lotName + "/" + getFileNameAfterModify(datFile.getName())));
                }
            } else {
                perfectCopy.copy(datFile, new File(mapdown + lotName + "/" + getFileNameAfterModify(datFile.getName())));
            }
            String waferId = information.get("waferId");
            String rightWaferId = waferId;
            if (slotFlag) {
                if (waferId.contains("-")) {
                    try {
                        String tempWaferId = getMesInfor.getWaferIdBySlot(lotName, waferId.split("-")[1]);
                        rightWaferId = "NA".equals(tempWaferId) ? rightWaferId : tempWaferId;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        String tempWaferId = getMesInfor.getWaferIdBySlot(lotName, waferId);
                        rightWaferId = "NA".equals(tempWaferId) ? rightWaferId : tempWaferId;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            String currentCpStep = getMesInfor.getCurrentCpStep(rightWaferId);
            String operator = "V888";
            if (information.get("time").equals("1") && null != lot1File) {
                operator = resultMapLot1.get("op");
            } else if (information.get("time").equals("2") && null != lot2File) {
                operator = resultMapLot2.get("op");
            }
            operator = operator.trim().equals("") ? "V888" : operator;
            File destFile = new File(backupPath + customerCodeAndDeviceBean.getCustomerCode() + "/" + customerCodeAndDeviceBean.getDevice() + "/" + lotName + "/" + currentCpStep + "/" + datFile.getName() + "_" + getDateString());
            try {
                FileUtils.copyFile(datFile, destFile);
                FileUtils.forceDelete(datFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!isFileLaterExist) {
                RawdataInitBean rawdataInitBean = new RawdataInitBean();
                LinkedHashMap<String, String> dataProperties = new LinkedHashMap<>();
                dataProperties.put("Wafer ID", rightWaferId);
                dataProperties.put("Operator", operator);
                dataProperties.put("CP Process", currentCpStep);
                rawdataInitBean.setDataProperties(dataProperties);
                dealWaferIdInformationBean.setRawdataInitBean(rawdataInitBean);
                dealWaferIdInformationBean.setFile(destFile);
                dealWaferIdInformationBeanArrayList.add(dealWaferIdInformationBean);
            }
        }
    }

    public String getFileNameAfterModify(String fileName) {
        Map<String, String> information = getFileInformaton(fileName);
        return information.get("waferId") + "1" + information.get("suffix");
    }

    public Map<String, String> getFileInformaton(String mappingName) {
        Map<String, String> information = new HashMap<>();
        String waferId = mappingName.substring(0, mappingName.lastIndexOf(".") - 1);
        Integer time = Integer.valueOf(mappingName.substring(mappingName.lastIndexOf(".") - 1, mappingName.lastIndexOf(".")));
        String suffix = mappingName.substring(mappingName.lastIndexOf("."));
        information.put("waferId", waferId);
        information.put("time", String.valueOf(time));
        information.put("suffix", suffix);
        return information;
    }

    public String getDateString() {
        return simpleDateFormat.format(new Date());
    }

    public Map<File, Boolean> checkEmpty() {
        Map<File, Boolean> fileNeedDealMap = new HashMap<>();
        File[] lots = new File(source).listFiles();
        for (File lot : lots) {
            if (lot.isDirectory() && lot.listFiles().length > 0) {
                File[] files = lot.listFiles();
                for (File file : files) {
                    if (file.getName().endsWith(".DA") && fileTimeCheck.fileTimeCheck(lot)) {
                        fileNeedDealMap.put(lot, false);
                        break;
                    }
                }
                if (!fileNeedDealMap.containsKey(lot) && fileTimeCheck.fileTimeCheck(lot)) {
                    fileNeedDealMap.put(lot, true);
                }
            } else {
                try {
                    FileUtils.forceDelete(lot);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileNeedDealMap;
    }
}
