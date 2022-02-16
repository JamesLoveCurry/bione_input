package com.yusys.bione.plugin.base.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端监听服务端返回结果的"回调类"<br>
 */
public class NettyClientHandler<T> extends SimpleChannelInboundHandler<T> {

	private static Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

	private NettyChannelPoolHandler<T> channelPoolHandler;

	/**
	 * 任务信息，其各个下标内容如下：<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;[0]：类型Object，待发送信息(可以为null，这时不需要读取数据)<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;[1]：类型Boolean，是否同步，在同步模式时，读取数据后将在params上执行notify<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;[2]：类型&lt;T&gt;，读取到的数据
	 * &nbsp;&nbsp;&nbsp;&nbsp;[3]：类型Throwable，出错异常
	 */
	private Object[] params;

	/**
	 * 偶尔有ChannelPoolHandler的channelAcquired后立刻出现channelReadComplete，因此以此区分
	 */
	private boolean haveRead;

	public NettyClientHandler(Class<? extends T> parameterClass) {
		super(parameterClass);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		logger.trace("◆channelRegistered, channel:" + ctx.channel() + ", Thread:" + Thread.currentThread().getName());
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
		logger.trace("◆channelUnregistered, channel:" + ctx.channel() + ", Thread:" + Thread.currentThread().getName());
	}

	void start(Channel channel) {
		params = channelPoolHandler.getTask();
		if (params == null || params[0] == null) {
			// 没有发送数据，直接释放链接
			release(channel);
		} else {
			// 输出信息
			channel.writeAndFlush(params[0]);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		logger.trace("◆channelActive, channel:" + ctx.channel() + ", Thread:" + Thread.currentThread().getName());
		start(ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		logger.trace("◆channelInactive, channel:" + ctx.channel() + ", Thread:" + Thread.currentThread().getName());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
		if (! haveRead) {
			return;
		}
		logger.trace("◆channelReadComplete, channel:" + ctx.channel() + ", Thread:" + Thread.currentThread().getName());
		release(ctx.channel());
	}

	/**
	 * 读取数据的回调事件..
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, T responseMsg) throws Exception {
		logger.trace("◆channelRead0, channel:" + ctx.channel() + ", Thread:" + Thread.currentThread().getName());
		if (((Boolean)params[1]).booleanValue()) {
			notify(responseMsg, null);
		}
		haveRead = true;
	}

	/**
	 * 发生异常时的回调
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		notify(null, cause);
		release(ctx.channel());

		logger.error("exceptionCaught: " + cause.getLocalizedMessage());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		cause.printStackTrace(new PrintStream(out));
		logger.error(out.toString());
		IOUtils.closeQuietly(out);
	}
	
	private void notify(T responseMsg, Throwable t) {
		synchronized (params) {
			if (t != null) {
				params[3] = t;
			} else {
				params[2] = responseMsg;
			}
			params.notify();
		}
	}

	private void release(Channel channel) {
		params = null;
		haveRead = false;
		logger.trace("◆release, channel:" + channel + ", Thread:" + Thread.currentThread().getName());
		channelPoolHandler.getChannelPool().release(channel);
	}

	protected void setChannelPoolHandler(NettyChannelPoolHandler<T> channelPoolHandler) {
		this.channelPoolHandler = channelPoolHandler;
	}
}
