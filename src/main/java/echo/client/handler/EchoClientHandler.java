package echo.client.handler;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * EchoClientHandler 클래스
 * => 서버 측 핸들러와 구현 절차 및 패턴은 동일하며,
 *    클라이언트 측 인바운드 핸들러는 서버로부터의 수신 이벤트 처리를 담당 
 * 
 * @author ahnndroid
 *
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
	
	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * STEP 1) 클라이언트 소켓 채널이 최초 활성화되는 시점에 자동으로 실행되는 이벤트 처리부
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// STEP 1-1) 전송 대상 문자열 선언 및 초기화하고
		String sendMsg = "Hello, Netty";
		
		// STEP 1-2) 전송 대상 문자열을 Netty 바이트 버퍼에 담은 후
		ByteBuf msgBuf = Unpooled.buffer();
		msgBuf.writeBytes(sendMsg.getBytes());
		
		System.out.println("전송 데이터 [ " + msgBuf.toString(Charset.forName(DEFAULT_CHARSET)) + " ]");
		
		// STEP 1-3) 바이트 버퍼 메시지를 채널에 담아 서버 측으로 전송
		ctx.writeAndFlush(msgBuf);
	}
	
	/**
	 * STEP 2) 서버로부터의 메시지 수신 시 자동으로 실행되는 이벤트 처리부 
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// STEP 2-1) 서버로부터 받아온 메시지를 문자열로 변환 추출하여
		String readMsg = ((ByteBuf) msg).toString(Charset.forName(DEFAULT_CHARSET));
		
		// STEP 2-2) 추출된 문자열을 콘솔에 출력
		System.out.println("수신 데이터 [ " + readMsg + " ]");
	}
	
	/**
	 * STEP 3) 서버 연결 채널 이벤트를 처리하는 과정에서 예외 발생 시 자동으로 실행되는 예외 처리부
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// STEP 3-1) 이벤트 처리 중 예외 발생 시 해당 예외 관련 스택 트레이스 출력
		cause.printStackTrace();
		
		// STEP 3-2) 문제 발생 채널 종료
		ctx.close();
	}
}
