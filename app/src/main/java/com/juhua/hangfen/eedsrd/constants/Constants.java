package com.juhua.hangfen.eedsrd.constants;

import com.juhua.hangfen.eedsrd.tools.CryptoTools;

/**
 * Created by JiaJin Kuai on 2017/2/20.
 */

public interface Constants {
    short MODEL_TYPE_SINGLE = 0;//返回单条json字符串
    short MODEL_TYPE_LIST = 1;//返回多条json字符串
    short MODEL_TYPE_MULTIPLE = 2;//单条线程 同时访问多个webSevice接口
    String VERIFY = "2014ZjrdVerify";
    int Mail_LIST_SIZE = 10;
    int UPDATE_USER_REQUESTCODE = 2;
    int CODE_FROM_LOGIN = 2;
    int CODE_FROM_PERSONAL = 3;
    String BASE_URL = "http://zjrd.juhua.com.cn/WebServices/AppServ.asmx";
    String METHOD_GET_ACCECPT_MAIL_LIST = "GetMailListNew";
    String METHOD_GET_SENT_MAIL_LIST = "GetMailSendListNoCacheNew";
    String METHOD_GET_FAVARITE_MAIL_LIST = "GetMailScListNoCacheNew";
    String METHOD_GET_USER_INFO = "GetUserInfo";
    String METHOD_GET_UNREAD_MAILNUM = "GetWDMailCount";
    String METHOD_USER_LOGIN = "FindUserNew";
    String METHOD_SEARCH_MUSIC = "baidu.ting.search.catalogSug";
    String METHOD_LRC = "baidu.ting.song.lry";
    String PARAM_METHOD = "method";
    String PARAM_TYPE = "os";
    String PARAM_SIZE = "PageSize";
    String PARAM_INDEX = "PageIndex";
    String PARAM_OFFSET = "offset";
    String PARAM_USER_ID = "UserId";
    String PARAM_USERROLE_ID = "urid";
    String PARAM_VERIFY = "verify";
    String PARAM_OLDAREA_ID = "oldareaid";
    String PARAM_QUERY = "title";//查询

    String DOMAIN_NAME = "http://202.96.113.83:808";
    String WEBSERVICE_URL = " /WebServers/AppSer.asmx";
    String NAME_SPACE = "http://tempuri.org/";
    String WSDL = "http://202.96.113.83:808/WebServers/AppSer.asmx?WSDL";
    String METHOD_APP_LOGIN = "AppLoginForZjrd";
    String METHOD_GET_APP_VERSION = "GetWebAppVersion";
    String METHOD_GET_BACK_PASSWORD = "GetBackPassWord";
}
