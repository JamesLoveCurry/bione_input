package com.yusys.bione.plugin.base.utils;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import com.yusys.bione.comp.utils.PropertiesUtils;


/**
 * Netty访问基类。&lt;T&gt;是返回消息类型<br>
 * 客户端与服务端之间的通讯方式是客户端发送一个请求，然后从服务端接收一个响应
 */
public class NettyClient<T> {

	private EventLoopGroup workerGroup;

	/**
	 * 所有Bootstrap的Map，以connectTimeout为KEY
	 */
	private ConcurrentMap<String, Bootstrap> bootstrapMap;
	
	/**
	 * 所有连接池，以channelPoolKey(&lt;host&gt;_&lt;port&gt;_&lt;connectTimeout&gt;_&lt;readTimeout&gt;_&lt;maxConnections&gt;)为KEY
	 */
	private ChannelPoolMap<String, ChannelPool> channelPoolMap;

	/**
	 * 所有任务队列的Map，KEY值见{@link #channelPoolMap}
	 */
	private ConcurrentMap<String, Queue<Object[]>> taskQueueMap;

	private Class<T> parameterClass;
	
	private Map<String, Object> responseMap = new HashMap<String, Object>();
	
	/**
	 * 初始化
	 */
	@SuppressWarnings("unchecked")
	protected void init() {
		parameterClass = (Class<T>) TypeUtils.getTypeArguments(this.getClass(), NettyClient.class)
				.get(NettyClient.class.getTypeParameters()[0]);
		workerGroup = new NioEventLoopGroup();
		bootstrapMap = new ConcurrentHashMap<String, Bootstrap>();
		channelPoolMap = new AbstractChannelPoolMap<String, ChannelPool>() {
			@Override
			protected ChannelPool newPool(String channelPoolKey) {
				String[] parts = StringUtils.split(channelPoolKey, '_');
				// 如果是JDK1.7及以上，可以使用LinkedTransferQueue
				Queue<Object[]> taskQueue = new LinkedBlockingQueue<Object[]>();
				Queue<Object[]> oldQueue = taskQueueMap.putIfAbsent(channelPoolKey, taskQueue);
				if (oldQueue != null) {
					taskQueue = oldQueue;
				}
				NettyChannelPoolHandler<T> channelPoolHandler = new NettyChannelPoolHandler<T>(
						parameterClass, taskQueue, Integer.parseInt(parts[3]));

				Bootstrap bootstrap = bootstrapMap.get(parts[2]);
				if (bootstrap == null) {
					bootstrap = new Bootstrap();
					bootstrap.channel(NioSocketChannel.class);
					bootstrap.group(workerGroup);
					bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
					bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.parseInt(parts[2]) * 1000);
					Bootstrap oldBootstrap = bootstrapMap.putIfAbsent(parts[2], bootstrap);
					if (oldBootstrap != null) {
						bootstrap = oldBootstrap;
					}
				}
				SocketAddress addr = new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
				ChannelPool channelPool = new FixedChannelPool(bootstrap.remoteAddress(addr), channelPoolHandler, Integer.parseInt(parts[4]));
				channelPoolHandler.setChannelPool(channelPool);
				return channelPool;
			}
		};
		taskQueueMap = new ConcurrentHashMap<String, Queue<Object[]>>();
	}

	/**
	 * 释放资源
	 */
	protected void close() {
		Future<?> future = workerGroup.shutdownGracefully();
		future.awaitUninterruptibly();
	}

	/**
	 * 发送消息，并接收响应消息
	 * 
	 * @param host 消息接收方主机
	 * @param port 消息接收方端口
	 * @param msg 待发送消息(可以为null，这时不需要接收响应消息)
	 * @param sync 是否同步，如果异步，则不接收响应消息，直接返回null
	 * @param connectTimeout 连接超时(秒)
	 * @param readTimeout 读超时(秒)
	 * @param maxConnections 最大同时连接数
	 * @return 响应消息，在异步模式时，直接返回null；在出错时，返回Throwable；正常时，返回T对象
	 * @throws InterruptedException
	 */
	public Object sendMsg(String host, int port, Object msg, boolean sync,
			int connectTimeout, int readTimeout, int maxConnections) throws InterruptedException {
		// 准备任务参数
		Object[] params = new Object[4];
		params[0] = msg;
		params[1] = Boolean.valueOf(sync);
		StringBuilder sb = new StringBuilder();
		sb.append(host).append('_').append(port).append('_').append(connectTimeout);
		sb.append('_').append(readTimeout).append('_').append(maxConnections);
		String channelPoolKey = sb.toString();
		ChannelPool channelPool = channelPoolMap.get(channelPoolKey);
		taskQueueMap.get(channelPoolKey).add(params);
		Future<Channel> future = channelPool.acquire();
		if (! sync) {
			return null;
		}
		// 处于阻塞状态，等待链接建立结果
		future.sync();
		if (! future.isSuccess()) {
			return params[3];
		}
		if (msg == null) {
			return params[2];
		}
		// 处于阻塞状态，等待结果返回
		while (true) {
			synchronized (params) {
				if (params[2] != null || params[3] != null) {
					break;
				}
				params.wait();
			}
		}
		return params[3] != null ? params[3] : params[2];
	}
	
	/**
	 * 通过短连接发送消息
	 * @param ip ip
	 * @param port 端口
	 * @param rqMsg 请求报文
	 * @param resMap 返回报文
	 * @param sync 是否同步，如果异步，则不接收响应消息
	 * @param connectTimeout 连接超时(秒)
	 * @param readTimeout 读超时(秒)
	 * @throws Throwable
	 */
	public void sendMsgByShort(String ip, int port, Object rqMsg, Map<String, Object> resMap, boolean sync, int connectTimeout, final int readTimeout) throws Throwable {
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		this.responseMap = resMap; 
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
			bootstrap.group(eventLoopGroup);
			bootstrap.remoteAddress(ip, port); 
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					PropertiesUtils propertiesUtils = PropertiesUtils.get("bione-frame/index/index.properties");
					String maxTransBytes = propertiesUtils.getProperty("maxTransBytes");
					int maxTransBytesNum = 10;
					if(StringUtils.isNotBlank(maxTransBytes)) {
						maxTransBytesNum = Integer.parseInt(maxTransBytes);
					}
					socketChannel.pipeline().addLast(new ReadTimeoutHandler(readTimeout));
					socketChannel.pipeline().addLast(new ObjectEncoder());
					socketChannel.pipeline().addLast(new ObjectDecoder(maxTransBytesNum * 1024 * 1024, ClassResolvers.cacheDisabled(null)));// 默认最大报文长度10M
					socketChannel.pipeline().addLast(new NettyClientHandlerByShort(responseMap)); 
				}
			});

			//实际连接,会处于阻塞状态,只有连通了(或失败)才会继续往下走.
			ChannelFuture future = bootstrap.connect(ip, port).sync(); //会处于阻塞状态,然后成功后才会继续往下走!!

			//判断连接结果
			if (future.isSuccess()) {
				//★★★实际发送消息
				SocketChannel channel = (SocketChannel) future.channel(); //
				if(null != rqMsg) {
					channel.writeAndFlush(rqMsg);
					//如果是异步通讯，就不阻塞
					if(sync) {
						channel.closeFuture().sync(); //这里会阻塞,等待关闭时才会继续往下走,从而形成"等数据"的效果!!
					}
				}
			} else {
				throw new Exception(future.cause().getLocalizedMessage());
			}
		} catch (Throwable _ex) {
			throw _ex;
		} finally {
			eventLoopGroup.shutdownGracefully();
		}
	}
}
