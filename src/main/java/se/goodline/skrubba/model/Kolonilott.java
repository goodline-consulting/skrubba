package se.goodline.skrubba.model;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Kolonilott 
{
    @Id
    private int     lottnr;
    private String  adress;
    private String  agare;
    private String  hemadress;
    private String  lgh;
    private String  postnr;
    private String  postort;
    private String  email;
    private int	    areal;
    private boolean tillsalu;
    private int     omrade;
	private String  telefon;

		
	public String getHemadress() {
		return hemadress;
	}

	public String getHemadressPlusLgh() 
	{
		return (hemadress != null ? hemadress : "") + " " + (lgh != null ? lgh : "");
	}
	
	public void setHemadress(String hemadress) {
		this.hemadress = hemadress;
	}

	public String getLgh() {
		return lgh;
	}

	public void setLgh(String lgh) {
		this.lgh = lgh;
	}

	public String getPostnr() {
		return postnr;
	}

	public void setPostnr(String postnr) {
		this.postnr = postnr;
	}

	public String getPostort() {
		return postort;
	}

	public void setPostort(String postort) {
		this.postort = postort;
	}

	public String getAgare() {
		return agare;
	}

	public void setAgare(String agare) {
		this.agare = agare;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getLottnr() {
		return lottnr;
	}

	public void setLottnr(int lottnr) {
		this.lottnr = lottnr;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public int getAreal() {
		return areal;
	}

	public void setAreal(int areal) {
		this.areal = areal;
	}

	public boolean isTillsalu() {
		return tillsalu;
	}

	public void setTillsalu(boolean tillsalu) {
		this.tillsalu = tillsalu;
	}
	
	
	public int getOmrade() {
		return omrade;
	}

	public void setOmrade(int omrade) {
		this.omrade = omrade;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}

	@Override
	public String toString() {
		return "Kolonilott [lottnr=" + lottnr + ", adress=" + adress + ", agare=" + agare + ", hemadress=" + hemadress
				+ ", lgh=" + lgh + ", postnr=" + postnr + ", postort=" + postort + ", email=" + email + ", areal="
				+ areal + ", tillsalu=" + tillsalu + ", omrade=" + omrade + ", telefon=" + telefon + "]";
	}

	
	public Kolonilott() {
		super();
	}
    
	public Kolonilott(int lottnr, String adress, String agare, String hemadress, String lgh, String postnr, String postort,
			          String email, int areal, boolean tillsalu, int omrade, String telefon) 
	{
		super();
		this.lottnr = lottnr;
		this.adress = adress;
		this.agare = agare;
		this.hemadress = hemadress;
		this.lgh = lgh;
		this.postnr = postnr;
		this.postort = postort;
		this.email = email;
		this.areal = areal;
		this.tillsalu = tillsalu;
		this.omrade = omrade;
		this.telefon = telefon;
	}   
}
