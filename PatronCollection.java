// specify the package
package bookstore;

// system imports
import java.util.Properties;
import java.util.Vector;

import javax.swing.JFrame;


// project imports
import exception.InvalidPrimaryKeyException;
import event.Event;
import database.*;
import impresario.IView;
import userinterface.View;
import userinterface.ViewFactory;
import userinterface.MainFrame;

/** The class containing the AccountCollection for the ATM application */
//==============================================================
public class PatronCollection  extends EntityBase implements IView
{
	private static final String myTableName = "Patron";

	private Vector patrons;
	// GUI Components

	// constructor for this class
	//----------------------------------------------------------
	public PatronCollection(Patron data, String val) throws
		Exception
	{
		super(myTableName);

		if (data == null)
		{
			new Event(Event.getLeafLevelClassName(this), "<init>",
				"Missing patron information", Event.FATAL);
			throw new Exception
				("UNEXPECTED ERROR: PatronCollection.<init>: patron information is null");
		}
		
		if (val == null)
		{
			new Event(Event.getLeafLevelClassName(this), "<init>",
				"Missing patron information", Event.FATAL);
			throw new Exception
				("UNEXPECTED ERROR: PatronCollection.<init>: patron information is null");
		}

		String query = "SELECT * FROM " + myTableName + " WHERE ( "+data+" = " +val+ " )";

		Vector allDataRetrieved = getSelectQueryResult(query);

		if (allDataRetrieved != null)
		{
			patrons = new Vector();

			for (int cnt = 0; cnt < allDataRetrieved.size(); cnt++)
			{
				Properties nextPatronData = (Properties)allDataRetrieved.elementAt(cnt);

				Patron patron = new Patron(nextPatronData);

				if (patron != null)
				{
					addPatron(patron);
				}
			}

		}
		else
		{
			throw new InvalidPrimaryKeyException("No patrons for id : "+val);
		}

	}

	//----------------------------------------------------------------------------------
	private void addPatron(Patron p)
	{
		//users.add(u);
		int index = findIndexToAdd(p);
		patrons.insertElementAt(p,index); // To build up a collection sorted on some key
	}

	//----------------------------------------------------------------------------------
	private int findIndexToAdd(Patron p)
	{
		//users.add(u);
		int low=0;
		int high = patrons.size()-1;
		int middle;

		while (low <=high)
		{
			middle = (low+high)/2;

			Patron midSession = (Patron)patrons.elementAt(middle);

			int result = Patron.compare(p,midSession);

			if (result ==0)
			{
				return middle;
			}
			else if (result<0)
			{
				high=middle-1;
			}
			else
			{
				low=middle+1;
			}


		}
		return low;
	}


	/**
	 *
	 */
	//----------------------------------------------------------
	public Object getState(String key)
	{
		if (key.equals("Patrons"))
			return patrons;
		else
		if (key.equals("PatronList"))
			return this;
		return null;
	}

	//----------------------------------------------------------------
	public void stateChangeRequest(String key, Object value)
	{
		
		myRegistry.updateSubscribers(key, this);
	}

	//----------------------------------------------------------
	public Patron retrieve(String patronId)
	{
		Patron retValue = null;
		for (int cnt = 0; cnt < patrons.size(); cnt++)
		{
			Patron nextPtrn = (Patron)patrons.elementAt(cnt);
			String nextPtrnNum = (String)nextPtrn.getState("PatronId");
			if (nextPtrnNum.equals(patronId) == true)
			{
				retValue = nextPtrn;
				return retValue; // we should say 'break;' here
			}
		}

		return retValue;
	}
	
	public Vector findPatronsOlderThan(String data, String val) throws Exception
	{
		String query = "SELECT * FROM " + myTableName + " WHERE ("+data+" > "+val+" )";
		
		makeVec(query, data, val);
		
		return patrons;
	}
	
	public Vector findPatronsYoungerThan(String data, String val) throws Exception
	{
		String query = "SELECT * FROM " + myTableName + " WHERE ("+data+" < "+val+" )";
		
		makeVec(query, data, val);
		
		return patrons;
	}
	
	public Vector findPatronsAtZipCode(String data, String val) throws Exception
	{
		String query = "SELECT * FROM " + myTableName + " WHERE ("+data+" LIKE "+val+" )";
		
		makeVec(query, data, val);
		
		return patrons;
	}
	
	public Vector findPatronsWithNameLike(String data, String val) throws Exception
	{
		String query = "SELECT * FROM " + myTableName + " WHERE ("+data+" LIKE "+val+" )";
		
		makeVec(query, data, val);
		
		return patrons;
	}
	
	public Vector makeVec(String query, String data, String val) throws InvalidPrimaryKeyException
	{
		Vector allDataRetrieved = getSelectQueryResult(query);

		if (allDataRetrieved != null)
		{
			patrons = new Vector();

			for (int cnt = 0; cnt < allDataRetrieved.size(); cnt++)
			{
				Properties nextPatronData = (Properties)allDataRetrieved.elementAt(cnt);

				Patron patron = new Patron(nextPatronData);

				if (patron != null)
				{
					addPatron(patron);
				}
			}

		}
		else
		{
			throw new InvalidPrimaryKeyException("No patrons for "+data+" : "
				+ val);
		}
		return patrons;
	}

	/** Called via the IView relationship */
	//----------------------------------------------------------
	public void updateState(String key, Object value)
	{
		stateChangeRequest(key, value);
	}

	//------------------------------------------------------
	protected void createAndShowView()
	{

		View localView = (View)myViews.get("PatronCollectionView");

		if (localView == null)
		{
				// create our initial view
				localView = ViewFactory.createView("PatronCollectionView", this);

				myViews.put("PatronCollectionView", localView);

				// make the view visible by installing it into the frame
				swapToView(localView);
		}
		else
		{
			// make the view visible by installing it into the frame
			swapToView(localView);
		}
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
