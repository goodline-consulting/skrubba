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

	@Override
	public String toString() {
		return "Tillsalu [id=" + id + ", lottnr=" + lottnr + ", visdatum=" + visdatum + ", saljdatum=" + saljdatum + "]";
	}

	public Tillsalu() {
		super();
	}

	public Tillsalu(int id, int lottnr) 
	{
		super();
		this.id      = id;
		this.lottnr = lottnr;		
	}   
}
