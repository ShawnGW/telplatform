package com.vtest.it.telplatform.deal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class StartDeal {
    @Autowired
    private TelPlatformDataDeal telPlatformDataDeal;
    @Scheduled(fixedDelay = 1000*60)
    public void deal(){

    }
}
