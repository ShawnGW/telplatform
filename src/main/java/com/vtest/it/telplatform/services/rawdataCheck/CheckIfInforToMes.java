package com.vtest.it.telplatform.services.rawdataCheck;


import com.vtest.it.telplatform.pojo.vtptmt.DataInforToMesBean;
import com.vtest.it.telplatform.services.vtptmt.impl.VtptmtInforImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CheckIfInforToMes {
    @Autowired
    private VtptmtInforImpl vtptmtInfor;

    public boolean check(String customCode, String device) {
        ArrayList<DataInforToMesBean> allConfigs = vtptmtInfor.getList();
        for (DataInforToMesBean bean : allConfigs) {
            if (bean.getCustomCode().equals(customCode) && (bean.getDevice().equals("ALL") || bean.getDevice().equals(device))) {
                return true;
            }
        }
        return false;
    }
}
