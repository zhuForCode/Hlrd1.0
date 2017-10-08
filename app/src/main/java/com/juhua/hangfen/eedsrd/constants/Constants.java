package com.juhua.hangfen.eedsrd.constants;

import com.juhua.hangfen.eedsrd.tools.CryptoTools;

/**
 * Created by JiaJin Kuai on 2017/2/20.
 */

public interface Constants {
    short MODEL_TYPE_SINGLE = 0;//返回单条json字符串
    short MODEL_TYPE_LIST = 1;//返回多条json字符串
    short MODEL_TYPE_MULTIPLE = 2;//单条线程 同时访问多个webSevice接口
    String VERIFY = "+afJ7yNqhDL67Hbo7ib9HQ==";
    int REQUEST_TIMEOUT = 8000;

    String DOMAIN_NAME = "http://192.168.0.107/";
    String WEBSERVICE_URL = " /WebServers/AppSer.asmx";
    String NAME_SPACE = "http://tempuri.org/";
    String WSDL = "http://192.168.0.107/WebServers/AppSer.asmx";
    String METHOD_APP_LOGIN = "AppLoginForZjrd";
    String METHOD_GET_APP_VERSION = "GetWebAppVersion";
    String METHOD_GET_BACK_PASSWORD = "GetBackPassWord";
    int RESULT_CONTACT_CONFIRM = 10;
}
