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

/** The class containing the Transaction for the BookStore application */
//==============================================================
public class Transaction extends EntityBase implements IView
{
	private static final String myTableName = "Transaction";

	protected Properties dependencies;

	// GUI Components

	private String updateStatusMessage = "";

	// constructor for this class
	//----------------------------------------------------------
	public Transaction(String transactionID)
		throws InvalidPrimaryKeyException
	{
		super(myTableName);

		//setDependencies();
		String query = "SELECT * FROM " + myTableName + " WHERE (transID = " + transactionID + ")";

		Vector allDataRetrieved = getSelectQueryResult(query);

		// You must get one transaction at least
		if (allDataRetrieved != null)
		{
			int size = allDataRetrieved.size();

			// There should be EXACTLY one Transaction. More than that is an error
			if (size != 1)
			{
				throw new InvalidPrimaryKeyException("Multiple Transactions matching id : "
					+ transactionID + " found.");
			}
			else
			{
				// copy all the retrieved data into persistent state
				Properties retrieveTransData = (Properties)allDataRetrieved.elementAt(0);
				persistentState = new Properties();

				Enumeration allKeys = retrieveTransData.propertyNames();
				while (allKeys.hasMoreElements() == true)
				{
					String nextKey = (String)allKeys.nextElement();
					String nextValue = retrieveTransData.getProperty(nextKey);

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
			throw new InvalidPrimaryKeyException("No transaction matching id : "
				+ transactionID + " found.");
		}
	}

	//----------------------------------------------------------
	public Transaction(Properties props)
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
	public static int compare(Transaction a, Transaction b)
	{
		String aNum = (String)a.getState("transID");
		String bNum = (String)b.getState("transID");

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
			if (persistentState.getProperty("transID") != null)
			{
				Properties whereClause = new Properties();
				whereClause.setProperty("transID", persistentState.getProperty("transID"));
				updatePersistentState(mySchema, persistentState, whereClause);
				updateStatusMessage = "Transaction data for transID : " + persistentState.getProperty("transID") + " updated successfully in database!";
			}
			else
			{
				Integer transID =
					insertAutoIncrementalPersistentState(mySchema, persistentState);
				persistentState.setProperty("transID", "" + transID.intValue());
				updateStatusMessage = "Transaction data for transaction book : " +  persistentState.getProperty("transID")
					+ "installed successfully in database!";
				System.out.println(updateStatusMessage);
			}
		}
		catch (SQLException ex)
		{
			updateStatusMessage = "Error in installing transaction data in database!";
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

		v.addElement(persistentState.getProperty("transID"));
		v.addElement(persistentState.getProperty("bookID"));
		v.addElement(persistentState.getProperty("patronID"));
		v.addElement(persistentState.getProperty("transType"));
		v.addElement(persistentState.getProperty("dateOfTrans"));

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

