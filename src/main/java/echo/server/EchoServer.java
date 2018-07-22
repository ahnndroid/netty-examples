package echo.server;

import echo.server.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {

	public static void main(String[] args) throws InterruptedException {
		
		/**
		 * STEP 1) 클라이언트와의 채널 연결 위한 스레드 그룹 설정
		 */
		// (1-1) 클라이언트로부터의 연결 요청 수락을 담당하는 스레드 그룹 생성
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		
		// (1-2) 연결된 클라이언트 소켓 채널로부터의 데이터 입출력 및 이벤트 처리를 담당하는 스레드 그룹 생성
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		
		try {
			/**
			 * STEP 2) 부트스트랩 설정
			 */
			// (2-1) Netty 서버의 최초 구동 시 초기 설정되어야 하는 모든 내용들을 담을 부트스트랩 객체 생성
			ServerBootstrap sbs = new ServerBootstrap();
			
			// (2-2) 부트스트랩에 (1), (2)를 통해 생성한 이벤트 루프 그룹들을 설정
			sbs.group(bossGroup, workerGroup)
			
			// (2-3) 부트스트랩에 서버 소켓이 사용할 네트워크 입출력 모드 설정
			   .channel(NioServerSocketChannel.class)
			
			// (2-4) 클라이언트로부터의 연결 채널에 대한 초기화를 위한 설정
			   .childHandler(new ChannelInitializer<SocketChannel>() {
			
				    // (2-5) 클라이언트 연결 채널 초기화 시 해당 채널의 이벤트 데이터를 처리하게 될 핸들러 설정
				    @Override
					protected void initChannel(SocketChannel ch) throws Exception {
				    	ChannelPipeline cpip = ch.pipeline();		// 소켓에 파이프라인을 꽂고
				    	cpip.addLast(new EchoServerHandler());		// 꽂은 파이프라인을 통해 들어온 이벤트 데이터를 처리할 이벤트 핸들러를 플러그인
					}
			   });
			
			/**
			 * STEP 3) 클라이언트 채널 이벤트 수신을 위한 바인딩 설정
			 */
			// 3-1) Echo 서버가 특정 포트를 사용하기 위한 포트 바인딩 호출 (이 때, 포트 바인딩은 비동기로 수행)
			ChannelFuture future = sbs.bind(8888).sync();
			
			// 3-2) (언제 들어올지 모를) 채널 연결 종료 이벤트를 수신하면 해당 채널을 닫기 위한 대기 설정
			future.channel().closeFuture().sync();
			
		} finally {
			/**
			 * STEP 4) Echo 서버 종료 처리
			 */
			// 4-1) 클라이언트 채널 이벤트 처리 담당 스레드 그룹 종료
			workerGroup.shutdownGracefully();
			
			// 4-2) 클라이언트 연결 요청 수락 담당 스레드 그룹 종료
			bossGroup.shutdownGracefully();
			
		}
	}
}