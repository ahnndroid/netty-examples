package echo.server.handler;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * EchoServerHandler 클래스
 * => 이번 장에서는 Discard 서버와 다르게 클라이언트로부터의 데이터 인바운드에 대한 처리를 위해
 *    ChannelInboundHandlerAdapter를 상속받도록 한다.
 * 
 * @author ahnndroid
 *
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
	
	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * STEP 1) 클라이언트로부터의 데이터 수신 시 자동 실행되는 이벤트 처리부
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// STEP 1-1) 클라이언트가 전송한 메시지를 담고 있는 Netty 바이트 버퍼로부터 문자열 데이터 추출
		String readMsg = ((ByteBuf) msg).toString(Charset.forName(DEFAULT_CHARSET));
		
		// STEP 1-2) 디버깅 확인 용도로 추출된 문자열 데이터 콘솔 출력
		System.out.println("수신 데이터 [ " + readMsg + " ]");
		
		// STEP 1-3) 클라이언트의 전송 메시지를 담은 바이트 버퍼를 그대로 해당 채널에 씀
		ctx.write(msg);
	}
	
	/**
	 * STEP 2) 클라이언트로부터 수신된 데이터 읽기 처리가 완료되는 시점에 자동 실행되는 이벤트 처리부
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// STEP 2-1) channelRead 이벤트 처리가 완료되는 시점에 해당 채널에 담긴 데이터를 클라이언트 측에 전송(flush)
		ctx.flush();
	}
	
	/**
	 * STEP 3) 클라이언트 연결 채널로부터의 이벤트를 처리하는 과정에서 예외 발생 시 자동으로 실행되는 예외 처리부
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// STEP 3-1) 이벤트 처리 중 예외 발생 시 해당 예외 관련 스택 트레이스 출력
		cause.printStackTrace();
		
		// STEP 3-2) 문제 발생 채널 종료
		ctx.close();
	}
}
