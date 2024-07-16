package se.goodline.skrubba.model;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Histpers 
{
    @Id
    private int     id;
    private String  fnamn;
    private String  enamn;    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date    inskriven;
    
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

	
	public Date getInskriven() {
		return inskriven;
	}

	public void setInskriven(Date inskriven) {
		this.inskriven = inskriven;
	}

	@Override
	public String toString() {
		return "Histpers [id=" + id + ", fnamn=" + fnamn + ", enamn=" + enamn + ", inskriven=" + inskriven + "]";
	}
    
	public Histpers() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Histpers(int id, String fnamn, String enamn, Date inskriven) {
		super();
		this.id = id;
		this.fnamn = fnamn;
		this.enamn = enamn;
		this.inskriven = inskriven;
	}
}
