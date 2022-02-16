package com.yusys.bione.plugin.base.utils;

import java.util.Queue;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yusys.bione.comp.utils.PropertiesUtils;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NettyChannelPoolHandler<T> implements ChannelPoolHandler {

	private static Logger logger = LoggerFactory.getLogger(NettyChannelPoolHandler.class);

	private static final String CLIENT_HANDLER_NAME = "nettyClientHandler";

	private Class<T> parameterClass;

	/**
	 * 本Handler对应的连接池
	 */
	private ChannelPool channelPool;

	/**
	 * 同一连接池的连接共享一个任务队列
	 */
	private Queue<Object[]> taskQueue;

	private int readTimeout;

	public NettyChannelPoolHandler(Class<T> parameterClass, Queue<Object[]> taskQueue, int readTimeout) {
		this.parameterClass = parameterClass;
		this.taskQueue = taskQueue;
		this.readTimeout = readTimeout;
	}

	@Override
	public void channelAcquired(Channel channel) throws Exception {
		logger.trace("◆channelAcquired:" + channel + ", Thread:" + Thread.currentThread().getName());
		NettyClientHandler<?> nettyClientHandler = (NettyClientHandler<?>)channel.pipeline().get(CLIENT_HANDLER_NAME);
		nettyClientHandler.start(channel);
	}

	@Override
	public void channelCreated(Channel channel) throws Exception {
		logger.trace("◆channelCreated:" + channel + ", Thread:" + Thread.currentThread().getName());
		PropertiesUtils propertiesUtils = PropertiesUtils.get("bione-frame/index/index.properties");
		String maxTransBytes = propertiesUtils.getProperty("maxTransBytes");
		int maxTransBytesNum = 10;
		if(StringUtils.isNotBlank(maxTransBytes)) {
			maxTransBytesNum = Integer.parseInt(maxTransBytes);
		}
		((SocketChannel)channel).config().setKeepAlive(true);
		((SocketChannel)channel).config().setTcpNoDelay(true);
		channel.pipeline().addLast(new ReadTimeoutHandler(readTimeout));
		channel.pipeline().addLast(new ObjectEncoder());
		channel.pipeline().addLast(new ObjectDecoder(
				maxTransBytesNum * 1024 * 1024, ClassResolvers.cacheDisabled(null)));	// 默认最大报文长度10M
		NettyClientHandler<T> nettyClientHandler = new NettyClientHandler<T>(parameterClass);;
		nettyClientHandler.setChannelPoolHandler(this);
		channel.pipeline().addLast(CLIENT_HANDLER_NAME, nettyClientHandler);
	}

	@Override
	public void channelReleased(Channel ch) throws Exception {
		logger.trace("channelReleased:" + ch + ", Thread:" + Thread.currentThread().getName());
	}

	/**
	 * 从任务队列中提取第一个任务
	 */
	Object[] getTask() {
		return taskQueue.poll();
	}

	void setChannelPool(ChannelPool channelPool) {
		this.channelPool = channelPool;
	}

	ChannelPool getChannelPool() {
		return channelPool;
	}
}
