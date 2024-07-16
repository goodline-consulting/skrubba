package se.goodline.skrubba.model;



import java.io.Serializable;

import javax.persistence.*;


@Entity
@IdClass(VisningId.class)
public class Visning implements Serializable 
{
	@Id
    private int id;
	@Id
    private int	asp;
    private int ko_plats;   
    private String svar;
    
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

	public int getKo_plats() {
		return ko_plats;
	}

	public void setKo_plats(int ko_plats) {
		this.ko_plats = ko_plats;
	}
		

	public String getSvar() {
		return svar;
	}

	public void setSvar(String svar) {
		this.svar = svar;
	}

	@Override
	public String toString() {
		return "Visning [id=" + id + ", asp=" + asp + ", ko_plats=" + ko_plats +  ", svar=" + svar + "]";
	}

	public Visning() {
		super();
	}

	public Visning(int id, int asp, int ko_plats, String svar) 
	{
		super();
		this.id  = id;
		this.asp = asp;
		this.ko_plats = ko_plats;
		this.svar = svar;
	}   
}
