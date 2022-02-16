package com.yusys.bione.frame.user.service;

import cn.com.crc.esb.sysncuser.webservice.ISyncUserWebService;
import cn.com.crc.esb.sysncuser.webservice.ReturnData;
import cn.com.crc.esb.sysncuser.webservice.SyncUserInfo;

public interface ISyncUserWebAfterService extends ISyncUserWebService {
    public abstract boolean isAuth();

    public abstract ReturnData createAccount(SyncUserInfo var1);

    public abstract ReturnData editAccount(SyncUserInfo var1);

    public abstract ReturnData disableAccount(SyncUserInfo var1);

    public abstract ReturnData enableAccount(SyncUserInfo var1);

    public abstract ReturnData deleteAccount(SyncUserInfo var1);
}
