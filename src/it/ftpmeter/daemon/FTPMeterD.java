package it.ftpmeter.daemon;

import it.ftpmeter.db.HSqlStarter;
import it.ftpmeter.ftp.FTPSenderClient;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.Logger;

/**
 * 
 * @author Quaranta_M
 * 
 * 
 *         This JAVA application has a bug running with Java 7
 *         http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7077696 Solved by
 *         running this as administrator in the command prompt: netsh
 *         advfirewall set global StatefulFTP disable
 * 
 */
public class FTPMeterD implements Daemon {

	public static final Logger log = Logger.getLogger(FTPMeterD.class);
	private boolean dropdb = false;
	private boolean endless = false;
	private int loop = 0;
	private FTPSenderClient clientThread[] = null;
	int numthread;

	@Override
	public void destroy() {
		for (FTPSenderClient sender : clientThread) {
			while (sender.isAlive()){
				try {
					sender.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		HSqlStarter.stop();
		log.info("FTP Meter .. bye");
	}

	@Override
	public void init(DaemonContext arg0) throws DaemonInitException, Exception {
		HSqlStarter.startDB(dropdb);
		int base = 0;
		if(arg0!=null){
			String args[] = arg0.getArguments();
			for (base = 0; base < args.length; base++) {
				if (args[base].equals("-dropdb")) {
					dropdb = true;
				}
				if (args[base].equals("-endless")) {
					endless = true;					
				}
				if (args[base].equals("-t")) {
					numthread = Integer.parseInt(args[base+1]);
				}
				if (args[base].equals("-loop")) {
					loop = Integer.parseInt(args[base+1]);					
				}
			}
		}
		if(numthread==0){
			numthread = Integer.parseInt(FTPSenderClient.getConfigString("ftp.threadnum"));
		}
		clientThread = new FTPSenderClient[numthread];
		for (int i = 0; i < numthread; i++) {
			clientThread[i] = new FTPSenderClient(endless,i, loop);
		}
	}

	@Override
	public void start() throws Exception {
		log.info("Starting threads ...");
		for (int i = 0; i < numthread; i++) {
			clientThread[i].start();
			log.info("Thread "+i+" started");
		}
		
	}

	@Override
	public void stop() throws Exception {
		log.info("Stopping threads ...");
		for (int i = 0; i < numthread; i++) {
			clientThread[i].setTerminate(true);
			log.info("Thread "+i+" notified for halting");
		}
		
		
	}
}
