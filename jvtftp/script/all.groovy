//priority LOW_COMMAND
if(request.isRead()) {
	accept() {
		println "this is a test of new stuff";
		println "anything could go here";
		print "this should make things easier";
		println " i hope these changes take effect";
		println "additional line";
	}
} else if(request.isWrite()) {
  accept() { 
  	it.eachLine { 
  		line -> println("l:" + line) 
 	 } 
  }
}