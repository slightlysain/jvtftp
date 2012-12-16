//priority INSTALL_COMMAND
import slightlysain.initrd.*;
import slightlysain.mac.*;

def installMACs = [ ]

if(registry.contains("installMACs")) {
	installMACs = registry.get("installMACs")
}

if(request.isRead()) {
	filename = request.getFilename()
	matcher = new MACMatcher(filename)
	if(matcher.matches()) {
		if(installMACs.contains(matcher.getMAC())) {
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

