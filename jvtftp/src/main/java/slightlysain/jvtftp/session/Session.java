package slightlysain.jvtftp.session;

import java.io.IOException;

import org.apache.commons.net.tftp.TFTPPacket;

import slightlysain.jvtftp.tftpadapter.OnPacketHandler;
import slightlysain.jvtftp.tftpadapter.OnTimeoutHandler;

public interface Session extends OnPacketHandler, OnTimeoutHandler {
	public abstract void onTimeout();
	public abstract void onPacket(TFTPPacket packet);
	public abstract void onQuit();
	public abstract void start() throws IOException;

}