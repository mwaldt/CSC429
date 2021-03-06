// specify the package
package model;

// system imports
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JFrame;

// project imports
import exception.InvalidPrimaryKeyException;
import database.*;

import impresario.IView;

import userinterface.View;
import userinterface.ViewFactory;

/** The class containing the book for the ATM application */
//==============================================================
public class Book extends EntityBase implements IView
{
	private static final String myTableName = "Book";

	protected Properties dependencies;

	// GUI Components

	private String updateStatusMessage = "";

	// constructor for this class
	//----------------------------------------------------------
	public Book(String bookID)
		throws InvalidPrimaryKeyException
	{
		super(myTableName);

		//setDependencies();
		String query = "SELECT * FROM " + myTableName + " WHERE (BookID = " + bookID + ")";

		Vector allDataRetrieved = getSelectQueryResult(query);

		// You must get one book at least
		if (allDataRetrieved != null)
		{
			int size = allDataRetrieved.size();

			// There should be EXACTLY one book. More than that is an error
			if (size != 1)
			{
				throw new InvalidPrimaryKeyException("Multiple books matching id : "
					+ bookID + " found.");
			}
			else
			{
				// copy all the retrieved data into persistent state
				Properties retrievedBookData = (Properties)allDataRetrieved.elementAt(0);
				persistentState = new Properties();

				Enumeration allKeys = retrievedBookData.propertyNames();
				while (allKeys.hasMoreElements() == true)
				{
					String nextKey = (String)allKeys.nextElement();
					String nextValue = retrievedBookData.getProperty(nextKey);

					if (nextValue != null)
					{
						persistentState.setProperty(nextKey, nextValue);
					}
				}

			}
		}
		// If no book found for this user name, throw an exception
		else
		{
			throw new InvalidPrimaryKeyException("No book matching id : "
				+ bookID + " found.");
		}
	}

	//----------------------------------------------------------
	public Book(Properties props)
	{
		super(myTableName);
		
		//setDependencies();
		persistentState = new Properties();
		Enumeration allKeys = props.propertyNames();
		while (allKeys.hasMoreElements() == true)
		{
			String nextKey = (String)allKeys.nextElement();
			String nextValue = props.getProperty(nextKey);

			if (nextValue != null)
			{
				persistentState.setProperty(nextKey, nextValue);
			}
		}
	}

	//-----------------------------------------------------------------------------------
	private void setDependencies()
	{
		dependencies = new Properties();
	
		myRegistry.setDependencies(dependencies);
	}

	
	//----------------------------------------------------------
	public Object getState(String key)
	{
		if (key.equals("UpdateStatusMessage") == true)
			return updateStatusMessage;

		return persistentState.getProperty(key);
	}

	//----------------------------------------------------------------
	public void stateChangeRequest(String key, Object value)
	{

		myRegistry.updateSubscribers(key, this);
	}

	/** Called via the IView relationship */
	//----------------------------------------------------------
	public void updateState(String key, Object value)
	{
		stateChangeRequest(key, value);
	}

	
	//-----------------------------------------------------------------------------------
	public static int compare(Book a, Book b)
	{
		String aNum = (String)a.getState("bookID");
		String bNum = (String)b.getState("bookID");

		return aNum.compareTo(bNum);
	}

	//-----------------------------------------------------------------------------------
	public void update()
	{
		updateStateInDatabase();
	}
	
	//-----------------------------------------------------------------------------------
	private void updateStateInDatabase() 
	{
		try
		{
			if (persistentState.getProperty("bookID") != null)
			{
				Properties whereClause = new Properties();
				whereClause.setProperty("bookID", persistentState.getProperty("bookID"));
				updatePersistentState(mySchema, persistentState, whereClause);
				updateStatusMessage = "Book data for bookID : " + persistentState.getProperty("bookID") + " updated successfully in database!";
			}
			else
			{
				Integer bookID =
					insertAutoIncrementalPersistentState(mySchema, persistentState);
				persistentState.setProperty("bookID", "" + bookID.intValue());
				updateStatusMessage = "Book data for new book : " +  persistentState.getProperty("bookID")
					+ "installed successfully in database!";
				System.out.println(updateStatusMessage);
			}
		}
		catch (SQLException ex)
		{
			updateStatusMessage = "Error in installing book data in database!";
		}
		//DEBUG System.out.println("updateStateInDatabase " + updateStatusMessage);
	}


	/**
	 * This method is needed solely to enable the book information to be displayable in a table
	 *
	 */
	//--------------------------------------------------------------------------
	public Vector getEntryListView()
	{
		Vector v = new Vector();

		v.addElement(persistentState.getProperty("bookID"));
		v.addElement(persistentState.getProperty("author"));
		v.addElement(persistentState.getProperty("title"));
		v.addElement(persistentState.getProperty("pubYear"));
		v.addElement(persistentState.getProperty("status"));

		return v;
	}

	//-----------------------------------------------------------------------------------
	protected void initializeSchema(String tableName)
	{
		if (mySchema == null)
		{
			mySchema = getSchemaInfo(tableName);
		}
	}
}

