package se.goodline.skrubba.model;

import java.util.Date;

import javax.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Tillsalu
{
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int	lottnr;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date visdatum;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date saljdatum;
    private String saldtill;
    private int pris;
    private int prisbyggnad;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date tilltrdatum;
    private int asp;
    
	public int getPris() {
		return pris;
	}

	public void setPris(int pris) {
		this.pris = pris;
	}

	public int getPrisbyggnad() {
		return prisbyggnad;
	}

	public void setPrisbyggnad(int prisbyggnad) {
		this.prisbyggnad = prisbyggnad;
	}

	public Date getTilltrdatum() {
		return tilltrdatum;
	}

	public void setTilltrdatum(Date tilltrdatum) {
		this.tilltrdatum = tilltrdatum;
	}

	public String getSaldtill() {
		return saldtill;
	}

	public void setSaldtill(String saldtill) {
		this.saldtill = saldtill;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLottnr() {
		return lottnr;
	}

	public void setLottnr(int lottnr) {
		this.lottnr = lottnr;
	}

	public Date getVisdatum() {
		return visdatum;
	}

	public void setVisdatum(Date visdatum) {
		this.visdatum = visdatum;
	}

	public Date getSaljdatum() {
		return saljdatum;
	}

	public void setSaljdatum(Date saljdatum) {
		this.saljdatum = saljdatum;
	}

	
	public int getAsp() {
		return asp;
	}

	public void setAsp(int asp) {
		this.asp = asp;
	}

	public Tillsalu() {
		super();
	}

	

	@Override
	public String toString() {
		return "Tillsalu [id=" + id + ", lottnr=" + lottnr + ", visdatum=" + visdatum + ", saljdatum=" + saljdatum
				+ ", saldtill=" + saldtill + ", pris=" + pris + ", prisbyggnad=" + prisbyggnad + ", tilltrdatum="
				+ tilltrdatum + ", asp=" + asp + "]";
	}

	public Tillsalu(int id, int lottnr) 
	{
		super();
		this.id      = id;
		this.lottnr = lottnr;		
	}   
}
