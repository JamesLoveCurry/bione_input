package com.yusys.bione.frame.base;
import java.util.Properties;


public class CryptSDK{
	
	private static CryptSDK sdk;
	private CryptSDK(){ 
		
	}
	
	public static CryptSDK getInstance(){
		if(sdk != null){
			return sdk;
		}
		else{
			sdk = new CryptSDK();
			return sdk;
		}
	}
	public void initSDK(String strClassPath){
		String jdkbit = System.getProperty("sun.arch.data.model");
		System.out.println (strClassPath);
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		System.out.println(os);
		if(os.startsWith("win") || os.startsWith("Win"))
		{
			if(jdkbit.equals("32"))
			{
				strClassPath = strClassPath + "/SDK/Windows/32/SDK_JNI.dll";
				//System.load("SDK_JNI.dll");
			} 
			else if(jdkbit.equals("64"))
			{
				strClassPath = strClassPath + "/SDK/Windows/64/SDK_JNI_X64.dll";
				//System.load("SDK_JNI_X64.dll");
			} 
		}
		else
		{
			if(jdkbit.equals("32"))
			{
				strClassPath = strClassPath + "/SDK/Linux/32/Native.cws_cebsdk.so";
				//System.load("/usr/lib/Native.cws_cebsdk.so");
			} 
			else if(jdkbit.equals("64"))
			{
				strClassPath = strClassPath + "/SDK/Linux/64/Native.cws_cebsdk.so";
				//System.load("/usr/lib64/Native.cws_cebsdk.so");
			} 
		}
		System.load(strClassPath);
	}
	public CryptSDK(String strClassPath){ 
		String jdkbit = System.getProperty("sun.arch.data.model");
		System.out.println (strClassPath);
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		System.out.println(os);
		if(os.startsWith("win") || os.startsWith("Win"))
		{
			if(jdkbit.equals("32"))
			{
				strClassPath = strClassPath + "/SDK/Windows/32/SDK_JNI.dll";
				//System.load("SDK_JNI.dll");
			} 
			else if(jdkbit.equals("64"))
			{
				strClassPath = strClassPath + "/SDK/Windows/64/SDK_JNI_X64.dll";
				//System.load("SDK_JNI_X64.dll");
			} 
		}
		else
		{
			if(jdkbit.equals("32"))
			{
				strClassPath = strClassPath + "/SDK/Linux/32/Native.cws_cebsdk.so";
				//System.load("/usr/lib/Native.cws_cebsdk.so");
			} 
			else if(jdkbit.equals("64"))
			{
				strClassPath = strClassPath + "/SDK/Linux/64/Native.cws_cebsdk.so";
				//System.load("/usr/lib64/Native.cws_cebsdk.so");
			} 
		}
		System.load(strClassPath);
	}
	
	/*@PreDestroy
	public void destroy(){
		this.Uninit();
	}*/
	public native int Init(String jstrInitInfo);
	
	public native int Uninit();
	
	public native int DecFile(String jstrSendMailInfo,String jstrOutMailInfo);
	
	public native int EncFile(String jstrSendMailInfo,String jstrOutMailInfo);
	
}
