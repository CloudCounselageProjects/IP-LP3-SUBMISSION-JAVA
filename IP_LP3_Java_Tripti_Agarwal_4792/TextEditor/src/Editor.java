import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.UndoManager;
public class Editor extends JFrame implements ActionListener{//extends class and implements the interface
	JFrame f;
	JTextArea text;
	JScrollPane scrolltext;
	String t;
	UndoManager um;
	String fileName="Untitled";
	String applicationName="Notepad";
	String filepath;
	JLabel statusBar;
	JMenuItem wordwrapItem;
	boolean wordWrapOn=false;
	FindandReplace findreplacedialogbox= null;
	JFontChooser fontDialogBox= null;
	Editor(){ //constructors
		f= new JFrame(fileName+" - "+applicationName);
		text= new JTextArea();
		text.setFont(new Font("Sans_Serif",Font.PLAIN,20));
		statusBar=new JLabel("       Line 1, Column 1  ",JLabel.RIGHT);
		f.add(statusBar,BorderLayout.SOUTH);
		f.add(new JLabel("  "),BorderLayout.EAST);
		f.add(new JLabel("  "),BorderLayout.WEST);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		statusBar.setVisible(false);
		text.addCaretListener(
				new CaretListener()
				{
					public void caretUpdate(CaretEvent e)
					{
					int lineNumber=0, column=0, pos=0;
					
					try
					{
						pos=text.getCaretPosition();
						lineNumber=text.getLineOfOffset(pos);
						column=pos-text.getLineStartOffset(lineNumber);
						}catch(Exception excp){}
						if(text.getText().length()==0){lineNumber=0; column=0;}
						statusBar.setText("       Line "+(lineNumber+1)+", Column "+(column+1));
					}
					});
		scrolltext = new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrolltext.setBorder(BorderFactory.createEmptyBorder());
		f.add(scrolltext);
		 um = new UndoManager();
		text.getDocument().addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				um.addEdit(e.getEdit());
			}
		});
		f.addWindowListener(new WindowAdapter() { // add window listener to frame when close the frame
			public void windowClosing(WindowEvent w) {
				int result=JOptionPane.showConfirmDialog(null, "Do you want to save changes?", "Notepad",JOptionPane.YES_NO_CANCEL_OPTION);
				if(result==JOptionPane.YES_OPTION) {
					String filenames= f.getTitle();
					if(filenames.equals("Untitled - Notepad")) {
						savetheFile();
					}
					else if(filepath!=null) {
						File myObj = new File(filepath);
						FileWriter filewriter;
						try {
							filewriter = new FileWriter(myObj, false);
							BufferedWriter br = new BufferedWriter(filewriter);
							br.write(text.getText());
							br.close();
							filewriter.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				else if(result==JOptionPane.NO_OPTION) {
					System.exit(0);
				}
				else {
					f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				}
				
			}
		});
		// create menus and give shortcut keys
        JMenuBar mb= new JMenuBar();
		JMenu m1= new JMenu("File");
		JMenuItem newItem = new JMenuItem("New");
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));
		JMenuItem openItem = new JMenuItem("Open");
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
		JMenuItem saveasItem = new JMenuItem("Save As");
		saveasItem.setMnemonic(KeyEvent.VK_S);
		saveasItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,ActionEvent.CTRL_MASK));
		JMenuItem printItem = new JMenuItem("Print");
		printItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.CTRL_MASK));
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0));
		newItem.addActionListener(this);
		openItem.addActionListener(this);
		saveItem.addActionListener(this);
		saveasItem.addActionListener(this);
		printItem.addActionListener(this);
		exitItem.addActionListener(this);
		m1.add(newItem);
		m1.add(openItem);
		m1.add(saveItem);
		m1.add(saveasItem);
		m1.add(printItem);
		m1.add(exitItem);
		JMenu m2= new JMenu("Edit");
		JMenuItem undoItem = new JMenuItem("Undo");
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,ActionEvent.CTRL_MASK));
		JMenuItem cutItem = new JMenuItem("Cut");
		cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK));
		JMenuItem copyItem = new JMenuItem("Copy");
		copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK));
		JMenuItem pasteItem = new JMenuItem("Paste");
		pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK));
		JMenuItem deleteItem = new JMenuItem("Delete");
		deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
		JMenuItem findItem = new JMenuItem("Find");
		findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.CTRL_MASK));
		JMenuItem findnextItem = new JMenuItem("Find Next");
		findnextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0));
		JMenuItem replaceItem = new JMenuItem("Replace");
		replaceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,ActionEvent.CTRL_MASK));
		JMenuItem gotoItem = new JMenuItem("Go to");
		gotoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,ActionEvent.CTRL_MASK));
		JMenuItem selectallItem = new JMenuItem("Select All");
		selectallItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
		JMenuItem timedateItem = new JMenuItem("Time/Date");
		timedateItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0));
		undoItem.addActionListener(this);
		cutItem.addActionListener(this);
		copyItem.addActionListener(this);
		pasteItem.addActionListener(this);
		deleteItem.addActionListener(this);
		findItem.addActionListener(this);
		findnextItem.addActionListener(this);
		replaceItem.addActionListener(this);
		gotoItem.addActionListener(this);
		selectallItem.addActionListener(this);
		timedateItem.addActionListener(this);
		m2.add(undoItem);
		m2.add(cutItem);
		m2.add(copyItem);
		m2.add(pasteItem);
		m2.add(deleteItem);
		m2.add(findItem);
		m2.add(findnextItem);
		m2.add(replaceItem);
		m2.add(gotoItem);
		m2.add(selectallItem);
		m2.add(timedateItem);
		JMenu m3= new JMenu("Format");
		wordwrapItem = new JMenuItem("Word Wrap:Off");
		wordwrapItem.setActionCommand("Word Wrap");
		JMenuItem fontItem = new JMenuItem("Font");
		JMenuItem textcolorItem = new JMenuItem("Text Color");
		JMenuItem backgroundcolorItem = new JMenuItem("Background Color");
		wordwrapItem.addActionListener(this);
		fontItem.addActionListener(this);
		textcolorItem.addActionListener(this);
		backgroundcolorItem.addActionListener(this);
		m3.add(wordwrapItem);
		m3.add(fontItem);
		m3.add(textcolorItem);
		m3.add(backgroundcolorItem);
		
		
		JMenu m4= new JMenu("View");
		JCheckBoxMenuItem sb=new JCheckBoxMenuItem("Status Bar");
		sb.setSelected(false);
		sb.addActionListener(this);
		m4.add(sb);
		mb.add(m1);
		mb.add(m2);
		mb.add(m3);
		mb.add(m4);
		LookAndFeelMenu.createLookAndFeelMenuItem(m4,this.f);
		f.setJMenuBar(mb);  
        f.setSize(1000, 800);
        f.setVisible(true);
	}
	public static void main(String[] args) {
		Editor e1= new Editor(); // calling the public class editor
	}
	void goTo() // defining method of goto for Go To menu item
	{
		int lineNumber=0;
		try
		{
			lineNumber=text.getLineOfOffset(text.getCaretPosition())+1;
			String tempStr=JOptionPane.showInputDialog(f,"Enter Line Number:",""+lineNumber);
			if(tempStr==null)
				{return;}
			lineNumber=Integer.parseInt(tempStr);
			text.setCaretPosition(text.getLineStartOffset(lineNumber-1));
			}catch(Exception e){}
	}
	void savetheFile() { // method for save as file
		JFileChooser filechooser=new JFileChooser();
		filechooser.addChoosableFileFilter(new MyFileFilter(".java","Java Source Files(*.java)"));
		filechooser.addChoosableFileFilter(new MyFileFilter(".txt","Text Files(*.txt)"));
		filechooser.addChoosableFileFilter(new MyFileFilter(".py","Python Source Files(*.py)"));
		int i=filechooser.showSaveDialog(filechooser);
		if(i==JFileChooser.APPROVE_OPTION) {
			File file = new File(filechooser.getSelectedFile().getAbsolutePath());
			try {
				System.out.println(file.getName());
				FileWriter filewriter = new FileWriter(file, false);
				BufferedWriter br = new BufferedWriter(filewriter);
				br.write(text.getText());
				fileName= file.getName();
				f.setTitle(fileName+"-"+applicationName);
				filepath= file.getPath();
				br.close();
			}
			catch (Exception e1) {
				System.out.println( e1.getMessage());
			}
		}
		else
			System.out.println("User cancel the operation");
		
	}
	@Override
	public void actionPerformed(ActionEvent e) { // perform the action on clicking the menu item
		String s= e.getActionCommand();
		
		if(s.equals("New")) {
			f.dispose();
			Editor ed= new Editor();
		}
		else if(s.equals("Open")) {
			JFileChooser filechooser=new JFileChooser(); 
			int i=filechooser.showOpenDialog(filechooser); 
			
		    if(i==JFileChooser.APPROVE_OPTION){    
		        File file=filechooser.getSelectedFile();    
		        filepath=file.getPath();    
		        try{  
		        BufferedReader br=new BufferedReader(new FileReader(filepath));    
		        String str1="",str2="";   
		        
		        while((str1=br.readLine())!=null){    
		        	str2=str2+str1+"\n";    
		        }    
		        text.setText(str2);    
		        fileName=file.getName();
		        f.setTitle(fileName+" - "+applicationName);
		        br.close();    
		        }catch (Exception ex) {System.out.println(ex);  }                 
		    }    
        }
		else if(s.equals("Save")) {
			String fileNames = f.getTitle();
			if(fileNames.equals("Untitled - Notepad")) {
				savetheFile();
		}
			else{
				File myObj = new File(filepath);
				FileWriter filewriter;
				try {
					filewriter = new FileWriter(myObj, false);
					BufferedWriter br = new BufferedWriter(filewriter);
					br.write(text.getText());
					br.close();
					filewriter.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				
			}
		}
		else if(s.equals("Save As")) {
			savetheFile();
			
		}
		else if(s.equals("Print")) {
			try {
				text.print();
			}catch(Exception e2) {}
		}
		else if(s.equals("Exit")) {
			System.exit(0);
		}
		else if(s.equals("Undo")) {
			um.undo();
		}
		else if(s.equals("Cut")) {
			t= text.getSelectedText();
			text.replaceRange("",text.getSelectionStart(),text.getSelectionEnd());
		}
		else if(s.equals("Copy")) {
			t= text.getSelectedText();
		}
		else if(s.equals("Paste")) {
			text.insert(t, text.getCaretPosition());
		}
		else if(s.equals("Delete")) {
			text.replaceSelection("");	
		}
		else if(s.equals("Find")) {
			if(text.getText().length()==0)
				return;
			if(findreplacedialogbox==null)
				findreplacedialogbox=new FindandReplace(this.text);
			findreplacedialogbox.showDialog(this.f,true);
		}
		else if(s.equals("Find Next")) {
			if(text.getText().length()==0)
				return;	
			
			if(findreplacedialogbox==null)
				statusBar.setText("Nothing to search for, use Find option of Edit Menu first.");
			else
			{
				findreplacedialogbox.findNextWithSelection();
			}
				
		}
		else if(s.equals("Replace")) {
			if(text.getText().length()==0)
				return;
			
				if(findreplacedialogbox==null)
					findreplacedialogbox=new FindandReplace(text);
				findreplacedialogbox.showDialog(f,false);
		}
		else if(s.equals("Go to")) {
			if(text.getText().length()==0)
				return;	
			goTo();
		}
		else if(s.equals("Select All")) {
			text.selectAll();
		}
		
		else if(s.equals("Time/Date")) {
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			text.append(dateFormat.format(date));
		}
		else if(s.equals("Word Wrap")) {
			if(wordWrapOn==false) {
				wordWrapOn= true;
				text.setLineWrap(true);
				text.setWrapStyleWord(true);
				wordwrapItem.setText("Word Wrap:On");
			}
			else if(wordWrapOn==true) {
				wordWrapOn= false;
				text.setLineWrap(false);
				text.setWrapStyleWord(false);
				wordwrapItem.setText("Word Wrap:Off");
			}
		}
		else if(s.equals("Font")) {
			if(fontDialogBox==null)
				fontDialogBox=new JFontChooser(text.getFont());
			
			if(fontDialogBox.showDialog(f,"Choose a Font"))
				text.setFont(fontDialogBox.createFont());
		}
		else if(s.equals("Text Color")) {
			JColorChooser colorchooser= new JColorChooser();
			Color color=JColorChooser.showDialog(null,"Pick a color", Color.black);
			text.setForeground(color);
		}
		else if(s.equals("Background Color")) {
			JColorChooser colorchooser= new JColorChooser();
			Color color=JColorChooser.showDialog(null,"Pick a color", Color.black);
			text.setBackground(color);
		}
		else if(s.equals("Status Bar")) {
			JCheckBoxMenuItem temp=(JCheckBoxMenuItem)e.getSource();
			statusBar.setVisible(temp.isSelected());
		}
		else
			statusBar.setText("This "+s+" command is yet to be implemented");
		}
}
