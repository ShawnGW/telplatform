package com.vtest.it.telplatform.advisor;

import com.vtest.it.telplatform.pojo.vtptmt.DataParseIssueBean;
import com.vtest.it.telplatform.services.rawdataCheck.GetIssueBean;
import com.vtest.it.telplatform.services.vtptmt.impl.VtptmtInforImpl;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

@Component
@Aspect
public class CheckException {

    @Autowired
    private GetIssueBean getIssueBean;
    @Autowired
    private VtptmtInforImpl vtptmtInfor;

    @AfterThrowing(value = "execution(* com.vtest.it.telplatform.services.rawdataCheck.RawDataCheck.check(..))&&args(rawdata,..))")
    public void exceptionOccur(File rawdata) {

        String waferId = null;
        String lotId = null;
        try {
            FileReader reader = new FileReader(rawdata);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String content = null;
            while ((content = bufferedReader.readLine()) != null) {
                if (content.startsWith("Lot ID")) {
                    lotId = content.split(":")[1].trim();
                    continue;
                }
                if (content.startsWith("Wafer ID")) {
                    waferId = content.split(":")[1].trim();
                    continue;
                }
            }
            DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBeanForException(5, "there are error in file coding", rawdata, waferId, lotId);
            ArrayList<DataParseIssueBean> list = new ArrayList<>();
            list.add(dataParseIssueBean);
            vtptmtInfor.dataErrorsRecord(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
