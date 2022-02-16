package com.yusys.bione.comp.bigdata.utils;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.security.UserGroupInformation;


public class HBaseConfig {

	private static class HbaseConnectionHolder {

		private static Configuration conf = HBaseConfiguration.create();

		private HbaseConnectionHolder() {
		}

		// static {
		// try {
		// String confPath =
		// HBaseConfig.class.getClassLoader().getResource("").toURI().getPath();
		// ResourceBundle rb = ResourceBundle.getBundle("bip",
		// Locale.getDefault());
		//
		// conf.set("hadoop.security.authentication", "kerberos");
		// System.setProperty("java.security.krb5.conf",confPath +
		// rb.getString("java.security.krb5.conf"));
		// if(rb.containsKey("java.security.auth.login.config"))
		// System.setProperty("java.security.auth.login.config",confPath +
		// rb.getString("java.security.auth.login.config"));
		//
		// UserGroupInformation.setConfiguration(HbaseConnectionHolder.conf);
		// UserGroupInformation.loginUserFromKeytab(rb.getString("username.client.kerberos.principal"),
		// confPath
		// + rb.getString("username.client.keytab.file"));
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
	}

	public HBaseConfig() {
	}

	public static Configuration getConf() {
		return HbaseConnectionHolder.conf;
	}

	public static void setConf(Configuration conf) {
		HbaseConnectionHolder.conf = conf;
	}

	public static void addResource(String name) {
		getConf().addResource(name);
	}

	public void initialize() throws IOException {
		try {
			ResourceBundle rb = ResourceBundle.getBundle("database",
					Locale.getDefault());
			
			String Kerberos = rb.getString("java.security.kerberos");
			if(StringUtils.isEmpty(Kerberos) || !StringUtils.equals(Kerberos, "true"))
				return;
			
			getConf().set("hbase.master.kerberos.principal",
					"hbase/_HOST@TDH");
			getConf().set("hbase.regionserver.kerberos.principal",
					"hbase/_HOST@TDH");
			getConf().set("hbase.security.authentication",
					"kerberos");

			String confPath = HBaseConfig.class.getClassLoader()
					.getResource("").toURI().getPath();

			getConf().set("hadoop.security.authentication",
					"kerberos");
			System.setProperty("java.security.krb5.conf",
					confPath + rb.getString("java.security.krb5.conf"));
			if (rb.containsKey("java.security.auth.login.config"))
				System.setProperty("java.security.auth.login.config", confPath
						+ rb.getString("java.security.auth.login.config"));

			UserGroupInformation.setConfiguration(HbaseConnectionHolder.conf);
			UserGroupInformation.loginUserFromKeytab(
					rb.getString("username.client.kerberos.principal"),
					confPath + rb.getString("username.client.keytab.file"));
			HBaseUtil.getResultScann("hbase:meta");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}