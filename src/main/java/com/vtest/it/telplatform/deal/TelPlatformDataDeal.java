package com.vtest.it.telplatform.deal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TelPlatformDataDeal {
    @Value("${system.tel.properties.mapup}")
    private String source;
    public void deal() {

    }
}
