package se.goodline.skrubba.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import se.goodline.skrubba.service.StringArrayConverter;

@Entity
public class Brevmall 
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
	private String namn;
	private String rubrik;
	private String kropp;
	private String fot;
	
	@Convert(converter = StringArrayConverter.class)
	private String[] bilagor;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNamn() {
		return namn;
	}
	public void setNamn(String namn) {
		this.namn = namn;
	}
	public String getRubrik() {
		return rubrik;
	}
	public void setRubrik(String rubrik) {
		this.rubrik = rubrik;
	}
	public String getKropp() {
		return kropp;
	}
	public void setKropp(String kropp) {
		this.kropp = kropp;
	}
	public String getFot() {
		return fot;
	}
	public void setFot(String fot) {
		this.fot = fot;
	}
	public String[] getBilagor() {
		return bilagor;
	}
	public void setBilagor(String[] bilagor) {
		this.bilagor = bilagor;
	}
	@Override
	public String toString() {
		return "Brevmall [id=" + id + ", rubrik=" + rubrik + ", kropp=" + kropp + ", fot=" + fot + ", bilagor="
				+ bilagor + "]";
	}
	
	
	
	
}
