package se.goodline.skrubbako.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Aspirant 
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String fnamn;
	private String enamn;
	private String tfn;
	private String adress;
	private String padress;
	private String postnr;
	private String email;
	private Date inskriven;
	
	
}
