import slightlysain.mac.*;
import slightlysain.email.*;
//priority LOW_COMMAND
def rebootAlertMACs = []
if(registry.contains("rebootAlertMACs")) {
	rebootAlertMACs = registry.get("rebootAlertMACs")
}

if(request.isRead()) {
	filename = request.getFilename()
	MACMatcher match = new MACMatcher(filename)
	if(match.matches()) {
		if(rebootAlertMACs.contains(match.getMAC())) {
			sub = "Boot up detected for - " + match.getMAC()
			msg = "A system has attempted to boot from the tftp server with mac:" + match.getMAC()	
			e = new GmailAlert(sub, msg)
			e.send()
			rebootAlertMACs.remove(match.getMAC())
		}
		accept() {
			println "DEFAULT reb"
			println "LABEL reb"
			println "  COM32 reboot"
		}
	} else if(filename.equals("reboot.c32")) {
		accept("reboot.c32")
	} else if(filename.equals("pxelinux.0")) {
		accept("pxelinux.0")
	} else if(filename.equals("ldlinux.c32")) {
		accept("ldlinux.c32")
	} else if(filename.equals("libcom32.c32")) {
		accept("libcom32.c32")
	}
}