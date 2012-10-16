package it.ftpmeter.daemon;

import it.ftpmeter.db.HSqlStarter;
import it.ftpmeter.ftp.FTPSenderClient;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.Logger;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;
import org.hsqldb.server.ServerAcl.AclFormatException;

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
	}

	@Override
	public void init(DaemonContext arg0) throws DaemonInitException, Exception {
		int base = 0;

		String args[] = arg0.getArguments();
		for (base = 0; base < args.length; base++) {
			if (args[base].equals("-dropdb")) {
				dropdb = true;
			}
			if (args[base].equals("-endless")) {
				endless = true;
			}
		}
		numthread = Integer.parseInt(FTPSenderClient
				.getConfigString("ftp.threadnum"));
		for (int i = 0; i < numthread; i++) {
			clientThread[i] = new FTPSenderClient(endless);
		}
	}

	@Override
	public void start() throws Exception {
		HSqlStarter.startDB(dropdb);

		for (int i = 0; i < numthread; i++) {
			clientThread[numthread].start();
		}

	}

	@Override
	public void stop() throws Exception {
		HSqlStarter.stop();
		for (int i = 0; i < numthread; i++) {
			clientThread[numthread].setTerminate(true);
		}

	}
}
