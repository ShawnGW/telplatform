package com.vtest.it.telplatform.services.mes;


import com.vtest.it.telplatform.pojo.mes.CustomerCodeAndDeviceBean;
import com.vtest.it.telplatform.pojo.mes.MesConfigBean;
import com.vtest.it.telplatform.pojo.mes.SlotAndSequenceConfigBean;
import org.apache.ibatis.annotations.Param;

public interface GetMesInfor {
    public String getWaferIdBySlot(String lot, String slot);
    public SlotAndSequenceConfigBean getLotSlotConfig(String lot);
    public MesConfigBean getWaferConfigFromMes(String waferId, String cpProcess);
    public CustomerCodeAndDeviceBean getCustomerAndDeviceByLot(String lot);
    public String getCurrentCpStep(String waferId);
    public CustomerCodeAndDeviceBean getCustomerAndDeviceByWaferAndCpStep(String waferId, String cpStep);
}
