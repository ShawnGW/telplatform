package com.vtest.it.telplatform.pojo.rawdataBean;

import lombok.Data;

import java.io.File;

@Data
public class DealWaferIdInformationBean {
    private RawdataInitBean rawdataInitBean;
    private boolean normalDieFlag;
    private File file;
}
