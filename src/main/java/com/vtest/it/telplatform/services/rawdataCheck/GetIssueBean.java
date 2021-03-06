package com.vtest.it.telplatform.services.rawdataCheck;

import com.vtest.it.telplatform.pojo.mes.CustomerCodeAndDeviceBean;
import com.vtest.it.telplatform.pojo.vtptmt.DataParseIssueBean;
import com.vtest.it.telplatform.services.mes.GetMesInfor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Service
public class GetIssueBean {
    @Value("${system.properties.tel.error-path}")
    private String errorPath;
    @Autowired
    private GetMesInfor getMesInfor;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    public DataParseIssueBean getDataBean(HashMap<String, String> waferInfor, int level, String descripth) {
        DataParseIssueBean dataParseIssueBean = new DataParseIssueBean();
        dataParseIssueBean.setCustomCode(waferInfor.get("customCode"));
        dataParseIssueBean.setDevice(waferInfor.get("device"));
        dataParseIssueBean.setLotId(waferInfor.get("lot"));
        dataParseIssueBean.setCpStep(waferInfor.get("cpStep"));
        dataParseIssueBean.setWaferNo(waferInfor.get("waferNo"));
        dataParseIssueBean.setResource(waferInfor.get("resource"));
        dataParseIssueBean.setIssueType("data Check");
        dataParseIssueBean.setIssuLevel(level);
        dataParseIssueBean.setIssuePath("na");
        dataParseIssueBean.setIssueDescription(descripth);
        dataParseIssueBean.setDealFlag(0);
        return dataParseIssueBean;
    }

    public DataParseIssueBean getDataBeanForException(int level, String description, File file, String waferId, String lot) throws IOException {
        String cpProcess = getMesInfor.getCurrentCpStep(waferId);
        CustomerCodeAndDeviceBean customerCodeAndDeviceBean = getMesInfor.getCustomerAndDeviceByWaferAndCpStep(waferId, cpProcess);
        HashMap<String, String> waferInfor = new HashMap<>();
        waferInfor.put("customCode", customerCodeAndDeviceBean.getCustomerCode());
        waferInfor.put("device", customerCodeAndDeviceBean.getDevice());
        waferInfor.put("lot", lot);
        waferInfor.put("cpStep", cpProcess);
        waferInfor.put("waferNo", waferId);
        waferInfor.put("resource", "TEL");
        DataParseIssueBean dataParseIssueBean = getDataBean(waferInfor, level, description);
        if (description.equals("there are error in file coding")) {
            dataParseIssueBean.setIssueType("mapping parse");
        } else {
            dataParseIssueBean.setIssueType("mes information");
        }
        String date = getDateString();
        dataParseIssueBean.setIssuePath(errorPath + "mappingParseError/" + lot + "/" + file.getName());
        FileUtils.copyFile(file, new File(errorPath + "mappingParseError/" + lot + "/" + file.getName()));
        return dataParseIssueBean;
    }

    public String getDateString() {
        return simpleDateFormat.format(new Date());
    }
}
