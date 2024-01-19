package se.goodline.skrubba.model;



import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;


@Entity
public class Param implements Serializable 
{
	@Id
	@Column(name = "param_name")
	private String paramName;
	
	@Column(name = "param_value")
	private String paramValue;
        
    
	
	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	@Override
	public String toString() {
		return "Param [paramName=" + paramName + ", paramValue=" + paramValue + "]";
	}

	public Param() {
		super();
	}

	public Param(String paramName, String paramValue) 
	{
		super();
		this.paramName  = paramName;
		this.paramValue = paramValue;		
		
	}   
}
