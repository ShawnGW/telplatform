package com.vtest.it.telplatform.services.vtptmt;

import com.vtest.it.telplatform.pojo.mes.MesProperties;
import com.vtest.it.telplatform.pojo.vtptmt.BinWaferInforBean;
import com.vtest.it.telplatform.pojo.vtptmt.CheckItemBean;
import com.vtest.it.telplatform.pojo.vtptmt.DataInforToMesBean;
import com.vtest.it.telplatform.pojo.vtptmt.DataParseIssueBean;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

public interface VtptmtInfor {
    public int dataErrorsRecord(@Param("list") ArrayList<DataParseIssueBean> list);

    public ArrayList<CheckItemBean> getCheckItemList();

    public ArrayList<DataInforToMesBean> getList();

    public ArrayList<BinWaferInforBean> getTesterStatus();

    public BinWaferInforBean getTesterStatusSingle(String tester);

    public int insertWaferInforToBinWaferSummary(BinWaferInforBean binWaferInforBean);

    public void waferFailTypeCheckOthers(@Param("waferId") String waferId, @Param("cp") String cpProcess, @Param("tester") String tester);

    public MesProperties getProperties();

    public int updateProperties(MesProperties mesProperties);
}
