package slightlysain.mac;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MACMatcher {
	final String REGEX = "pxelinux\\.cfg/01-"
			+ "((\\p{XDigit}\\p{XDigit})-(\\p{XDigit}\\p{XDigit})-(\\p{XDigit}\\p{XDigit})"
			+ "-(\\p{XDigit}\\p{XDigit})-(\\p{XDigit}\\p{XDigit})-(\\p{XDigit}\\p{XDigit}))";
	private Pattern pattern = Pattern.compile(REGEX);
	private Matcher matcher;
	
	public MACMatcher(final String input) {
		matcher = pattern.matcher(input);
	}
	
	public boolean matches() {
		return matcher.matches();
	}
	
	public String getMAC() {
		return matcher.group(1);
	}
	
	public String getMACByte(int i) {
		if(i < 0 || i > 5) {
			throw new IndexOutOfBoundsException();
		}
		
		return matcher.group(i + 1);
	}
	

}
