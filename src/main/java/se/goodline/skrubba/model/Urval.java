package se.goodline.skrubba.model;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
public class Urval 
{
	private List<String> valda;

	public List<String> getValda() {
		return valda;
	}

	public void setValda(List<String> valda) 
	{
		this.valda = valda;
	}
	
}
