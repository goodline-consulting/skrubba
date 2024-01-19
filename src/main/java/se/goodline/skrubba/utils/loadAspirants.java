package se.goodline.skrubba.utils;

import java.util.ArrayList;

import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.repository.AspirantLoader;

public class loadAspirants 
{
	
	AspirantLoader al = new AspirantLoader();
	ArrayList<Aspirant> aspList;
	
	void run()
	{
		al.loadAspirants("intresselistan.csv");
		aspList = al.getAsprianter();
		for(Aspirant asp: aspList)
			System.out.println(asp.toString());
	}
	
	
	public static void main(String[] args) 
	{
		System.out.println("AspirantLoader ver 1.0 (C) Goodline Consulitng AB Feb 2022, all rights reserved");
		new loadAspirants().run();
		System.out.println("Finished");
	}

}
