package se.goodline.skrubba.model;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Aspirant 
{
    @Id
    private int id;
    private String fnamn;
    private String enamn;
    private String adress;
    private String postnr;
    private String postAdress;
    private String telefon;
    private String email;  
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date   inskriven;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date   betalat;
    private int	   koPlats;
    private int    koPlatsAktiv;
    private int	   visningar;
    
	private String koStatus;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

       
	public String getFnamn() {
		return fnamn;
	}

	public void setFnamn(String fnamn) {
		this.fnamn = fnamn;
	}

	public String getEnamn() {
		return enamn;
	}

	public void setEnamn(String enamn) {
		this.enamn = enamn;
	}

	public String getKoStatus() {
		return koStatus;
	}

	public void setKoStatus(String koStatus) {
		this.koStatus = koStatus;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getPostnr() {
		return postnr;
	}

	public void setPostnr(String postnr) {
		this.postnr = postnr;
	}

	public String getPostAdress() {
		return postAdress;
	}

	public void setPostAdress(String postAdress) {
		this.postAdress = postAdress;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Date getInskriven() {
		return inskriven;
	}

	public void setInskriven(Date inskriven) {
		this.inskriven = inskriven;
	}

	public Date getBetalat() {
		return betalat;
	}

	public void setBetalat(Date betalat) {
		this.betalat = betalat;
	}

	public int getKoPlats() {
		return koPlats;
	}

	public void setKoPlats(int koPlats) {
		this.koPlats = koPlats;
	}
	public int getKoPlatsAktiv() {
		return koPlatsAktiv;
	}

	public void setKoPlatsAktiv(int koPlatsAktiv) {
		this.koPlatsAktiv = koPlatsAktiv;
	}

	public int getVisningar() {
		return visningar;
	}

	public void setVisningar(int visningar) {
		this.visningar = visningar;
	}

	@Override
	public String toString() {
		return "Aspirant [id=" + id + ", fnamn=" + fnamn + ", enamn=" + enamn + ", adress=" + adress + ", postnr="
				+ postnr + ", postAdress=" + postAdress + ", telefon=" + telefon + ", email=" + email + ", inskriven="
				+ inskriven + ", betalat=" + betalat + ", koPlats=" + koPlats + ", koPlatsAktiv=" + koPlatsAktiv + ", koStatus="  
				+ koStatus + ", visningar=" + visningar +"]";
	}

	public Aspirant() {
		super();
		this.inskriven = new Date();
		this.visningar = 0;
	}

	public Aspirant(int id, String fnamn, String enamn, String adress, String postnr, String postAdress, Date inskriven, Date betalat, int koPlats, int koPlatsAktiv, String koStatus) 
	{
		super();
		this.id           = id;
		this.fnamn        = fnamn;
		this.enamn        = enamn;
		this.adress       = adress;
		this.postnr       = postnr;
		this.postAdress   = postAdress;
		this.inskriven    = inskriven;
		this.betalat      = betalat;
		this.koPlats      = koPlats;
		this.koPlatsAktiv = koPlats;
		this.koStatus     = koStatus;
		this.visningar    = 0;
	}
   
}
