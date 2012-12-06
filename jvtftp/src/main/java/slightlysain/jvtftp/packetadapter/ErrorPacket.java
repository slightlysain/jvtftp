package slightlysain.jvtftp.packetadapter;

public interface ErrorPacket extends PacketAdapter {

	public abstract int getCode();

	public abstract String getMessage();

	public abstract String getCodeString();

	public abstract boolean isOutOfSpace();

}