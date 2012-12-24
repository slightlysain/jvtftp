//priority INSTALL_COMMAND
import slightlysain.initrd.*;
import slightlysain.mac.*;
import slightlysain.jvtftp.io.*;

MACS_KEY = "laptopMACs"
KERNEL_KEY = "squeeze-laptop-linux"
INITRD_KEY = "squeeze-laptop-initrd.gz"


def installMACs = []

if(registry.contains(MACS_KEY)) {
	installMACs = registry.get(MACS_KEY)
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
				registry.put("rebootAlertMACs", [ matcher.getMAC() ])
			}
			accept() {
				println "DEFAULT installdebian"
				println "LABEL installdebian"
				println "  KERNEL " + KERNEL_KEY
				println "  INITRD " + INITRD_KEY
				//println "  APPEND desktop=lxde"
			}
		}
	} else if(filename.equals(KERNEL_KEY)) {
		accept("squeeze/linux")
	} else if(filename.equals(INITRD_KEY)) {
		rd = new Initrd("squeeze/initrd.gz", streamFactory)
		//		url = "http://images.wisegeek.com/laptop-computer.jpg"
		//		u = new URL(url)
		//		inputurl = u.openStream()
		//		rd.add("laptop.jpg") {
		//			out << inputurl
		//		}
		preseed = new ReplaceInputStream("squeeze/laptop-squeeze-preseed.cfg", streamFactory)
		def map = [ 'username': 'winston',
			'fullusername' : 'Winston',
			'password' : '//password', //password
			'rootpassword' : '//password', //password
			'hostname' : 'Something',
			'domainname' : 'home.local',
			'httpproxy' : 'http://192.168.5.2:3142'
		]
		preseed.setMapping(map)
		rd.add("preseed.cfg", preseed)
		//rd.add("squeeze/squeeze-preseed.cfg", "preseed.cfg")
		accept(rd)
	}
}

