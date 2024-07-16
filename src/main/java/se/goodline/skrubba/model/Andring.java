package se.goodline.skrubba.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "andring")
public class Andring 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "tidpunkt", nullable = false)
    private Date tidpunkt;
    
    @Column(name = "user")
    private String user;
    
    @Column(name = "lottnr", nullable = false)
    private int lottnr;

    @Column(name = "note")
    private String note;

    // Constructors, getters, and setters
    public Andring() {
    }

    public Andring(Date tidpunkt, String user, int lottnr, String note) {
        this.tidpunkt = tidpunkt;
        this.user = user;
        this.lottnr = lottnr;
        this.note = note;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getTidpunkt() {
		return tidpunkt;
	}

	public void setTidpunkt(Date tidpunkt) {
		this.tidpunkt = tidpunkt;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getLottnr() {
		return lottnr;
	}

	public void setLottnr(int lottnr) {
		this.lottnr = lottnr;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
    
}    