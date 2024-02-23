package se.goodline.skrubba.model;



import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;


@Entity
public class Betalning implements Serializable 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
	private int	asp;
    private int summa;
    private int ar;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date betdatum;
    private Date mailat;
    
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAsp() {
		return asp;
	}

	public void setAsp(int asp) {
		this.asp = asp;
	}

	public int getSumma() {
		return summa;
	}

	public void setSumma(int summa) {
		this.summa = summa;
	}

	public int getAr() {
		return ar;
	}

	public void setAr(int ar) {
		this.ar = ar;
	}

	public Date getBetdatum() {
		return betdatum;
	}

	public void setBetdatum(Date betDatum) {
		this.betdatum = betDatum;
	}
	
	public Date getMailat() {
		return mailat;
	}

	public void setMailat(Date mailat) {
		this.mailat = mailat;
	}

	@Override
	public String toString() {
		return "Betalning [id=" + id + ", Asp=" + asp + ", Summa=" + summa + ", ar=" + ar + ", betDatum=" + betdatum + ", mailat=" + mailat + "]";
	}

	public Betalning() {
		super();
	}

	public Betalning(int id, int asp, int summa, int ar, Date betDatum, Date mailat) 
	{
		super();
		this.id  = id;
		this.asp = asp;		
		this.summa = summa;
		this.ar = ar;
		this.betdatum = betDatum;
	}   
}
