package it.ftpmeter.db;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class ExecutionBean {

	private long id;	//PK
	private String nmSender; // nome del versatore
	private long pgEsecuzione; // numero di esecuzione
	private Timestamp dtInizio; // data di avvio della i-esima esecuzione
	private Timestamp dtFine; // data di completamento della i-esima esecuzione
	private Double niDurata;// start - end della i-esima esecuzione in secondi
	private long niSize; // dimensione in byte dei dati trasferiti
	private Double  niSpeed; // MB/s
	private Double  niSpeedbit; // Mbit
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getPgEsecuzione() {
		return pgEsecuzione;
	}
	public void setPgEsecuzione(long pgEsecuzione) {
		this.pgEsecuzione = pgEsecuzione;
	}
	public Timestamp getDtInizio() {
		return dtInizio;
	}
	public void setDtInizio(Timestamp dtInizio) {
		this.dtInizio = dtInizio;
	}
	public Timestamp getDtFine() {
		return dtFine;
	}
	public void setDtFine(Timestamp dtFine) {
		this.dtFine = dtFine;
	}
	public Double getNiDurata() {
		return niDurata;
	}
	public void setNiDurata(Double niDurata) {
		this.niDurata = niDurata;
	}
	public long getNiSize() {
		return niSize;
	}
	public void setNiSize(long niSize) {
		this.niSize = niSize;
	}
	public Double  getNiSpeed() {
		return niSpeed;
	}
	public void setNiSpeed(Double  niSpeed) {
		this.niSpeed = niSpeed;
	}
	public Double  getNiSpeedbit() {
		return niSpeedbit;
	}
	public void setNiSpeedbit(Double  niSpeedbit) {
		this.niSpeedbit = niSpeedbit;
	}
	public String getNmSender() {
		return nmSender;
	}
	public void setNmSender(String nmSender) {
		this.nmSender = nmSender;
	}

}
