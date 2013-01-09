import slightlysain.mac.*;
import slightlysain.initrd.*;
//priority HIGH_COMMAND

LIST_KEY = "clonezillaMACs"
TFTP_SERVER = "192.168.7.5"

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
						"ocs_live_run=\"ocs-live-general\"" +
						//"ocs_live_run=\"service ssh start\"" +
						//ocs_live_extra_param=\"\" " +
						//	" ocs_live_keymap=\"\" ocs_live_batch=\"no\" ocs_lang=\"\" " +
						" fetch=tftp://" + TFTP_SERVER + "/filesystem.squashfs " +
						'locales=en_US.UTF-8 keyboard-layouts=us keyboard-variant=qwerty' +
						" vga=788 nosplash noprompt"
			}
			//remove from list
		}
	} else if(filename.equals("clonezilla-kernel")) {
		accept("clonezilla/vmlinuz")
	} else if(filename.equals("clonezilla-initrd.img")) {
		//accept("clonezilla/initrd-newc.gz")
		//rd = new Initrd("clonezilla/initrd.gz", streamFactory);
//		rd.add("scripts/stuff",{
//			println "#!/bin/sh"
//			println "echo Hello world"
//		},  "100777")
		
		
//		INIT_SCRIPT_TYPE="live"
//		SCRIPTS_DIR = "scripts/${INIT_SCRIPT_TYPE}-premount/"
//		SCRIPT = "04primdisk"
//		INIT_BOTTOM_ORDER = "/scripts/init-bottom/udev\n" +
//				"[ -e /conf/param.conf ] && . /conf/param.conf\n"
//
//		LIVE_PREMOUNT_ORDER = 	"/scripts/live-premount/select_eth_device\n" +
//				"[ -e /conf/param.conf ] && . /conf/param.conf\n"+
//				"/scripts/live-premount/readonly\n"+
//				"[ -e /conf/param.conf ] && . /conf/param.conf\n"+
//				"/scripts/live-premount/modules\n"+
//				"[ -e /conf/param.conf ] && . /conf/param.conf\n"
//
//		LIVE_BOTTOM_ORDER = "/scripts/live-bottom/08persistence_excludes\n" +
//				"[ -e /conf/param.conf ] && . /conf/param.conf\n/scripts/live-bottom/12fstab\n" +
//				"[ -e /conf/param.conf ] && . /conf/param.conf\n" +
//				"/scripts/live-bottom/23networking\n" +
//				"[ -e /conf/param.conf ] && . /conf/param.conf\n" +
//				"/scripts/live-bottom/24preseed\n"  +
//				"[ -e /conf/param.conf ] && . /conf/param.conf\n" +
//				"/scripts/live-bottom/30accessibility\n" +
//				"[ -e /conf/param.conf ] && . /conf/param.conf\n" +
//				"/scripts/live-bottom/31upstart-tty\n" +
//				"[ -e /conf/param.conf ] && . /conf/param.conf\n"
//
//		

		//	rd.add("${SCRIPTS_DIR}text", { println "this is some text for you" })

//		rd.add("${SCRIPTS_DIR}${SCRIPT}", "777", {
//			println "#!/bin/sh"
//			println ""
//			println "#set -e"
//			println ""
//			println 'PREREQ=""'
//			println ""
//			print "prereqs()\n{\n\techo \"\${PREREQ}\"\n}\n\n"
//			print "case \"\${1}\" in\n"
//			print "\tprereqs)\n"
//			print "\t\tprereqs\n"
//			print "\t\texit 0\n"
//			print "\t\t;;\n"
//			print "esac\n"
//			println ""
//
//			println "echo HELLOWORLD"

			//			println ". /scripts/live-functions"
			//			println ""
			//			println 'log_begin_msg "*****Preparing system***********"'
			//			println 'log_warning_msg "WARNING"'
			//			println 'log_end_msg "done"'
			//			println ""
			//println "cat /${SCRIPTS_DIR}text > \${rootmnt}lhi.txt"
		//})



		//rd.del("${SCRIPTS_DIR}ORDER")


//		rd.add("${SCRIPTS_DIR}ORDER", "644"){
//			print LIVE_PREMOUNT_ORDER
//			println "/${SCRIPTS_DIR}${SCRIPT}"
//			println "[ -e /conf/param.conf ] && . /conf/param.conf"

		//}
		//accept(rd);
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