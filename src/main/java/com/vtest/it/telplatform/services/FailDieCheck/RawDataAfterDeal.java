package com.vtest.it.telplatform.services.FailDieCheck;


import com.vtest.it.telplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.telplatform.pojo.vtptmt.BinWaferInforBean;

public interface RawDataAfterDeal {
    void deal(RawdataInitBean rawdataInitBean, BinWaferInforBean binWaferInforBean);
}
