// specify the package
package bookstore;

// system imports
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JFrame;

// project imports
import exception.InvalidPrimaryKeyException;
//import exception.PasswordMismatchException;
import database.*;

import impresario.IView;

import userinterface.View;
import userinterface.ViewFactory;

/** The class containing the Patron  for the bookstore application */
//==============================================================
public class Patron extends EntityBase implements IView
{
	private static final String myTableName = "Patron";

	protected Properties dependencies;
	
	// GUI Components

	// constructor for this class
	//----------------------------------------------------------
	public Patron(Properties props)
	{
		super(myTableName);

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

	//----------------------------------------------------------
	public Patron(String patronId) throws InvalidPrimaryKeyException
	{
		super(myTableName);

		String query = "SELECT * FROM " + myTableName + " WHERE (PatronId = " + patronId + ")";

		Vector allDataRetrieved =  getSelectQueryResult(query);

		// You must get one patron at least
		if (allDataRetrieved != null)
		{
			int size = allDataRetrieved.size();

			// There should be EXACTLY one patron. More than that is an error
			if (size != 1)
			{
				throw new InvalidPrimaryKeyException("Multiple patrons matching patron id : "
					+ patronId + " found.");
			}
			else
			{
				// copy all the retrieved data into persistent state
				Properties retrievedPatronData = (Properties)allDataRetrieved.elementAt(0);
				persistentState = new Properties();

				Enumeration allKeys = retrievedPatronData.propertyNames();
				while (allKeys.hasMoreElements() == true)
				{
					String nextKey = (String)allKeys.nextElement();
					String nextValue = retrievedPatronData.getProperty(nextKey);

					if (nextValue != null)
					{
						persistentState.setProperty(nextKey, nextValue);
					}
				}

			}
		}
		// If no patron found for this user name, throw an exception
		else
		{
			throw new InvalidPrimaryKeyException("No patron matching patronId : "
				+ patronId + " found.");
		}
	}
	
	public static int compare(Patron a, Patron b)
	{
		String first = (String)a.getState("patronId");
		String second = (String)b.getState("patronId");

		return first.compareTo(second);
	}
	
	public void update()
	{
		updateStateInDatabase();
	}
	
	private void updateStateInDatabase() 
	{
		String text = new String();
		try
		{
			if (persistentState.getProperty("patronId") != null)
			{
				Properties pro = new Properties();
				pro.setProperty("patronId",
				persistentState.getProperty("patronId"));
				updatePersistentState(mySchema, persistentState, pro);
				text = "Patron info for patron ID : " + persistentState.getProperty("patronId")
						+ " has been updated";
			}
			else
			{
				Integer patronId =
					insertAutoIncrementalPersistentState(mySchema, persistentState);
				persistentState.setProperty("patronId", "" + patronId.intValue());
				text = "Patron data for new patron : " +  persistentState.getProperty("patronId")
					+ "has been installed";
			}
		}
		catch (SQLException ex)
		{
			text = "Error in installing patron in database";
		}
		//DEBUG System.out.println("updateStateInDatabase " + updateStatusMessage);
	}
	
	public Vector getEntryListView()
	{
		Vector v = new Vector();

		v.addElement(persistentState.getProperty("patronId"));
		v.addElement(persistentState.getProperty("name"));
		v.addElement(persistentState.getProperty("address"));
		v.addElement(persistentState.getProperty("city"));
		v.addElement(persistentState.getProperty("stateCode"));
		v.addElement(persistentState.getProperty("zip"));
		v.addElement(persistentState.getProperty("email"));
		v.addElement(persistentState.getProperty("dateOfBirth"));
		v.addElement(persistentState.getProperty("status"));

		return v;
	}

	//----------------------------------------------------------
	public Object getState(String key)
	{
		return persistentState.getProperty(key);
	}

	//----------------------------------------------------------------
	public void stateChangeRequest(String key, Object value)
	{
		persistentState.setProperty(key, (String)value);

		myRegistry.updateSubscribers(key, this);
	}

	/** Called via the IView relationship */
	//----------------------------------------------------------
	public void updateState(String key, Object value)
	{
		stateChangeRequest(key, value);
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

