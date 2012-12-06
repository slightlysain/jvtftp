package slightlysain.jvtftp.packetadapter;

public interface DataPacket extends PacketAdapter {

	public abstract byte[] getData();

	public abstract int getDataLength();

	public abstract int getBlockNumber();

}