package menno.ctf;

/*
======================================================================================
==                                 Q2JAVA CTF                                       ==
==                                                                                  ==
==                   Author: Menno van Gangelen <menno@element.nl>                  ==
==                                                                                  ==
==            Based on q2java by: Barry Pederson <bpederson@geocities.com>          ==
==                                                                                  ==
== All sources are free for non-commercial use, as long as the licence agreement of ==
== ID software's quake2 is not violated and the names of the authors of q2java and  ==
== q2java-ctf are included.                                                         ==
======================================================================================
*/


import java.util.*;
import java.awt.Rectangle;
import q2java.*;
import q2jgame.*;
import javax.vecmath.*;



public class GenericMenu
{
	protected Player      fOwner;
	protected GenericMenu fLastMenu;

	protected String[]    fHeader;
	protected String[]    fFooter;
	protected Vector      fMenuItems;

	protected int         fSelectedItem;
	protected int         fMaxLineLength;
	protected int         fVerticalShift;

	protected int         fPercAbove;
	protected int         fPercBeneath;

	public GenericMenu( Player owner, GenericMenu lastMenu )
	{
		fOwner       = owner;
		fLastMenu    = lastMenu;

		fHeader     = new String[0];
		fFooter     = new String[0];
		fMenuItems  = new Vector();

		fSelectedItem = 0;
	}
	public void addMenuItem( String[] s )
	{
		fMenuItems.addElement( s );
	}
	public void close()
	{
		// close the menu;
		fOwner.cmd_inven( null );
	}
	private String createBotsel( int linelength )
	{
		String s = "\u0018";

		for ( int i=0; i<linelength; i++ )
			s += "\u0019";

		return s + "\u001A";
	}
	private String createTopsel( int linelength )
	{
		String s = "\u0012";

		for ( int i=0; i<linelength; i++ )
			s += "\u0013";

		return s + "\u0014";
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
	private String getFooter( Rectangle area )
	{
		String s    = "", line;
		int    x;
		int    y    = area.y + area.height;
		int    xmid = area.x + ( area.width/2 );

		for ( int i=fFooter.length-1; i>=0; i-- )
		{
			line = fFooter[i];
			y   -= 8;
			x    = xmid - ( 4*line.length() );
			s   += " xv " + x + " yv " + y + " string \"" + line +"\"";
		}

		// substract the header-area
		area.height = y - area.y;
		return s;
	}
	private String getHeader( Rectangle area )
	{
		String s    = "", line;
		int    x;
		int    y    = area.y;
		int    xmid = area.x + ( area.width/2 );

		for ( int i=0; i<fHeader.length; i++ )
		{
			line = fHeader[i];
			x    = xmid - ( 4*line.length() );
			s   += " xv " + x + " yv " + y + " string \"" + line +"\"";
			y   += 8;
		}

		// substract the header-area
		area.height -= (y-area.y);
		area.y       = y;
		return s;
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
		String      s = "", line;
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
				s += getSelector( lines, x, y );

			for ( int i=0; i<lines.length; i++, y+=8 )
			{
				// make sure the line fits in the area
				if ( (y+4) < area.y )
					continue;
				if ( (y+12) > (area.y+area.height) )
					break;

				line = lines[i];
				if ( line.length() > fMaxLineLength )
					line = line.substring( 0, fMaxLineLength );
				s   += " xv " + (x+4) + " yv " + (y+4) + " string2 \"" + line +"\"";
			}
			y += 4;
		}

		return s;
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
	private String getSelector( String[] lines, int x, int y )
	{
		String s = "";

		String topsel   = createTopsel( fMaxLineLength );
		String botsel   = createBotsel( fMaxLineLength );
		String leftsel  = "\u0015";
		String rightsel = "\u0017";
		
		s += " xv " + x + " yv " + y + " string \"" + topsel + "\"";	// top
		y += 8;

		for ( int i=1; i<lines.length; i++ )
			s += " yv " + (y+8*(i-1)) + " string \"" + leftsel + "\"";	// leftsel

		if (lines.length > 1)
			s += " xv " + (x + 8*(fMaxLineLength+1));

		for ( int i=1; i<lines.length; i++ )
			s += " yv " + (y+8*(i-1)) + " string \"" + rightsel + "\"";	// rightsel

		if (lines.length > 1)
		{
			s += " xv " + x;
			y += 8*(lines.length-1);
		}

		s += " yv " + y + " string \"" + botsel + "\"";	// bottom

		return s;
	}
	public void select()
	{
		fOwner.fEntity.cprint( Engine.PRINT_HIGH, "selected: " + (String)fMenuItems.elementAt(fSelectedItem) + "\n" );
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
	public void show()
	{
		show( "", new Rectangle(0, 0, 320, 240) );
	}
	protected void show( String preString, Rectangle screen )
	{
		String      s, line;
		int         x, y;
		Enumeration enum;

		s = preString;

		// show the header and substract its area from the screen
		s += getHeader( screen );

		// show the footer and substract its area from the screen
		s += getFooter( screen );

		// OK, we now have the free area rect into the screen
		// we can paint the selector and the items into the area
		
		s += getMenu( screen );
		if ( s.length() > 1023 )
		{
			System.out.println( "size too big: " + s.length() );
			s = s.substring( 0, 1023 );
		}	

		// send the menu to the client
		Engine.writeByte( Engine.SVC_LAYOUT );
		Engine.writeString( s );
		Engine.unicast( fOwner.fEntity, true );
	}
}