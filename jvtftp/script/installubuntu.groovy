//priority BOOT_COMMAND
import slightlysain.initrd.*;
import slightlysain.mac.*;

String[] installMacs[] = { "" }

if(request.isRead()) {
	filename = request.getFilename()
	matcher = new MACMatcher(filename)
	if(matcher.matches()) {
		if(matcher.getMAC().equals(installMacs[0])) {
		accept() {
			println "DEFAULT installubuntu"
			println "LABEL installubuntu"
			println "  KERNEL linux"
			println "  INITRD initrd.gz"
		}
		}
	} else if(filename.equals("linux")) {
		accept("linux")
	} else if(filename.equals("initrd.gz")) {
		rd = new Initrd("initrd.gz", streamFactory);
		rd.add("preseed/example-preseed.cfg", "preseed.cfg")
		accept(rd)
	}
}

