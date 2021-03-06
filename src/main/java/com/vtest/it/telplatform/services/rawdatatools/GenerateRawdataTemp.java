package com.vtest.it.telplatform.services.rawdatatools;

import com.vtest.it.telplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.telplatform.pojo.vtptmt.DataParseIssueBean;
import com.vtest.it.telplatform.services.rawdataCheck.RawDataCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;

@Service
public class GenerateRawdataTemp {
    @Autowired
    private GenerateRawdataFinal generateRawdataFinal;
    @Autowired
    private GenerateRawdata generateRawdata;
    @Autowired
    private RawDataCheck rawDataCheck;

    public boolean generateTempRawdata(RawdataInitBean rawdataInitBean, ArrayList<DataParseIssueBean> dataParseIssueBeans) throws Exception {
        File tempRawdata = generateRawdata.generate(rawdataInitBean);
        boolean checkFlag = rawDataCheck.check(tempRawdata, dataParseIssueBeans);
        if (!checkFlag) {
            return false;
        }
        generateRawdataFinal.generateFinalRawdata(tempRawdata, rawdataInitBean);
        return true;
    }
}
