package it.ftpmeter.test;

import it.ftpmeter.daemon.FTPMeterD;

public class Main {

	
	public static void main(String[] args) throws Exception {
		FTPMeterD ftpd = new FTPMeterD();
		ftpd.init(null);
		ftpd.start();
//		ftpd.stop();
//		ftpd.destroy();
		
	}

}
