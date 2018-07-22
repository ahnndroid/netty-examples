package echo.client;

import echo.client.handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class EchoClient {
	
	private static final String HOST = "localhost";
	private static final int PORT = 8888;

	public static void main(String[] args) throws InterruptedException {
		/**
		 * STEP 1) 서버와의 채널 연결 위한 스레드 그룹 설정
		 */
		// STEP 1-1) 서버와의 연결 채널 관리를 담당할 스레드 그룹 생성
		EventLoopGroup group = new NioEventLoopGroup();
		
		try {
			/**
			 * STEP 2) 부트스트랩 설정
			 */
			// (2-1) Netty 클라이언트 최초 구동 시 초기 설정되어야 하는 모든 내용들을 담을 부트스트랩 객체 생성
			Bootstrap cbs = new Bootstrap();
			
			// (2-2) 부트스트랩에 이벤트 루프 그룹 설정 (서버와의 연결 채널 관리만 필요하므로 이벤트 루프 그룹이 서버와 달리 한 개만 존재)
			cbs.group(group)
			
			// (2-3) 부트스트랩에 클라이언트 소켓이 사용할 네트워크 입출력 모드 설정
			   .channel(NioSocketChannel.class)
			
			// (2-4) 서버와의 연결 채널에 대한 초기화 설정
			   .handler(new ChannelInitializer<SocketChannel>() {
				    // (2-5) 서버 연결 채널 초기화 시 해당 채널의 이벤트 데이터를 처리하게 될 핸들러 설정
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline cpip = ch.pipeline();		// 소켓에 파이프라인을 꽂고
						cpip.addLast(new EchoClientHandler());		// 꽂은 파이프라인을 통해 들어온 이벤트 데이터를 처리할 이벤트 핸들러를 플러그인
					};
			   });
			
			/**
			 * STEP 3) 서버와의 채널 연결 및 이후 연결 종료 시까지 대기 
			 */
			// 3-1) Echo 서버와의 채널 연결 진행
			ChannelFuture future = cbs.connect(HOST, PORT).sync();
			
			// 3-2) 채널 연결을 통해 발행된 퓨처 객체를 통해 해당 채널 완료 시까지 비동기 방식으로 대기
			future.channel().closeFuture().sync();
			
		} finally {
			/**
			 * STEP 4) Echo 클라이언트 종료 처리
			 */
			// 4-1) 서버 연결 채널 관리용 스레드 그룹 종료
			group.shutdownGracefully();
		}
	}

}
