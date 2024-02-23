package se.goodline.skrubba.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "anmalan", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Anmalan 
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fnamn;
    private String enamn;
    private String telefon;
    private String email;
    private String adress;
    private String postnr;
    private String padress;
	
    
    public Anmalan(Long id, String fnamn, String enamn, String telefon, String email, String adress, String postnr,
			String padress) {
		super();
		this.id      = id;
		this.fnamn   = fnamn;
		this.enamn   = enamn;
		this.telefon = telefon;
		this.email   = email;
		this.adress  = adress;
		this.postnr  = postnr;
		this.padress = padress;
	}
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
	public String getPadress() {
		return padress;
	}
	public void setPadress(String padress) {
		this.padress = padress;
	}
	
	@Override
	public String toString() {
		return "Anmalan [id=" + id + ", fnamn=" + fnamn + ", enamn=" + enamn + ", telefon=" + telefon + ", email="
				+ email + ", adress=" + adress + ", postnr=" + postnr + ", padress=" + padress + "]";
	}

    // Constructors, getters, and setters
}
