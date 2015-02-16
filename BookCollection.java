// specify the package
package model;

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

/** The class containing the BookCollect for the BookStore application */
//==============================================================
public class BookCollection  extends EntityBase implements IView
{
	private static final String myTableName = "Book";

	private Vector books;
	// GUI Components

	// constructor for this class
	// NEEDS: a rework, must find a better way to pull in a list of book objects based on a search parameter
	//----------------------------------------------------------
   public BookCollection(String searchParam, String keyValue) throws Exception
   {
      super(myTableName);
      
      if(searchParam == null){
			new Event(Event.getLeafLevelClassName(this), "<init>",
				"Missing book search information", Event.FATAL);
			throw new Exception
				("UNEXPECTED ERROR: BookCollection.<init>: book search information is null");
      }
      
      if(keyValue == null){
			new Event(Event.getLeafLevelClassName(this), "<init>",
				"Missing book information", Event.FATAL);
			throw new Exception
				("UNEXPECTED ERROR: BookCollection.<init>: book information is null");
      }
      
      String query = "SELECT * FROM " + myTableName + " WHERE " + searchParam +"('" + keyValue + "')";

      Vector allDataRetrieved = getSelectQueryResult(query);

		if (allDataRetrieved != null)
		{
			books = new Vector();

			for (int cnt = 0; cnt < allDataRetrieved.size(); cnt++)
			{
				Properties nextBookData = (Properties)allDataRetrieved.elementAt(cnt);

				Book book = new Book(nextBookData);

				if (book != null)
				{
					addBook(book);
				}
			}

		}
		else
		{
			throw new InvalidPrimaryKeyException("Invalid primary key exception.");
		}

	}

	//----------------------------------------------------------------------------------
	private void addBook(Book b)
	{
		//users.add(u);
		int index = findIndexToAdd(b);
		books.insertElementAt(b,index); // To build up a collection sorted on some key
	}

	//----------------------------------------------------------------------------------
	private int findIndexToAdd(Book b)
	{
		//users.add(u);
		int low=0;
		int high = books.size()-1;
		int middle;

		while (low <=high)
		{
			middle = (low+high)/2;

			Book midSession = (Book)books.elementAt(middle);

			int result = Book.compare(b,midSession);

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
		if (key.equals("Books"))
			return books;
		else
		if (key.equals("BookList"))
			return this;
		return null;
	}

	//----------------------------------------------------------------
	public void stateChangeRequest(String key, Object value)
	{
		
		myRegistry.updateSubscribers(key, this);
	}

	//----------------------------------------------------------
	public Book retrieve(String bookID)
	{
		Book retValue = null;
		for (int cnt = 0; cnt < books.size(); cnt++)
		{
			Book nextBook = (Book)books.elementAt(cnt);
			String nextBookNum = (String)nextBook.getState("BookId");
			if (nextBookNum.equals(bookID) == true)
			{
				retValue = nextBook;
				return retValue; // we should say 'break;' here
			}
		}

		return retValue;
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

		View localView = (View)myViews.get("BookCollectionView");

		if (localView == null)
		{
				// create our initial view
				localView = ViewFactory.createView("BookCollectionView", this);

				myViews.put("BookCollectionView", localView);

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
