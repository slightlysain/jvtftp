import slightlysain.mac.*;
//priority HIGH_COMMAND

def disklessMACs = []

if(registry.contains("disklessMACs")) {
	disklessMACs = registry.get("disklessMACs")
}

if(request.isRead()) {
	filename = request.getFilename()
	MACMatcher match = new MACMatcher(filename)
	if(match.matches()) {
		if(disklessMACs.contains(match.getMAC())) {
			accept() {
				println "DEFAULT diskless"
				println "LABEL diskless"
				println "  KERNEL vmlinuz-2.6.32-5-amd64"
				println "  INITRD initrd.img.netboot"
				println "  APPEND root=/dev/nfs nfsroot=192.168.5.2:/nfs/diskless ip=dhcp rw"
			}
		}
	} else if(filename.equals("initrd.img.netboot")) {
		accept("initrd.img.netboot")
	} else if(filename.equals("vmlinuz-2.6.32-5-amd64")) {
		accept("vmlinuz-2.6.32-5-amd64")
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