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

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author shawn.sun
 * @date 2020/04/26 14:35
 */
@Service
public class GenerateRawdataInitInformation {
    private static final String CHROMAMODEL = "Chroma";
    private static final String CHROMAMODEL_OTHER = "Chroma-3380P";
    Pattern pattern = Pattern.compile(";");
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
        MesConfigBean mesConfigBean = getMesInfor.getWaferConfigFromMes(rawdataInitBean.getDataProperties().get("Wafer ID"), rawdataInitBean.getDataProperties().get("CP Process"));
        if (null == mesConfigBean.getInnerLot()) {
            throw new Exception("can't find this wafer in mes system : no such wafer or cpProcess");
        }
        try {
            LOGGER.error(bean.getFile().getName() + " size: " + bean.getFile().length() + " time diff: " + (System.currentTimeMillis() - bean.getFile().lastModified()) / 1000);
            if (bean.isNormalDieFlag()){
                telProberMappingNormalParse.Get(bean.getFile(),rawdataInitBean);
            }else {
                String testerModelConfig = mesConfigBean.getTesterModel().trim();
                String currentProcessTesterModel = "others";
                if (CHROMAMODEL.equals(testerModelConfig) || CHROMAMODEL_OTHER.equals(testerModelConfig)) {
                    currentProcessTesterModel = CHROMAMODEL;
                } else if (testerModelConfig.contains(CHROMAMODEL)) {
                    try {
                        Optional<String> optional = pattern.splitAsStream(testerModelConfig).filter(x -> x.contains(rawdataInitBean.getDataProperties().get("CP Process"))).map(x -> x.split(":")[1]).findFirst();
                        currentProcessTesterModel = optional.get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (CHROMAMODEL.equals(currentProcessTesterModel)) {
                    telProberMappingSmallDieParse.Get(bean.getFile(), rawdataInitBean, false);
                } else {
                    telProberMappingSmallDieParse.Get(bean.getFile(), rawdataInitBean, true);
                }

            }
        } catch (Exception e) {
            throw new Exception("there are error in file coding");
        }
        initMesConfigToRawdataProperties.initMesConfig(rawdataInitBean, mesConfigBean);
        return rawdataInitBean;
    }
}
