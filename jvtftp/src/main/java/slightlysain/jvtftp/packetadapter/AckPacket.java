package slightlysain.jvtftp.packetadapter;

public interface AckPacket extends PacketAdapter {

	public abstract int getBlockNumber();

}