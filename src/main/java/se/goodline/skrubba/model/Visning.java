package se.goodline.skrubba.model;



import java.io.Serializable;

import javax.persistence.*;


@Entity
@IdClass(Visning.class)
public class Visning implements Serializable 
{
	@Id
    private int id;
	@Id
    private int	asp;
       
    
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

	@Override
	public String toString() {
		return "Visning [id=" + id + ", Asp=" + asp + "]";
	}

	public Visning() {
		super();
	}

	public Visning(int id, int asp) 
	{
		super();
		this.id  = id;
		this.asp = asp;		
	}   
}
