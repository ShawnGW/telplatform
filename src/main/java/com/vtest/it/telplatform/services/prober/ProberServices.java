package com.vtest.it.telplatform.services.prober;


import com.vtest.it.telplatform.pojo.vtptmt.BinWaferInforBean;

import java.util.ArrayList;
import java.util.HashMap;

public interface ProberServices {
    public int insertSiteInforToBinInfoSummary(String customerCode, String device, String lot, String cp, String waferId, HashMap<Integer, HashMap<Integer, Integer>> siteMap, String testType, ArrayList<Integer> passBins);

    public int deleteSiteInforToBinInfoSummary(String customerCode, String device, String lot, String cp, String waferId);

    public int insertWaferInforToBinWaferSummary(BinWaferInforBean binWaferInforBean);
}
