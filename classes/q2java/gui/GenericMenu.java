package q2java.gui;

/**
 * Base class for game menus.
 *
 * Somewhat more like a java.awt.List than an AWT menu.
 *
 * @author Menno van Gangelen &lt;menno@element.nl&gt;
 */

/* 
======================================================================================
== All sources are free for non-commercial use, as long as the licence agreement of ==
== ID software's quake2 is not violated and the names of the authors of q2java and  ==
== q2java-ctf are included.                                                         ==
======================================================================================
*/


import java.util.*;
import java.awt.Rectangle;
import q2java.*;

public class GenericMenu
{
	protected NativeEntity      fOwner;

	protected String[]    fHeader;
	protected String[]    fFooter;
	protected Vector      fMenuItems;

	protected int         fSelectedItem;
	protected int         fMaxLineLength;
	protected int         fVerticalShift;

	protected int         fPercAbove;
	protected int         fPercBeneath;

	public GenericMenu( NativeEntity owner )
	{
		fOwner       = owner;

		fHeader     = new String[0];
		fFooter     = new String[0];
		fMenuItems  = new Vector();

		fSelectedItem = 0;
	}
	public void addMenuItem( String[] s )
	{
		fMenuItems.addElement( s );
	}
	/**
	 * Remove the menu from the player's screen
	 */
	public void close()
	{
		// close the menu;
		fOwner.setPlayerStat(NativeEntity.STAT_LAYOUTS, (short)0);
	}
	private char[] createBotsel( int linelength )
	{
		char[] result = new char[linelength+2];
		
		result[0] = '\u0018';
		for (int i = 1; i <= linelength; i++)
			result[i] = '\u0019';
		result[linelength+1] = '\u001A';

		return result;
	}
	private char[] createTopsel( int linelength )
	{
		char[] result = new char[linelength+2];
		
		result[0] = '\u0012';
		for (int i = 1; i <= linelength; i++)
			result[i] = '\u0013';
		result[linelength+1] = '\u0014';

		return result;
	}
	/**
	 * Actually show the menu on the player's screen.
	 *
	 * @param prefixString info to be prepended to beginning of layout string sent to client.  May be null or empty.
	 * @param screen a java.awt.rectangle that describes the area the menu should take up.
	 */
	protected void displayMenu( String prefixString, Rectangle screen )
	{
		StringBuffer sb = new StringBuffer();
		int         x, y;
		Enumeration enum;

		if (prefixString != null)
			sb.append(prefixString);

		// show the header and substract its area from the screen
		sb.append(getHeader( screen ));

		// show the footer and substract its area from the screen
		sb.append(getFooter( screen ));

		// OK, we now have the free area rect into the screen
		// we can paint the selector and the items into the area		
		sb.append(getMenu( screen ));

		// make sure the layout string isn't too long
		if ( sb.length() > 1023 )
		{
			System.out.println( "size too big: " + sb.length() );
			sb.setLength(1023); // truncate the StringBuffer
		}	

		// send the menu to the client
		Engine.writeByte( Engine.SVC_LAYOUT );
		Engine.writeString( sb.toString() );
		Engine.unicast( fOwner, true );

		// display it
		fOwner.setPlayerStat(NativeEntity.STAT_LAYOUTS, (short)1);				
	}
	/**
	 * Sets the fVerticalShift needed to shift the area up or down
 	 * to make the selected item visable.
	 * It also sets the percentages which are NOT visible above
	 * and beneath the visible area.
	 **/
	private void ensureSelectedVisable( Rectangle area )
	{
		int         y = area.y + fVerticalShift;
		int         numlines;
		Enumeration enum;
		Rectangle   areaSelected = null;
		Rectangle   areaNeeded;
		int         fullHeight = 0;
		int         fullY;

		enum = fMenuItems.elements();
		while ( enum.hasMoreElements() )
		{
			String[] lines = (String[])enum.nextElement();

			// if the item is selected, calculate the area it needs
			if ( lines == fMenuItems.elementAt(fSelectedItem) )
			{
				areaSelected = new Rectangle( area.x, y, area.width, 8*(lines.length+1) );
			}
			y          += 8*lines.length + 4;
			fullHeight += 8*lines.length + 4;
		}

		// see if areaSelected is completely inside the menu area or not
		areaNeeded = area.union( areaSelected );

		if ( areaNeeded.y < area.y )
			fVerticalShift += area.y - areaNeeded.y;

		else if ( areaNeeded.height > area.height )
			fVerticalShift += area.height - areaNeeded.height;

		// any needed shifting has been done, lets calculate the non-visible percentages
		fullY        = area.y + fVerticalShift;
		fPercAbove   = (int)( 100*((float)(area.y-fullY)/fullHeight) );
		fPercBeneath = Math.max(0, 100 - fPercAbove - (int)((float)100*area.height/fullHeight) );

		//System.out.println( "full: " + areaFull );
		//System.out.println( "this: " + area     );
		//System.out.println( "above: " + fPercAbove + " beneath: " + fPercBeneath );
	}
	/**
	 * Get the menu's footer.
	 *
	 * @return The footer formatted as a Q2 client layout string.
	 */
	private String getFooter( Rectangle area )
	{
		StringBuffer sb = new StringBuffer();
		int    x;
		int    y    = area.y + area.height;
		int    xmid = area.x + ( area.width/2 );

		for ( int i=fFooter.length-1; i>=0; i-- )
		{
			String line = fFooter[i];
			y   -= 8;
			x    = xmid - ( 4*line.length() );
			sb.append(" xv ");
			sb.append( x );
			sb.append(" yv ");
			sb.append( y );
			sb.append(" string \"");
			sb.append( line );
			sb.append('\"');
		}

		// substract the header-area
		area.height = y - area.y;
		return sb.toString();
	}
	/**
	 * Get the menu's header.
	 *
	 * @return The header formatted as a Q2 client layout string.
	 */
	private String getHeader( Rectangle area )
	{
		StringBuffer sb = new StringBuffer();
		
		int    x;
		int    y    = area.y;
		int    xmid = area.x + ( area.width/2 );

		for ( int i=0; i<fHeader.length; i++ )
		{
			String line = fHeader[i];
			x    = xmid - ( 4*line.length() );
			sb.append(" xv ");
			sb.append( x );
			sb.append(" yv ");
			sb.append( y );
			sb.append(" string \"");
			sb.append(line);
			sb.append('\"');
			y   += 8;
		}

		// substract the header-area
		area.height -= (y-area.y);
		area.y       = y;
		return sb.toString();
	}
	/**
	* Paints the menu into the area
	**/
	private String getMenu( Rectangle area )
	{
		// paint the menu into the area	
		//Rectangle   scrollbarArea;
		//Rectangle   menuItemArea;

		// calculate the areas for the menuitems and the scrollbar
		//menuItemArea  = new Rectangle( area.x, area.y, area.width-16, area.height );
		//scrollbarArea = new Rectangle( area.x+area.width-16, area.y, 16, area.height );

		// calculate the fMaxLineLength
		//fMaxLineLength = menuItemArea.width/8 - 1;	// substract the sidelines of the selector
		fMaxLineLength = area.width/8 - 1;	// substract the sidelines of the selector

		//return getMenuItems(menuItemArea) + getScrollbar(scrollbarArea);
		return getMenuItems( area );

		// scrollbar can't be painted, cause the total stringlength would > 1024.. (snif);
	}
	protected String getMenuItems( Rectangle area )
	{
		// paint the items into the area	
		StringBuffer sb = new StringBuffer();
		int         x = area.x;
		int         y;
		int         numlines;
		Enumeration enum;

		// make sure that the selected item is completely visible
		ensureSelectedVisable( area );
		y = area.y + fVerticalShift;

		enum = fMenuItems.elements();
		while ( enum.hasMoreElements() )
		{
			String[] lines = (String[])enum.nextElement();

			// if the item is selected, paint the selector
			if ( lines == fMenuItems.elementAt(fSelectedItem) )
				sb.append(getSelector( lines, x, y ));

			for ( int i=0; i<lines.length; i++, y+=8 )
			{
				// make sure the line fits in the area
				if ( (y+4) < area.y )
					continue;
				if ( (y+12) > (area.y+area.height) )
					break;

				String line = lines[i];
				if ( line.length() > fMaxLineLength )
					line = line.substring( 0, fMaxLineLength );
				sb.append(" xv ");
				sb.append(x+4);
				sb.append(" yv ");
				sb.append(y+4);
				sb.append(" string2 \"");
				sb.append(line);
				sb.append('\"');
			}
			y += 4;
		}

		return sb.toString();
	}
	protected String getScrollbar( Rectangle area )
	{
		String s = "";
		int    x = area.x;
		int    y = area.y;

		// paint the outside bar
		/*
		s += " xv " + x + " yv " + y + " string \"\u0012\u0014\"";
		y += 8;

		while ( y < (area.y+area.height-8) )
		{
			s += " yv " + y + " string \"\u0015\u0017\"";
			y += 8;
		}
		s += " yv " + (area.y+area.height-8) + " string \"\u0018\u001A\"";
		*/
		// paint the scroller
		// the scroller-character overlaps...
		s += " xv " + (x += 4);

		int maxh = area.height;
		int miny = ( fPercAbove   != 0 ? area.y + maxh/fPercAbove     : area.y );
		int maxy = ( fPercBeneath != 0 ? area.y + maxh - maxh/fPercBeneath : area.y + maxh );

		System.out.println( "painting from " + miny + " to " + maxy );

		s += " yv " + area.y + " string \"\u001B\"";

		y = miny;
		while ( y < maxy )
		{
			s += " yv " + y + " string \"\u000B\"";
			y += 6;
		}

		s += " yv " + maxy + " string \"\u000B\"";

		s += " yv " + (area.y + maxh) + " string \"\u005F\"";

		return s;
	}
	/**
	 * Get the index of the currently selected menu item.
	 */
	public int getSelectedIndex()
	{
		return fSelectedItem;
	}
	/**
	 * Get the currently selected item in the menu.
	 * @return java.lang.String[]
	 */
	public String[] getSelectedItem() 
	{
		return (String[]) fMenuItems.elementAt(fSelectedItem);
	}
	private String getSelector( String[] lines, int x, int y )
	{
		StringBuffer sb = new StringBuffer();

		char[] topsel   = createTopsel( fMaxLineLength );
		char[] botsel   = createBotsel( fMaxLineLength );
		char leftsel  = '\u0015';
		char rightsel = '\u0017';
		
		// top
		sb.append(" xv ");
		sb.append( x );
		sb.append(" yv ");
		sb.append( y );
		sb.append(" string \"");
		sb.append( topsel );
		sb.append('\"');	
		
		y += 8;

		for ( int i=1; i<lines.length; i++ )
		{
			// leftsel
			sb.append(" yv ");
			sb.append( y+8*(i-1) );
			sb.append(" string \"");
			sb.append( leftsel );
			sb.append('\"');
		}

		if (lines.length > 1)
		{
			sb.append(" xv ");
			sb.append(x + 8*(fMaxLineLength+1));
		}

		for ( int i=1; i<lines.length; i++ )
		{
			// rightsel
			sb.append(" yv ");
			sb.append(y+8*(i-1));
			sb.append(" string \"");
			sb.append( rightsel );
			sb.append('\"');
		}

		if (lines.length > 1)
		{
			sb.append(" xv ");
			sb.append( x );
			y += 8*(lines.length-1);
		}

		// bottom
		sb.append(" yv ");
		sb.append( y );
		sb.append(" string \"");
		sb.append( botsel );
		sb.append('\"');	

		return sb.toString();
	}
	public void selectNextItem()
	{
		fSelectedItem = (fSelectedItem + 1) % fMenuItems.size();
		show();
	}
	public void selectPreviousItem()
	{
		fSelectedItem = (fMenuItems.size() + fSelectedItem - 1) % fMenuItems.size();
		show();
	}
	public void setFooter( String[] s )
	{
		fFooter = s;
	}
	public void setHeader( String[] s )
	{
		fHeader = s;
	}
	/**
	 * Show the menu on the player's screen, using default settings.
	 *
	 */
	public void show()
	{
		displayMenu( "", new Rectangle(0, 0, 320, 240) );
	}
}