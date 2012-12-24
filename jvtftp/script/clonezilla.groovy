import slightlysain.mac.*;
//priority HIGH_COMMAND

LIST_KEY = "clonezillaMACs"
TFTP_SERVER = "192.168.5.5"

def disklessMACs = []

if(registry.contains(LIST_KEY)) {
	disklessMACs = registry.get(LIST_KEY)
}

if(request.isRead()) {
	filename = request.getFilename()
	MACMatcher match = new MACMatcher(filename)
	if(match.matches()) {
		if(disklessMACs.contains(match.getMAC())) {
			accept() {
				println "DEFAULT clonezilla"
				println "LABEL clonezilla"
				println "  KERNEL clonezilla-kernel"
				println "  INITRD clonezilla-initrd.img"
				println "  APPEND boot=live config noswap nolocales edd=on nomodeset " +
						" ocs_live_run=\"ocs-live-general\" ocs_live_extra_param=\"\" " +
						" ocs_live_keymap=\"\" ocs_live_batch=\"no\" ocs_lang=\"\" " +
						" fetch=tftp://" + TFTP_SERVER + "/filesystem.squashfs vga=788 nosplash noprompt "
			}
		}
	} else if(filename.equals("clonezilla-kernel")) {
		accept("clonezilla/vmlinuz")
	} else if(filename.equals("clonezilla-initrd.img")) {
		accept("clonezilla/initrd.img")
	} else if(filename.equals("filesystem.squashfs")) {
		accept("clonezilla/filesystem.squashfs")
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