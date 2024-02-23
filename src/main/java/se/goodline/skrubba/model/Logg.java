package se.goodline.skrubba.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "logg")
public class Logg 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "tidpunkt", nullable = false)
    private Date tidpunkt;

    @Column(name = "user", nullable = false)
    private String user;

    @Column(name = "typ")
    private String typ;

    @Column(name = "note")
    private String note;

    // Constructors, getters, and setters
    public Logg() {
    }

    public Logg(Date tidpunkt, String user, String typ, String note) {
        this.tidpunkt = tidpunkt;
        this.user = user;
        this.typ = typ;
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

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
    
}    