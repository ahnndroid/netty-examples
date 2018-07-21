package discard.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DiscardServerHandler extends SimpleChannelInboundHandler<Object> {

	/**
	 * STEP 1) 클라이언트 측으로부터 데이터가 전송되면(즉, 데이터가 수신되면) 자동으로 실행되는 이벤트 처리부
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("Received data => " + msg);
	}
	
	/**
	 * STEP 2) 클라이언트 연결 채널로부터의 이벤트를 처리하는 과정에서 예외 발생 시 자동으로 실행되는 예외 처리부 
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();	// 예외 발생에 관한 스택 트레이스 출력
		ctx.close();	// 연결 채널 종료
	}

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Object arg1) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
