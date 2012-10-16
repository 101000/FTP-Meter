package it.ftpmeter.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class ExecutionDAO {

	public static final Logger log = Logger.getLogger(ExecutionDAO.class);
	static Connection connection = null;
	
	static{
		try {
			connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/ftpmeter", "sa", "");
		} catch (SQLException e) {
			 log.error("Errore durante la connessione al Database");			 
		}
	}
		
	public void insert(final ExecutionBean execBean) throws SQLException{
		String sql = "insert into fme_execution (" +
				"nm_sender, " +
				"pg_esecuzione, " +
				"dt_inizio, " +
				"dt_fine, " +
				"ni_durata, " +
				"ni_size, " +
				"ni_speed, " +
				"ni_speedbit) " +
				"values (?,?,?,?,?,?,?,?)";
				
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, execBean.getNmSender());
		ps.setLong(2, execBean.getPgEsecuzione());
		ps.setTimestamp(3, execBean.getDtInizio());
		ps.setTimestamp(4, execBean.getDtFine());		
		ps.setDouble(5, execBean.getNiDurata());
		ps.setLong(6, execBean.getNiSize());
		ps.setDouble(7, execBean.getNiSpeed());
		ps.setDouble(8, execBean.getNiSpeedbit());		
		ps.execute();
		ps.close();
        
        
	}
	
	public long getLastPgEsecuzione() throws Exception{
		String sql = "select MAX(pg_esecuzione) from fme_execution";			
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if(rs.next())
			return rs.getInt(1);
		else throw new Exception("Impossibile trovare l'ultima esecuzione");
	}
}
