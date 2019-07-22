package com.vtest.it.telplatform.services.vtptmt.impl;


import com.vtest.it.telplatform.dao.vtptmt.VtptmtDao;
import com.vtest.it.telplatform.pojo.mes.MesProperties;
import com.vtest.it.telplatform.pojo.vtptmt.BinWaferInforBean;
import com.vtest.it.telplatform.pojo.vtptmt.CheckItemBean;
import com.vtest.it.telplatform.pojo.vtptmt.DataInforToMesBean;
import com.vtest.it.telplatform.pojo.vtptmt.DataParseIssueBean;
import com.vtest.it.telplatform.services.vtptmt.VtptmtInfor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional(transactionManager = "vtptmtTransactionManager", isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
public class VtptmtInforImpl implements VtptmtInfor {
    @Autowired
    private VtptmtDao vtptmtDao;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public int dataErrorsRecord(ArrayList<DataParseIssueBean> list) {
        for (DataParseIssueBean bean : list) {
            if (null == bean.getCustomCode()) {
                bean.setCustomCode("NA");
            }
            if (null == bean.getDevice()) {
                bean.setDevice("NA");
            }
        }
        return vtptmtDao.dataErrorsRecord(list);
    }

    @Override
    @Cacheable(cacheNames = {"SystemPropertiesCache"}, key = "'tel&'+#root.methodName")
    public ArrayList<CheckItemBean> getCheckItemList() {
        return vtptmtDao.getCheckItemList();
    }

    @Override
    @Cacheable(cacheNames = {"SystemPropertiesCache"}, key = "'tel&'+#root.methodName")
    public ArrayList<DataInforToMesBean> getList() {
        return vtptmtDao.getList();
    }

    @Override
    @Cacheable(cacheNames = {"SystemPropertiesCache"}, key = "'tel&'+#root.methodName")
    public ArrayList<BinWaferInforBean> getTesterStatus() {
        return vtptmtDao.getTesterStatus();
    }

    @Override
    @Cacheable(cacheNames = {"SystemPropertiesCache"}, key = "'tel&'+#root.methodName+'&'+#tester")
    public BinWaferInforBean getTesterStatusSingle(String tester) {
        return vtptmtDao.getTesterStatusSingle(tester);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public int insertWaferInforToBinWaferSummary(BinWaferInforBean binWaferInforBean) {
        return vtptmtDao.insertWaferInforToBinWaferSummary(binWaferInforBean);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public void waferFailTypeCheckOthers(String waferId, String cpProcess, String tester) {
        vtptmtDao.waferFailTypeCheckOthers(waferId, cpProcess, tester);
    }

    @Override
    @Cacheable(cacheNames = {"SystemPropertiesCache"}, key = "'tel&'+#root.methodName")
    public MesProperties getProperties() {
        return vtptmtDao.getProperties();
    }

    @Override
    @CacheEvict(cacheNames = {"SystemPropertiesCache"}, key = "'tel&getProperties'")
    public int updateProperties(MesProperties mesProperties) {
        return vtptmtDao.updateProperties(mesProperties);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    @Caching(evict = {
            @CacheEvict(cacheNames = {"SystemPropertiesCache"}, key = "'tel&getTesterStatusSingle&'+#tester"),
            @CacheEvict(cacheNames = {"SystemPropertiesCache"}, key = "'tel&getTesterStatus'")
    })
    public void singleWaferDeal(BinWaferInforBean binWaferInforBean, String waferId, String cpProcess, String tester) {
        vtptmtDao.insertWaferInforToBinWaferSummary(binWaferInforBean);
        vtptmtDao.waferFailTypeCheckOthers(waferId, cpProcess, tester);
    }
}
