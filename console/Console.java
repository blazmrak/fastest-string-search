package console;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class Console implements KeyListener, ActionListener
{

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int screenWidth = screenSize.width;
	int screenHeight = screenSize.height;

	String title = null;
	String text = null;

	JFrame jf = null;
	JTextArea jta = null;
	JScrollPane jsp = null;
	JMenuBar jmb = null;
	JMenu jm = null;
	JMenuItem jmi = null;

	// key codes
	int BACKSPACE = 8;
	int ENTER = 10;
	int PG_UP = 33; // do nothing for this key pressed
	int PG_DN = 34; // do nothing for this key pressed
	int END = 35;
	int HOME = 36;
	int LEFT_ARROW = 37;
	int UP_ARROW = 38; // do nothing for this key pressed
	//int RIGHT_ARROW = 39; // handled by JTextArea
	int DOWN_ARROW = 40; // do nothing for this key pressed

	int CTRL = 128;
	int A = 65; // disable ctrl-a
	int H = 72; // handle ctrl-h
	//int DELETE = 127; // handled by JTextArea

	int initialCaretPosition = 0;
	int endOfInputCaretPosition = 0;
	Object lock1 = new Object();
	Object lock2 = new Object();
	boolean inputAvailable = false;
	byte[] b = null;
	int len = -1;
	int indexIntoByteArray = -1;
	boolean newLineSent = false;
	byte endOfInput = -1;
	byte newLine = 10;
	boolean enterAlreadyPressedEarlier = false;

	long Id_keyPressed = 0;
	long Id_getNextByteFromJTextArea = 0;
	long Id_outputToJTextArea = 0;

	public void actionPerformed(ActionEvent ae)
	{
		int cCurrPos = jta.getCaretPosition();
		jta.selectAll();
		jta.copy();
		jta.select(cCurrPos, cCurrPos);
	} // end of actionPerformed

	public void keyTyped(KeyEvent ke)
	{
	} // end of keyTyped

	public void keyReleased(KeyEvent ke)
	{
	} // end of keyReleased

	public void keyPressed(KeyEvent ke)
	{
		Id_keyPressed = Thread.currentThread().getId();
		int keyCode = ke.getKeyCode();
		if ((keyCode == PG_UP) || (keyCode == PG_DN) || (keyCode == UP_ARROW) ||
				(keyCode == DOWN_ARROW) || ((keyCode == A) && (ke.getModifiersEx() == CTRL)))
		{
			ke.consume();
		}
		else if ((keyCode == LEFT_ARROW) || (keyCode == BACKSPACE) ||
				((keyCode == H) && (ke.getModifiersEx() == CTRL)))
		{
			synchronized (lock1)
			{
				if (jta.getCaretPosition() <= initialCaretPosition)
				{
					ke.consume();
				}
			} // end of synchronized block
		}
		else if (keyCode == HOME)
		{
			synchronized (lock1)
			{
				jta.setCaretPosition(initialCaretPosition);
				ke.consume();
			} // end of synchronized block
		}
		else if (keyCode == END)
		{
			synchronized (lock1)
			{
				jta.setCaretPosition(jta.getDocument().getLength());
				ke.consume();
			} // end of synchronized block
		}
		else if (keyCode == ENTER)
		{
			// this if block should not exit until all the input has been
			// processed.
			synchronized (lock1)
			{
				inputAvailable = true;
				endOfInputCaretPosition = jta.getDocument().getLength();
				//if ((endOfInputCaretPosition - initialCaretPosition) == 1) {
				// only newline was entered, so increment initialCaretPosition
				if ((enterAlreadyPressedEarlier == true) &&
						(endOfInputCaretPosition - initialCaretPosition) > 0)
				{
					// need to increment initialCaretPosition by 1 to account for last enter pressed
					initialCaretPosition++;
				}
				jta.setCaretPosition(jta.getDocument().getLength());
				enterAlreadyPressedEarlier = true;
				lock1.notifyAll();
			}
			// wait until all input has been processed
			synchronized (lock2)
			{
				//if (Thread.holdsLock(lock2) == true) { System.out.println("Thread id: " + Thread.currentThread().getId() + ", lock2 is held"); } else { System.out.println("Thread id: " + Thread.currentThread().getId() + ", lock2 is _not_ held"); }
				try
				{
					lock2.wait();
				}
				catch (Exception e)
				{
					//System.out.println("Exception (debug:1): " + e.getMessage());
				}
			}
		} // end of if else if
	} // end of keyPressed

	byte getNextByteFromJTextArea()
	{
		String s = "";
		Id_getNextByteFromJTextArea = Thread.currentThread().getId();
		synchronized (lock1)
		{
			//if (Thread.holdsLock(lock1) == true) { System.out.println("Thread id: " + Thread.currentThread().getId() + ", lock1 is held"); } else { System.out.println("Thread id: " + Thread.currentThread().getId() + ", lock1 is _not_ held"); }
			if (inputAvailable == false)
			{
				try
				{
					lock1.wait();
				}
				catch (Exception e)
				{
					//System.out.println("Excpetion (debug:2): " + e.getMessage());
					//System.exit(1);
				} // end of try catch
			} // end of if inputAvailable

			if (newLineSent == true)
			{
				// send endOfInput now, all input has been prcocessed, anyone
				// waiting on lock2 should be woken up and some variables
				// should be re-initialized
				newLineSent = false;
				b = null;
				len = -1;
				indexIntoByteArray = -1;
				inputAvailable = false;
				initialCaretPosition = jta.getDocument().getLength();
				endOfInputCaretPosition = jta.getDocument().getLength();
				synchronized (lock2)
				{
					//if (Thread.holdsLock(lock2) == true) {
					//    System.out.println("lock2 is held..2..Thread id = " + Thread.currentThread().getId());
					//} else {
					//    System.out.println("lock2 is ___not___ held..2..Thread id = " + Thread.currentThread().getId());
					//}
					lock2.notifyAll();
					return endOfInput;
				}
			} // end of if newLineSent

			if (len == -1)
			{ // read input
				len = endOfInputCaretPosition - initialCaretPosition;
				try
				{
					s = jta.getText(initialCaretPosition, len);
					b = s.getBytes(); // enter is still getting processed, the text area
					// hasn't been updated with the enter, so send a
					// newline once all bytes have been sent.
				}
				catch (Exception e)
				{
					//System.out.println("Exception (debug:3): " + e.getMessage());
					if (b != null)
					{
						Arrays.fill(b, (byte) (-1));
					}
				} // end of try catch
			} // end of if len == -1

			// if control reaches here then it means that we have to send a byte
			indexIntoByteArray++;
			if (indexIntoByteArray == len)
			{ // send newLine as all input have been sent already
				newLineSent = true;
				return newLine;
			}
			if (b[indexIntoByteArray] == newLine)
			{
				newLineSent = true;
			}
			return b[indexIntoByteArray];
		} // end of synchronized block
	} // end of getNextByteFromJTextArea

	void outputToJTextArea(byte b)
	{
		Id_outputToJTextArea = Thread.currentThread().getId();
		synchronized (lock1)
		{
			char ch = (char) (b);
			String text = Character.toString(ch);
			jta.append(text);
			jta.setCaretPosition(jta.getDocument().getLength());
			initialCaretPosition = jta.getCaretPosition();
			enterAlreadyPressedEarlier = false;
		}
	} // end of outputToJTextArea

	void configureJTextAreaForInputOutput()
	{
		jta.addKeyListener(this);

		// remove all mouse listeners
		for (MouseListener listener : jta.getMouseListeners())
		{
			//outputToJTextArea(jta, "\nRemoving mouse listener\n");
			jta.removeMouseListener(listener);
		}

		// remove all mouse motion listeners
		for (MouseMotionListener listener : jta.getMouseMotionListeners())
		{
			//outputToJTextArea(jta, "\nRemoving mouse motion listener\n");
			jta.removeMouseMotionListener(listener);
		}

		// remove all mouse wheel listeners
		for (MouseWheelListener listener : jta.getMouseWheelListeners())
		{
			//outputToJTextArea(jta, "\nRemoving mouse wheel listener\n");
			jta.removeMouseWheelListener(listener);
		}

		System.setIn(new InputStream()
		{
			@Override
			public int read()
			{
				// we need to sleep here because of some threading issues
				//try {
				//    Thread.sleep(1);
				//} catch (Exception e) {
				//System.out.println("Exception (debug:4): " + e.getMessage());
				//}
				byte b = getNextByteFromJTextArea();
				return ((int) (b));
			}
		});

		System.setOut(new PrintStream(new OutputStream()
		{
			@Override
			public void write(int b)
			{
				outputToJTextArea((byte) (b));
			}
		}));

		System.setErr(new PrintStream(new OutputStream()
		{
			@Override
			public void write(int b)
			{
				outputToJTextArea((byte) (b));
			}
		}));

	} // end of configureJTextAreaForInputOutput

	public void createAndShowConsole()
	{
		title = "console.Console";
		jf = InitComponents.setupJFrameAndGet(
				title,
				(3 * screenWidth) / 4,
				(3 * screenHeight) / 4
		);

		jta = InitComponents.setupJTextAreaAndGet(
				"",
				5000,
				100,
				true,
				true,
				true,
				false,
				0,
				0,
				0,
				0
		);
		configureJTextAreaForInputOutput();

		jsp = InitComponents.setupScrollableJTextAreaAndGet(
				jta,
				10,
				10,
				(3 * screenWidth) / 4 - 33,
				(3 * screenHeight) / 4 - 79
		);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jf.add(jsp);
		//jf.setLocation(screenWidth / 5, screenHeight / 6);

		jmb = InitComponents.setupJMenuBarAndGet();
		jm = InitComponents.setupJMenuAndGet("Copy All to Clipboard");
		jm.setBorder(BorderFactory.createLineBorder(Color.green, 2));
		jmi = InitComponents.setupJMenuItemAndGet("Copy All to Clipboard");
		jm.add(jmi);
		jmb.add(jm);
		jmi.addActionListener(this);
		jf.setJMenuBar(jmb);

		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
	} // end of createAndShowConsole

} // end of console.Console

class InitComponents
{

	public static JFrame setupJFrameAndGet(
			String title,
			int width,
			int height
	)
	{
		JFrame tmpJF = new JFrame(title);
		tmpJF.setSize(width, height);
		tmpJF.setLocationRelativeTo(null);
		tmpJF.setLayout(null);
		tmpJF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return tmpJF;
	} // end of setupJFrameAndGet

	public static JTextArea setupJTextAreaAndGet(
			String text,
			int rows,
			int columns,
			boolean setEditableFlag,
			boolean setLineWrapFlag,
			boolean setWrapStyleWordFlag,
			boolean setBoundsFlag,
			int xpos,
			int ypos,
			int width,
			int height
	)
	{
		JTextArea tmpJTA = new JTextArea(text, rows, columns);
		tmpJTA.setEditable(setEditableFlag);
		tmpJTA.setLineWrap(setLineWrapFlag);
		tmpJTA.setWrapStyleWord(setWrapStyleWordFlag);
		if (setBoundsFlag == true)
		{
			tmpJTA.setBounds(xpos, ypos, width, height);
		}
		return tmpJTA;
	} // end of setupJTextAreaAndGet

	public static JScrollPane setupScrollableJTextAreaAndGet(
			JTextArea jta,
			int xpos,
			int ypos,
			int width,
			int height
	)
	{
		JScrollPane tmpJSP = new JScrollPane(jta);
		tmpJSP.setBounds(xpos, ypos, width, height);
		return tmpJSP;
	} // end of setupScrollableJTextAreaAndGet

	public static JMenuBar setupJMenuBarAndGet()
	{
		JMenuBar tmpJMB = new JMenuBar();
		return tmpJMB;
	} // end of setupJMenuBarAndGet

	public static JMenu setupJMenuAndGet(String text)
	{
		JMenu tmpJM = new JMenu(text);
		return tmpJM;
	} // end of setupJMenuAndGet

	public static JMenuItem setupJMenuItemAndGet(String text)
	{
		JMenuItem tmpJMI = new JMenuItem(text);
		return tmpJMI;
	} // end of setupJMenuItemAndGet

}// end of console.InitComponents
