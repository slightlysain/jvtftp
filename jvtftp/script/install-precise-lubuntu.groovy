//priority INSTALL_COMMAND
import slightlysain.initrd.*;
import slightlysain.mac.*;
import slightlysain.jvtftp.io.*;

LIST_KEY = "install-precise-lubuntuMACs"
RD_KEY = "precise-non-pae-i386-initrd.gz"
KERNEL_KEY = "precise-non-pae-i386-linux"
PRESEED_FILE = "precise/lubuntu-preseed.cfg"


def installMACs = []

if(registry.contains(LIST_KEY)) {
	installMACs = registry.get(LIST_KEY)
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
				println "  KERNEL " + KERNEL_KEY
				println "  INITRD " + RD_KEY
			}
		}
	} else if(filename.equals(KERNEL_KEY)) {
		accept("precise/i386/non-pae/linux")
	} else if(filename.equals(RD_KEY)) {
		rd = new Initrd("precise/i386/non-pae/initrd.gz", streamFactory);
		//		url = "http://images.wisegeek.com/laptop-computer.jpg"
		//		u = new URL(url)
		//		inputurl = u.openStream()
		//		rd.add("laptop.jpg") {
		//			out << inputurl
		//		}
		//rd.add("preseed/example-preseed.cfg", "preseed.cfg")
		preseed = new ReplaceInputStream("precise/lubuntu-preseed.cfg", streamFactory)
		throw new Exception("Password not set");
		def map = [ 'username' : 'winston', 
					'fullusername' : "Winston",
					'password' :  '//password', //is "password"
					'hostname' : 'daphne',
					'domainname' : 'home.local'
					]
		preseed.setMapping(map)
		rd.add("preseed.cfg", preseed)
		accept(rd)
	}
}

