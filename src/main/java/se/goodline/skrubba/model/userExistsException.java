package se.goodline.skrubba.model;

public class userExistsException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public userExistsException() 
	{
		 super();
	}
	
	public userExistsException(String message) 
	{
	        super(message);
	}
	
	public userExistsException(String message, Throwable cause) 
	{
	        super(message, cause);
	}
	
	public userExistsException(Throwable cause) 
	{
        super(cause);
    }
}
