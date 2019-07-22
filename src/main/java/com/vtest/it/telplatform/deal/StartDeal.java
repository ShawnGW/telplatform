package com.vtest.it.telplatform.deal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class StartDeal {
    @Autowired
    private TelPlatformDataDeal telPlatformDataDeal;
    @Scheduled(fixedDelay = 1000*5)
    public void deal(){
        telPlatformDataDeal.deal(null);
    }
}
