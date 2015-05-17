/*
Miasma is based on the Plasma applet.
Modifications by J. Random Programmer.
Modifications released into the public domain.

I've fixed a number of flaws and bugs, and added features to break
out the individual elements that contribute to overall speed.
The original Plasma applet is public domain, and so is this.

Do what you want with Miasma, but DON'T SEND ME NUMBERS because I don't care.
If you care, then go ahead and post numbers, but that doesn't mean I have to care.
And, no, I don't have to explain why I don't care.
	-- JRP

From the original Plasma applet:
This applet creates an animated display by summing four
sine waves into an array. Example FPS rates are at
http://rsb.info.nih.gov/plasma.
It is based on "Sam's Java Plasma Applet"
(http://www.dur.ac.uk/~d405ua/Plasma.html) by Sam Marshall
(t-sammar@microsoft.com). It was modified to use 8-bit images
by Menno van Gangelen (M.vanGangelen@element.nl). Improved
frame rate calculation and code for using MemoryImageSource.setAnimated()
contributed by andy@mindgate.net.
*/

import java.awt.*;
import java.awt.image.*;

public class Miasma
	extends java.applet.Applet
 implements Runnable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image img;
	private Thread runThread;
	private long first;
	private int frames, fps;
	private int width, height;
	private int w,h,size;
	private int scale;
	private boolean showFPS;
	private IndexColorModel icm;
	private int[] waveTable;
	private byte[] pixels;
	private MemoryImageSource source;

	private boolean draw;
	private int deliver;
	private boolean filter;
	private boolean sync;
	private int pri;

	private boolean useRGB;
	private int[] pixelsRGB;
	private int[] mapRGB;

	private final boolean[] pending;
	private String strFPS;

	int framesIndex, past;
	int framesSum, elapsedSum;
	int[] framesPast, elapsedPast;

	public
	Miasma() 
	{
		pending = new boolean[ 1 ];
		strFPS = "";
	}


	public void init() 
	{
		scale = getInt( "scale", 2 );
		showFPS = getBoolean( "showfps", true );

		pri = getInt( "pri", Thread.MIN_PRIORITY );
		draw = getBoolean( "draw", true );
		deliver = getInt( "deliver", -1 );
		filter = getBoolean( "filter", false );
		useRGB = getBoolean( "rgb", false );
		sync = getBoolean( "sync", true );

		int avg = getInt( "avg", 10 );
		framesPast = new int[ avg ];
		elapsedPast = new int[ avg ];

		width = getSize().width;
		height = getSize().height;
		w = width/scale;
//		h = w;	// from Plasma original.	Huh?
		h = height/scale;
		pixels = new byte[ w * h ];
		pixelsRGB = new int[ pixels.length ];
		size = ((w+h)/2) * 4;
		waveTable = new int[size];
		calculateWaveTable();
		calculatePaletteTable();
	}

	private boolean
	getBoolean( String name, boolean defaultValue )
	{
		String val = getParameter( name );
		if ( val == null )
			return ( defaultValue );
		else
			return ( "true".equals( val ) );
	}

	private int
	getInt( String name, int defaultValue )
	{
		String val = getParameter( name );
		if ( val == null )
			return ( defaultValue );
		else
			return ( Integer.parseInt( val ) );
	}

	private void
	calculateWaveTable()
	{
		double perStep = (2 * Math.PI) / size;
		for ( int i = 0;	i < size;	 ++i )
		{	 waveTable[ i ] = (int) (32 * (1 + Math.sin( i * perStep )));	 }
	}

	private void
	calculatePaletteTable()
	{
		mapRGB = new int[ 256 ];

		// All G components are 0 in palette, so do nothing to fill the 'gg' array.
		int r, b;
		byte[] rr = new byte[ 256 ];
		byte[] gg = new byte[ 256 ];
		byte[] bb = new byte[ 256 ];

		// To ensure that the RGB image and the indexed image look different,
		// the RGB one shows red/green gradients vs. the indexed one's red/blue gradients.
		// Do this simply by using 'b' as a G component, not a B, in the 24-bit mapRGB values.
		for ( int i = 0;	i < 128;	i++ )
		{
			rr[ i ] = rr[ 255 - i ] = (byte) (r = i + i + 1);
			bb[ i ] = bb[ 255 - i ] = (byte) (b = 0xFF & -r);
			mapRGB[ i ] = mapRGB[ 255 - i ] = (r << 16) | (b << 8);
		}
		icm = new IndexColorModel( 8, 256, rr, gg, bb );
	}


	public void start() 
	{
//		System.out.println( "codebase = " + getCodeBase() );
//		System.out.println( " docbase = " + getDocumentBase() );

		// Defer creation of Images and MemoryImageSources until the last possible moment.
		// Use source's state as representative of all image-related variables.
		if ( source == null )
		{
			if ( useRGB )
			{
				// could use this.getColorModel() or ColorModel.getRGBdefault() or Toolkit.getColorModel()
				ColorModel modelRGB = getColorModel();
//				System.out.println( "ColorModel: " + modelRGB );
				source = new MemoryImageSource( w, h, modelRGB, pixelsRGB, 0, w );
			}
			else
			{
				// source is indexed image, with bytes for pixels.
				source = new MemoryImageSource( w, h, icm, pixels, 0, w );
			}
			source.setAnimated( true );
			source.setFullBufferUpdates( true );

			ImageProducer producer = source;
			if ( filter )
			{
				// The filter rescales to original size, so drawImage() won't.
				// You could use a different class of rescaling filter, to measure its effect on speed.
				producer = new FilteredImageSource( producer, new ReplicateScaleFilter( width, height ) );
			}

//			img = Toolkit.getDefaultToolkit().createImage( producer );
			img = createImage( producer );
		}

		if ( runThread == null )
		{
			for ( int i = 0;	i < framesPast.length;	++i )
			{	 framesPast[ i ] = elapsedPast[ i ] = 0;	}
			framesIndex = past = framesSum = elapsedSum = frames = 0;
			first = System.currentTimeMillis();

			pending[ 0 ] = false;

			runThread = new Thread( this );
			runThread.setPriority( pri );
			runThread.setDaemon( true );
			runThread.start();
		};
	}

	public void stop()
	{
		if	( runThread != null )
		{
			runThread.interrupt();
			runThread = null;
		}
	}

	public void update(Graphics g)
	{
		// Deliver source's pixels to Image, if needed.
		// An int started at -1 will reach 0 after 49.7 days at 1000 fps,
		// or 497 days at 100 fps, etc.
		if ( deliver != 0 )
		{
			source.newPixels();
			--deliver;
		}

		// Signal acceptance after new pixels delivered, but before drawing occurs.
		// This maximizes concurrency between producer and consumer threads, 
		// while ensuring that every calculated frame is accepted and handled.
		acceptDelivery();

		if ( draw )
			g.drawImage( img, 0, 0, width, height, null );

		++frames;
		if ( showFPS )
			calculateFPS( System.currentTimeMillis(), g );
	}


	private void
	calculateFPS( long now, Graphics g )
	{
		if ( now > first + 1000L )
		{
			fps = frames;
			frames = 0;

			// Elapsed millis should never overflow the capacity of an int (~2Msecs).
			int elapsed = (int) (now - first);
			first = now;

			int n = framesIndex;
			framesSum = framesSum - framesPast[ n ] + fps;
			elapsedSum = elapsedSum - elapsedPast[ n ] + elapsed;
			framesPast[ n ] = fps;
			elapsedPast[ n ] = elapsed;
			framesIndex = (n + 1) % framesPast.length;

			// Calculate scaled up by 10, to get an extra decimal digit of precision to display.
			// This is reasonable considering the precision of past readings, and the averaging of them.
			n = (framesSum * 10000) / elapsedSum;

			if ( past < framesPast.length )
				++past;

			strFPS = fps + " fps, avg " + (n/10) + "." + (n%10) + " over " + past + " sec";
			showStatus( strFPS );
		}

		// ## This display now taken over by showStatus()
//		g.clearRect( 0, height-15, 200, height );
//		g.drawString( strFPS, 2, height - 2 );
	}

	

	/** Trigger delivery of new pixels, waiting for prior pixels to be accepted, if necessary. */
	private void
	triggerDelivery()
	{
		if ( sync )
		{
			long failsafe = 1000;
			long abandon = System.currentTimeMillis() + failsafe;
			synchronized ( pending )
			{
			// Wait for prior delivery to be accepted before triggering another one.
			// Interrupted wait()'s return without triggering a delivery.
			while ( pending[ 0 ] )
			{
				if ( System.currentTimeMillis() >= abandon )
				break;
	
				try
				{	 pending.wait( failsafe );	}
				catch ( InterruptedException why )
				{	 Thread.currentThread().interrupt();	return;	 }	// reassert interrupt, then return
			}
			pending[ 0 ] = true;
			repaint();
			}
		}
		else
		{
			// Identical to original Plasma code.
			repaint();
			Thread.yield();
		}
	}

	/** Accept delivery of new pixels, allowing calculation of new pixels to proceed. */
	private void
	acceptDelivery()
	{
		if ( sync )
		{
			synchronized ( pending )
			{
				pending[ 0 ] = false;
				pending.notifyAll();
			}
		}
	}


	public void
	run()
	{
		int index, bottom;
		int result, tempval;
		int tpos1, tpos2, tpos3, tpos4;
		int inc1=6, inc2=3, inc3=3, inc4=9;
		int pos1=0, pos2=0, pos3=0, pos4=0;
		int spd1=2, spd2=5, spd3=1, spd4=4;

		while ( ! Thread.currentThread().isInterrupted() )
		{
			tpos1 = pos1;	 tpos2 = pos2;
			for( index = pixels.length - 1;	 index >= 0;	) 
			{
				tpos3 = pos3 - inc3;	tpos4 = pos4 - inc4;
				tempval = waveTable[ tpos1 %= size ] + waveTable[ tpos2 %= size ];
				for ( bottom = index - w;	 index > bottom;	)
				{
					tpos3 = (tpos3 + inc3) % size;	tpos4 = (tpos4 + inc4) % size;
					result = tempval + waveTable[ tpos3 ] + waveTable[ tpos4 ];
					// Fill in pixelsRGB[] and pixels[], though only one has its data delivered to the Image.
					pixelsRGB[ index ] = mapRGB[ 0xFF & result ];
//					pixelsRGB[ index ] = (0xFF & result) << 8;
					pixels[ index-- ] = (byte) result;
				}
				tpos1 += inc1;	tpos2 += inc2;
			}
			triggerDelivery();
			pos1+=spd1; pos2+=spd2; pos3+=spd3; pos4+=spd4;
		}

	}

}
