package it.ftpmeter.ftp;

import it.ftpmeter.db.ExecutionBean;
import it.ftpmeter.db.ExecutionDAO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;

public class FTPSenderClient extends Thread{

	public static final int MEGABYTE = 1024 * 1024;
	public static final Properties properties = new Properties();
	public static final Logger log = Logger.getLogger(FTPSenderClient.class);
	public static byte[] bytes;
	public static String filename;
	public static String username;
	public static String password;
	public static ExecutionDAO executionDAO = new ExecutionDAO();

	static {
		InputStream in = FTPSenderClient.class.getClassLoader().getResourceAsStream("ftp.properties");
		try {
			properties.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static {
		try {
			int filesize = Integer.parseInt(getConfigString("ftp.filesize"));
			bytes = new byte[filesize * MEGABYTE];
			new Random().nextBytes(bytes);
			filename = getConfigString("ftp.filename");  
			username = getConfigString("ftp.user");      
			password = getConfigString("ftp.pass");      
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

	public void start(boolean endless) throws NumberFormatException, IOException {
		FTPClient ftp = new FTPClient();
		InetAddress addr = null;
		int port = 0;		
		try {
			addr = InetAddress.getByName(getConfigString("ftp.server"));
			port = Integer.parseInt(getConfigString("ftp.port"));
		} catch (UnknownHostException ex) {
			log.error("Unknown host");
		}
		
		int loop =  Integer.parseInt(getConfigString("ftp.loop"));
		try {

			int reply;
			ftp.connect(addr, port);
			log.info("Connected to " + addr);
			log.info(ftp.getReplyString());

			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				log.error("FTP server refused connection.");
				System.exit(1);
			}
			log.info("Login user "+username);
			if (!ftp.login(username, password)) {
				log.info(ftp.getReplyString());
				ftp.logout();
			} else {

				log.info(ftp.getReplyString());

				ftp.setFileType(FTP.BINARY_FILE_TYPE);
				log.info("Binary mode on");
				log.info(ftp.getReplyString());

				ftp.enterLocalPassiveMode();
				log.info("Enter local passive mode.\n");
				
				if(endless){
					int i = 0;
					while(!terminate){
						sendFileAndStore(ftp,filename+"_"+Thread.currentThread().getId(),i);
						i++;
					}
				}
				else{
					for(int i = 0; i<loop; i++){
						sendFileAndStore(ftp,filename+"_"+Thread.currentThread().getId(),i);
					}
				}
				ftp.logout();
				log.info(ftp.getReplyString());
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
					log.info("Disconnected from server: " + addr);
				} catch (IOException e) {
					// do nothing
				}
			}
			log.error("Couldn't connect to server: " + addr,ex);			
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
					log.info("Disconnected from server: " + addr);
				} catch (IOException e) {
					// do nothing
				}
			}
		}

	}

	public static String getConfigString(final String key) throws IOException {
		return properties.getProperty(key);
	}

	protected static void sendFileAndStore(FTPClient ftp, String filename, int loopIndex) throws Exception{
		ExecutionBean executionBean = new ExecutionBean();		
		OutputStream fout = null;
		fout = ftp.storeFileStream(filename);
		log.info(ftp.getReplyString());
		log.info("Start sending file " + filename+" ...");	
		long startTime = System.currentTimeMillis();
		try{					
			fout.write(bytes);					
		}finally{
			fout.close();
		}
		long endTime = System.currentTimeMillis();	
		ftp.completePendingCommand();						
		log.info(ftp.getReplyString());
		log.info("File stored on FTP server");				
		float trRate =  ((float) bytes.length / (endTime - startTime));
		log.info("Invio #"+loopIndex+": "+bytes.length + " bytes (" + bytes.length / MEGABYTE + " MB) copiati in " + (float)(endTime - startTime)/1000 + " s. " 
				+ trRate/1000 + " MB/s - ~"+((long)(trRate*8)/1000)+" Mbit/s");
		executionBean.setDtInizio(new Timestamp(startTime));
		executionBean.setDtFine(new Timestamp(endTime));
		executionBean.setNiDurata((double)(endTime - startTime)/1000);
		executionBean.setNiSize(bytes.length);
		executionBean.setNiSpeed(new Double(trRate/1000));
		executionBean.setNiSpeedbit(new Double((trRate*8)/1000));
		executionBean.setNmSender(username+"_"+Thread.currentThread().getName());
		executionBean.setPgEsecuzione(executionDAO.getLastPgEsecuzione()+1);
		executionDAO.insert(executionBean);
	}

@Override
public void run() {
	try {
		this.start(endlessloop);
	} catch (NumberFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

private boolean endlessloop = false;
private boolean terminate = false;


public FTPSenderClient(boolean endless){
	this.endlessloop = endless;
}

public void setTerminate(boolean terminate) {
	this.terminate = terminate;
}




}
