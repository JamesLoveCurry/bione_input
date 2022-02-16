package com.yusys.bione.plugin.yuformat.utils;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;

/**
 * 调用 Shell 脚本  执行器
 * 远程访问主机  
 */
public class RemoteShellExecutor {

    private final Log logger = LogFactory.getLog(RemoteShellExecutor.class);

    /**
     * SSH 登录 认证
     * @return
     * @throws IOException
     */
    private Connection login(String ip, String userNm, String passWd){
    	Connection conn = new Connection(ip);
        try {
			conn.connect();
			boolean isLogin = conn.authenticateWithPassword(userNm, passWd);
			if(!isLogin){
				conn = null;
			}
		} catch (IOException e) {
			if (conn != null) {
                conn.close();
            }
			conn = null;
			e.printStackTrace();
		}
        
        return conn;
    }

    /**
     * 远程访问主机  调用 Shell 脚本
     * SSH 登录 认证
     * @param shell
     * @return
     * @throws Exception
     */
    public int exec(String sshIp, String sshUserNm, String sshPassWd, String shell) throws Exception {
        int ret = -1;
        //SSH 登录认证
        Connection conn = this.login(sshIp, sshUserNm, sshPassWd);
        try {
            if (conn != null) {
            	Session session = conn.openSession();
                session.execCommand(shell);
                ret = 0;
                //不需要返回结果
                session.waitForCondition(ChannelCondition.EXIT_STATUS, 36000);
                ret = session.getExitStatus();
            }else{
            	logger.info("调用远程服务器失败," + sshIp);
            }
        } finally {
        	if (conn != null) {
                conn.close();
            }
        }
        return ret;
    }
    
    /**
	 * 远程访问主机  调用 Shell 脚本
	 * ssh免登录
	 * 暂未进行  多Server 高可用 部署优化 
	 * @param taskCmds
	 * @return
	 * @throws Exception
	 */
    public int exec(RemoteShellPropertyVO sPVO, String shell) throws Exception {
    	
        try{
            java.util.Properties config = new java.util.Properties(); 
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            com.jcraft.jsch.Session session=jsch.getSession(sPVO.getSshUserName(), sPVO.getSshIp(), 22);
            jsch.setKnownHosts(sPVO.getKnownHosts());
            jsch.addIdentity(sPVO.getIdRsa());
            session.setConfig(config);
            session.connect();
            
            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(shell);
            channel.connect();
            System.out.println("免密登陆成功！IP:"+sPVO.getSshIp());
            
            channel.disconnect();
            session.disconnect();
        }catch(Exception e){
            System.out.println("免密登陆失败！IP:"+sPVO.getSshIp());
            e.printStackTrace();
        }
        
        return 0;
    }
}