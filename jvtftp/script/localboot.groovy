import slightlysain.mac.*;
//priority BOOT_COMMAND

def bootMACs = [] 

if(registry.contains("bootMACs")) {
	bootMACs = registry.get("bootMACs")
}

if(request.isRead()) {
	filename = request.getFilename()
	MACMatcher match = new MACMatcher(filename)
	if(match.matches()) {
		if(bootMACs.contains(match.getMAC())) {
			accept() {
				println "DEFAULT local"
				println "LABEL local"
				println "  COM32 chain.c32"
				println "  APPEND hd0 0"
			}
		}
	} else if(filename.equals("reboot.c32")) {
		accept("reboot.c32")
	} else if(filename.equals("pxelinux.0")) {
		accept("pxelinux.0")
	} else if(filename.equals("ldlinux.c32")) {
		accept("ldlinux.c32")
	} else if(filename.equals("libcom32.c32")) {
		accept("libcom32.c32")
	} else if(filename.equals("chain.c32")) {
		accept("chain.c32")
	} else if(filename.equals("libutil_com.c32")) {
		accept("libutil_com.c32")
	}
}