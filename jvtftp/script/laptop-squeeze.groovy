//priority INSTALL_COMMAND
import javax.naming.ldap.Rdn;

import slightlysain.initrd.*;
import slightlysain.mac.*;
import slightlysain.jvtftp.io.*;
import slightlysain.passwords.*;

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
		accept("squeeze/i386/linux")
	} else if(filename.equals(INITRD_KEY)) {
		rd = new Initrd("squeeze/i386/initrd.gz", streamFactory)
		rd.add("squeeze/i386/google-chrome-stable_current_i386.deb", "google-chrome.deb")
		rd.add("squeeze/i386/install-chrome.sh", "install-chrome.sh")
		//		url = "http://images.wisegeek.com/laptop-computer.jpg"
		//		u = new URL(url)
		//		inputurl = u.openStream()
		//		rd.add("laptop.jpg") {
		//			out << inputurl
		//		}
		
		
		//TODO: add chrome to rd + install
		preseed = new ReplaceInputStream("squeeze/laptop-squeeze-preseed.cfg", streamFactory)
		
		pass = "password"
		def map = [ 'username': 'winston',
			'fullusername' : 'Winston',			
			'password' : EncryptPassword.encrypt(pass), //password
			'rootpassword' : EncryptPassword.encrypt(pass), //password
			'hostname' : 'Something',
			'domainname' : 'home.local',
			'httpproxy' : 'http://192.168.7.1:3142',
			'extra' : 'd-i preseed/late_command string /install-chrome.sh'
		]
		preseed.setMapping(map)
		rd.add("preseed.cfg", preseed)
		//rd.add("squeeze/squeeze-preseed.cfg", "preseed.cfg")
		accept(rd)
	}
}

