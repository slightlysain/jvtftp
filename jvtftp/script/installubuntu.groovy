//priority INSTALL_COMMAND
import slightlysain.initrd.*;
import slightlysain.mac.*;

def installMACs = []

if(registry.contains("installMACs")) {
	installMACs = registry.get("installMACs")
}

if(request.isRead()) {
	filename = request.getFilename()
	matcher = new MACMatcher(filename)
	if(matcher.matches()) {
		if(installMACs.contains(matcher.getMAC())) {
			installMACs.remove(matcher.getMAC())
			if(registry.contains("rebootAlertMACs")) {
				macs = registry.get("rebootAlertMACs")
				macs.add(matcher.getMAC())
			} else {
				registry.put("rebootAlertMACs", [ matcher.getMAC()])
			}
			accept() {
				println "DEFAULT installubuntu"
				println "LABEL installubuntu"
				println "  KERNEL linux"
				println "  INITRD initrd.gz"
			}
		}
	} else if(filename.equals("linux")) {
		accept("install/i386-non-pae-linux")
	} else if(filename.equals("initrd.gz")) {
		rd = new Initrd("install/i386-non-pae-initrd.gz", streamFactory);
		//		url = "http://images.wisegeek.com/laptop-computer.jpg"
		//		u = new URL(url)
		//		inputurl = u.openStream()
		//		rd.add("laptop.jpg") {
		//			out << inputurl
		//		}
		rd.add("preseed/example-preseed.cfg", "preseed.cfg")
		accept(rd)
	}
}

