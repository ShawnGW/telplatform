package com.vtest.it.telplatform.deal;


import com.vtest.it.telplatform.pojo.mes.MesConfigBean;
import com.vtest.it.telplatform.pojo.rawdataBean.DealWaferIdInformationBean;
import com.vtest.it.telplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.telplatform.services.mes.impl.GetMesInforImpl;
import com.vtest.it.telplatform.services.probermappingparsetools.TelProberMappingNormalParse;
import com.vtest.it.telplatform.services.probermappingparsetools.TelProberMappingSmallDieParse;
import com.vtest.it.telplatform.services.rawdatatools.InitMesConfigToRawdataProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenerateRawdataInitInformation {

    @Autowired
    private InitMesConfigToRawdataProperties initMesConfigToRawdataProperties;
    @Autowired
    private GetMesInforImpl getMesInfor;
    @Autowired
    private TelProberMappingNormalParse telProberMappingNormalParse;
    @Autowired
    private TelProberMappingSmallDieParse telProberMappingSmallDieParse;
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateRawdataInitInformation.class);
    public RawdataInitBean generateRawdata(DealWaferIdInformationBean bean) throws Exception {
        RawdataInitBean rawdataInitBean = bean.getRawdataInitBean();
        try {
            LOGGER.error(bean.getFile().getName() + " size: " + bean.getFile().length() + " time diff: " + (System.currentTimeMillis() - bean.getFile().lastModified()) / 1000);
            if (bean.isNormalDieFlag()){
                telProberMappingNormalParse.Get(bean.getFile(),rawdataInitBean);
            }else {
                telProberMappingSmallDieParse.Get(bean.getFile(),rawdataInitBean);
            }
        } catch (Exception e) {
            throw new Exception("there are error in file coding");
        }
        MesConfigBean mesConfigBean = getMesInfor.getWaferConfigFromMes(rawdataInitBean.getDataProperties().get("Wafer ID"), rawdataInitBean.getDataProperties().get("CP Process"));
        if (null == mesConfigBean.getInnerLot()) {
            throw new Exception("can't find this wafer in mes system : no such wafer or cpProcess");
        }
        initMesConfigToRawdataProperties.initMesConfig(rawdataInitBean, mesConfigBean);
        return rawdataInitBean;
    }
}
