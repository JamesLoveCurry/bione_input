package com.yusys.bione.plugin.base.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yuchengtech.index.engine.to.BaseMsg;
import com.yuchengtech.index.engine.to.ResponseMsg;
import com.yusys.bione.plugin.valid.check.BizException;

/**
 * 客户端监听服务端返回结果的"回调类"，短连接使用
 *
 */
public class NettyClientHandlerByShort extends SimpleChannelInboundHandler<BaseMsg> {
	private static Logger logger = LoggerFactory.getLogger(NettyClientHandlerByShort.class);

	private Map<String, Object> responseMap = new HashMap<String, Object>();
	
	public NettyClientHandlerByShort(Map<String, Object> resMap) {
		super();
		this.responseMap = resMap;
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		logger.trace("◆Client成功创建了一个与DispatchServer端的管道，管道:" + ctx.channel() + "，当前线程:" + Thread.currentThread().getName());
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
		logger.trace("◆Client销毁了一个与DispatchServer端的管道，管道:" + ctx.channel() + "，当前线程:" + Thread.currentThread().getName());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		logger.trace("◆Client一个与DispatchServer端的管道有活动了，管道:" + ctx.channel() + "，当前线程:" + Thread.currentThread().getName());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		logger.trace("◆Client一个与DispatchServer端的管道不活动了，管道:" + ctx.channel() + "，当前线程:" + Thread.currentThread().getName());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
		logger.trace("◆Client一个与DispatchServer端的管道读数完成，管道:" + ctx.channel() + "，当前线程:" + Thread.currentThread().getName());
	}

	/**
	 * 读取数据的回调事件..
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, BaseMsg _baseMsg) throws Exception {
		logger.trace("◆Client的一个管道读取到DispatchServer返回数据，管道:" + ctx.channel() + "，当前线程:" + Thread.currentThread().getName());
		SocketChannel socketChannel = (SocketChannel) ctx.channel();
		ResponseMsg responseMsg = (ResponseMsg) _baseMsg; //
		try {
			//★★因为已经取得数据了,所以可以立即关闭!!从而快速释放管道,高让其他人可以使用管道...
			responseMap.put("message", responseMsg);
			socketChannel.close();
		} catch (Exception e) {
			ctx.close();
			if (e instanceof BizException) {
				logger.error("执行任务失败:" + e.getLocalizedMessage());
			} else {
				logger.error("解析返回报文异常:" + e.getLocalizedMessage());
				logger.error("错误信息:" , e);
			}
		} finally {
			socketChannel.close(); 
		}
	}

	/**
	 * 发生异常时的回调
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		SocketChannel socketChannel = (SocketChannel) ctx.channel();
		socketChannel.close(); 
		logger.error("调用引擎异常:" + cause.getLocalizedMessage());
		logger.error("错误信息: ", cause);
		logger.error("异常结束");
	}

}