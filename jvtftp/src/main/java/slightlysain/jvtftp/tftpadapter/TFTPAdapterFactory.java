package slightlysain.jvtftp.tftpadapter;

public class TFTPAdapterFactory {
	
	public TFTPAdapter createTFTPAdapter() {
		return new TFTPAdapterImpl();
	}

}
