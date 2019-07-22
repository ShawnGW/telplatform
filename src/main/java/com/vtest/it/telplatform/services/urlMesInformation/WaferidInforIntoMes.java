package com.vtest.it.telplatform.services.urlMesInformation;

import com.vtest.it.telplatform.pojo.mes.MesProperties;
import com.vtest.it.telplatform.services.vtptmt.impl.VtptmtInforImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * bin summary into MES
 *
 * @author shawn.sun
 * @version 2.0
 * @category IT
 * @since 2018.3.15
 */
@Service
public class WaferidInforIntoMes {
    @Autowired
    private VtptmtInforImpl vtptmtInfor;
    @Autowired
    private GetStreamFromMes getStreamFromMes;

    public void write(String lotNumber, String waferID, String CP, String BinSummary) {
        try {
            MesProperties properties = vtptmtInfor.getProperties();
            String URL = "?Action=UploadBinSummaryPerWafer&ACode=";
            int RandomNumber = (int) ((Math.random() * 100000000) / 1);
            URL = URL + properties.getAcode() + "&ItemName=WaferLot:" + lotNumber + "|WaferID:" + waferID + "|CP:" + CP.trim() + BinSummary + "&rand=" + RandomNumber;
            getStreamFromMes.getStream(URL);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
