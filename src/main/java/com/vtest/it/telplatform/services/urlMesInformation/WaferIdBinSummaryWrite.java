package com.vtest.it.telplatform.services.urlMesInformation;

import com.vtest.it.telplatform.pojo.rawdataBean.RawdataInitBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

@Service
public class WaferIdBinSummaryWrite {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaferIdBinSummaryWrite.class);

    private WaferidInforIntoMes waferidInforIntoMes;

    @Autowired
    public void setWaferidInforIntoMes(WaferidInforIntoMes waferidInforIntoMes) {
        this.waferidInforIntoMes = waferidInforIntoMes;
    }

    public void write(RawdataInitBean rawdataInitBean) {
        String lot = rawdataInitBean.getProperties().get("Lot ID");
        String cp = rawdataInitBean.getProperties().get("CP Process");
        String waferId = rawdataInitBean.getProperties().get("Wafer ID");

        HashMap<Integer, HashMap<Integer, Integer>> siteBinSmmary = rawdataInitBean.getSiteBinSum();
        TreeMap<Integer, Integer> binSummary = new TreeMap<>();
        Set<Integer> siteSet = siteBinSmmary.keySet();
        for (Integer site : siteSet) {
            HashMap<Integer, Integer> binMap = siteBinSmmary.get(site);
            Set<Integer> binSet = binMap.keySet();
            for (Integer bin : binSet) {
                if (binSummary.containsKey(bin)) {
                    binSummary.put(bin, binSummary.get(bin) + binMap.get(bin));
                } else {
                    binSummary.put(bin, binMap.get(bin));
                }
            }
        }
        StringBuffer SB = new StringBuffer();
        Set<Integer> set = binSummary.keySet();
        for (Integer bin : set) {
            SB.append("|Bin" + bin + ":" + binSummary.get(bin));
        }
        String startTime = rawdataInitBean.getProperties().get("Test Start Time");
        String endTime = rawdataInitBean.getProperties().get("Test End Time");
        startTime = startTime.length() > 14 ? startTime.substring(0, 14) : startTime;
        endTime = endTime.length() > 14 ? endTime.substring(0, 14) : endTime;
        SB.append("|TestStart:" + startTime);
        SB.append("|TestEnd:" + endTime);
        String summary = SB.toString();
        LOGGER.info("Bin summary to mes:" + lot + "&" + waferId + "&" + cp + "&" + summary);
        waferidInforIntoMes.write(lot, waferId, cp, summary);
        LOGGER.info("Bin summary to mes successfully");
    }
}
