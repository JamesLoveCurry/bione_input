package com.yusys.bione.frame.user.test;

import com.yusys.bione.frame.user.service.SyncUserWebServiceImpl;

import javax.xml.ws.Endpoint;

/**
 * 使用Endpoint(终端)类发布webservice
 */
public class WebServicePublish {
    public static void main(String[] args) {
        String address = "http://127.0.0.1:8099/webservice/syncUserWebService";
//        Endpoint.publish("http://10.132.124.103:1510/u",new SyncUserWebServiceImpl());
        Endpoint.publish(address,new SyncUserWebServiceImpl());
        System.out.println("发布WebService成功");
    }

}
