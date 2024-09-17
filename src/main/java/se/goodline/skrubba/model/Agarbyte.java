package se.goodline.skrubba.model;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Agarbyte 
{
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int     id;
    private int     lottnr;
    private String  relation;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date    datum;
    private String  fnamn;
    private String  enamn;
    private String  adress;
    private String  postnr;
    private String  postAdress;
    private String  telefon;
    private String  email;
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
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	public Date getDatum() {
		return datum;
	}
	public void setDatum(Date datum) {
		this.datum = datum;
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
	@Override
	public String toString() {
		return "Agarbyte [id=" + id + ", lottnr=" + lottnr + ", relation=" + relation + ", datum=" + datum + ", fnamn="
				+ fnamn + ", enamn=" + enamn + ", adress=" + adress + ", postnr=" + postnr + ", postAdress="
				+ postAdress + ", telefon=" + telefon + ", email=" + email + "]";
	}  
    
}
