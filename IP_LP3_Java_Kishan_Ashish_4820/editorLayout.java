import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.swing.KeyStroke;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

import java.util.*;
public class editorLayout implements ActionListener {
	
	//Creating all components 
	JFrame frame;
	JTextArea textarea;
	JMenuBar menubar;
	JMenu file,edit,format,view;
	JMenuItem newitem, open, save, saveas, print, exit;
	JMenuItem undo, cut, copy, paste, delete, find, findnext, replace, gotooption, selectall,timedate;
	JMenuItem wordWrap, font, textColor,backgroundColor;
	JLabel statusBar;
	JScrollPane scrollPane;
	
	JColorChooser textColorChooser=null;
	JColorChooser bgColorChooser=null;
	JDialog backgroundDialog=null;
	JDialog foregroundDialog=null;
	FindDialog findReplaceDialog=null ;
	
	String backgroundColor1;
	String textColor1;
	FontChooser fontchooser;
	
	UndoManager undomanager = new UndoManager();
	
	//Constructor
	public editorLayout() {
		
		// frame create
		frame=new JFrame("Untitled - Notepad");
		//frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
	
		//menu bar and menu items
		menubar=new JMenuBar();
		menubar.setBounds(0, 0, 10, 10);
		
		textarea=new JTextArea(10,20);
		textarea.setBounds(0, 0, 600, 600);
		


		file =new JMenu("File");
		edit=new JMenu("Edit");
		format=new JMenu("Format");
		view=new JMenu("View");
		
		//File menu item created
		newitem=new JMenuItem("New");
		open=new JMenuItem   ("Open");
		save=new JMenuItem   ("Save");
		saveas=new JMenuItem ("Save As");
		print=new JMenuItem  ("Print");
		exit=new JMenuItem   ("Exit");
		
		//Edit menu item created
		undo=new JMenuItem        ("Undo");
		cut=new JMenuItem         ("Cut");
		copy=new JMenuItem        ("Copy");
		paste=new JMenuItem       ("Paste");
		delete=new JMenuItem      ("Delete");
		find=new JMenuItem        ("Find");
		findnext=new JMenuItem    ("Find next");
		replace=new JMenuItem     ("Replace");
		gotooption=new JMenuItem  ("GoTo");
		selectall=new JMenuItem   ("Select All");
		timedate=new JMenuItem    ("Time/Date");
		
		//Format menu item created
		wordWrap=new JMenuItem("Word Wrap");
		font=new JMenuItem("Font");
		textColor=new JMenuItem("Text Color");
		backgroundColor=new JMenuItem("Background Color");
		
		//Adding all components
		menubar.add(file);
		menubar.add(edit);
		menubar.add(format);
		menubar.add(view);
		
		file.add(newitem);
		file.add(open);
		file.add(save);
		file.add(saveas);
		file.add(print);
		file.add(exit);
		
		edit.add(undo);
		edit.add(cut);
		edit.add(copy);
		edit.add(paste);
		edit.add(delete);
		edit.add(find);
		edit.add(findnext);
		edit.add(replace);
		edit.add(gotooption);
		edit.add(selectall);
		edit.add(timedate);
		
		format.add(wordWrap);
		format.add(font);
		format.add(textColor);
		format.add(backgroundColor);
		
		//adding  status bar
		statusBar=new JLabel("||       Ln 1, Col 1  ",JLabel.RIGHT);
		frame.add(new JScrollPane(textarea),BorderLayout.CENTER);
		frame.add(statusBar,BorderLayout.SOUTH);
		frame.add(new JLabel("  "),BorderLayout.EAST);
		frame.add(new JLabel("  "),BorderLayout.WEST);
		
		
		
		// add file menu action listener
		
		newitem.addActionListener(this);
		open.addActionListener(this);
		save.addActionListener(this);
		saveas.addActionListener(this);
		print.addActionListener(this);
		exit.addActionListener(this);
		
		
		// add edit menu action listener
		
		undo.addActionListener(this);
		cut.addActionListener(this);
		copy.addActionListener(this);
		paste.addActionListener(this);
		delete.addActionListener(this);
		find.addActionListener(this);
		findnext.addActionListener(this);
		replace.addActionListener(this);
		gotooption.addActionListener(this);
		selectall.addActionListener(this);
		timedate.addActionListener(this);
		
		// add Format menu action listener
		
		wordWrap.addActionListener(this);
		font.addActionListener(this);
		textColor.addActionListener(this);
		backgroundColor.addActionListener(this);
		
		
		// undo manager 
		
		undomanager = new UndoManager();
		textarea.getDocument().addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent event) {
				undomanager.addEdit(event.getEdit());
			}
		});
		
		
		//add scroll panel
		
		scrollPane=new JScrollPane(
				textarea, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		frame.add(scrollPane);
		
	
		
		//Add shortcut key
		newitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.CTRL_MASK));
		find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		findnext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, ActionEvent.CTRL_MASK));
		replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		gotooption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		selectall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		timedate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,ActionEvent.CTRL_MASK));
		
		
		//Add confirm save dialog box
		frame.addWindowListener(new WindowAdapter() {
		      public void windowClosing(WindowEvent we) {
		        int result = JOptionPane.showConfirmDialog(frame,
		            "Do you want to Save this file ?", "Save Confirmation : ",
		            JOptionPane.YES_NO_OPTION);
		        if (result == JOptionPane.YES_OPTION) {
		        	 // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        	if(frame.getTitle()=="Untitled - Notepad") {
		        		saveas();
		        		
		        	}else {
		        		save();
		        	}
		        		
		        }
		        else if (result == JOptionPane.NO_OPTION)
		          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		      }
		    });
		
		
		
		
		
		
		frame.setJMenuBar(menubar);
		frame.setBounds(0, 0, 600,600);
		frame.setVisible(true);
		
		}//This is end line of Constructor


	
//------------------start action performed method---------------
	
	public void actionPerformed(ActionEvent ae) {
			
		//----------File menu item click event-------------------
		
		if(ae.getSource()==newitem) {
			frame.dispose();
			new editorLayout();
			//System.out.println("This is new tab");
		}  
		else if(ae.getSource()==open){    
			OpenItem();
		
		}
		else if(ae.getSource()==save) {
			
			String fileNames = frame.getTitle();
			System.out.println(fileNames);
			if(fileNames.equals("Untitled - Notepad")) {
				//System.out.println("save");
				saveas();
			}
			else {	save();	} 
		}
		else if(ae.getSource()==saveas) {
		//	System.out.println("This is save as ");
			 saveas();
					
		}
		else if(ae.getSource()==print) {
			//System.out.println("this is print menu");
			try {
				// print the file 
				textarea.print();
			}
			catch (Exception evt) {
				JOptionPane.showMessageDialog(frame, evt.getMessage());
			}

		}
		else if(ae.getSource()==exit) {
			frame.dispose();
		}

//-----------------------------------------------------------------------------
//*****************************************************************************
		
	
		
		//---------------Edit and Format Sub Menu click event-----------
		
		if(ae.getSource()==undo) {
			undomanager.undo();
			
		}
		else if(ae.getSource()==cut) {
			textarea.cut();
			
		}
		else if(ae.getSource()==copy) {
			textarea.copy();
		}
		else if(ae.getSource()==paste) {
			textarea.paste();
		}
		else if(ae.getSource()==delete) {
			textarea.replaceSelection("");
			
		}
		else if(ae.getSource()==find) {
			
			if(editorLayout.this.textarea.getText().length()==0)
				return;	// text box have no text
			else{//(findReplaceDialog==null)
				findReplaceDialog=new FindDialog(editorLayout.this.textarea);
			findReplaceDialog.showDialog(editorLayout.this.frame,true);//find
			}
		}
		else if(ae.getSource()==findnext) {
			
			if(editorLayout.this.textarea.getText().length()==0)
				return;	// text box have no text
			
			if(findReplaceDialog==null)
				statusBar.setText("Nothing to search for, use Find option of Edit Menu first !!!!");
			else
				findReplaceDialog.findNextWithSelection();

		}
		else if(ae.getSource()==replace) {
			ReplaceItem();
			
		}
		else if(ae.getSource()==gotooption) {
		
			gotoOption();

		}
		else if(ae.getSource()==selectall) {
			textarea.selectAll();
		}
		else if(ae.getSource()==timedate) {
			SimpleDateFormat formatter= new SimpleDateFormat(" hh:mm aa MM/dd/yyyy");
		//	System.out.println();
			textarea.insert( formatter.format(new Date()),textarea.getSelectionStart());
		}
		
		else if(ae.getSource()==wordWrap) {
		//	System.out.println("This is word wrap");
			textarea.setLineWrap(true);
			textarea.setWrapStyleWord(true);
		}
		else if(ae.getSource()==font) {
			FontItem();
				
		}
		else if(ae.getSource()==textColor) {
			TextColorItem();
		}
		
		else if(ae.getSource()==backgroundColor) {
			BgColorItem();
		}
		else if(ae.getSource()==view) {
			JCheckBoxMenuItem status=(JCheckBoxMenuItem)ae.getSource();
			statusBar.setVisible(status.isSelected());
		}
	}

	

//----------------------------------------------------------------------
//						All method
//----------------------------------------------------------------------

//---------------------Open event method--------------------------------
	
	private void OpenItem() {
		// TODO Auto-generated method stub
		JFileChooser filechooser=new JFileChooser(); 
		int i=filechooser.showOpenDialog(filechooser); 
		
	    if(i==JFileChooser.APPROVE_OPTION){    
	        File file=filechooser.getSelectedFile();   
	        frame.setTitle(file.getName());
	        String filepath=file.getPath();    
	        try{  
	        BufferedReader br=new BufferedReader(new FileReader(filepath));    
	        String str1="",str2="";   
	        
	        while((str1=br.readLine())!=null){    
	        	str2=str2+str1+"\n";    
	        }    
	        textarea.setText(str2);    
	        br.close();    
	        }catch (Exception ex) {System.out.println(ex);  }                 
	    }    
		
	}


//----------------Replace event method-----------------

	private void ReplaceItem() {
		// TODO Auto-generated method stub
		if(editorLayout.this.textarea.getText().length()==0)
			return;	// text box have no text
		
		if(findReplaceDialog==null)
			findReplaceDialog=new FindDialog(editorLayout.this.textarea);
		findReplaceDialog.showDialog(editorLayout.this.frame,false);//replace
		
	}



//----------------Set font style event method-----------------
	
	private void FontItem() {
		// TODO Auto-generated method stub
		if(font!=null) {
			fontchooser=new FontChooser(textarea.getFont());
		}

		if(fontchooser.showDialog(editorLayout.this.frame,"Choose a font")) {
			editorLayout.this.textarea.setFont(fontchooser.createFont());
		}
		
	}



//----------------Set background color event method-----------------
	
	private void BgColorItem() {
		// TODO Auto-generated method stub
		if(bgColorChooser==null)
			bgColorChooser=new JColorChooser();
		if(backgroundDialog==null)
			backgroundDialog=JColorChooser.createDialog
				(editorLayout.this.frame,
				backgroundColor1,
				false,
				bgColorChooser,
				new ActionListener()
				{public void actionPerformed(ActionEvent evvv){
					editorLayout.this.textarea.setBackground(bgColorChooser.getColor());}},
				null);		

		backgroundDialog.setVisible(true);
		
	}



//----------------Set font color event method-----------------
	
	private void TextColorItem() {
		// TODO Auto-generated method stub
		if(textColorChooser==null)
			textColorChooser=new JColorChooser();
		if(foregroundDialog==null)
			foregroundDialog=JColorChooser.createDialog
				(editorLayout.this.frame,
				textColor1,
				false,
				textColorChooser,
				new ActionListener()
				{public void actionPerformed(ActionEvent evvv){
					editorLayout.this.textarea.setForeground(textColorChooser.getColor());}},
				null);		

		foregroundDialog.setVisible(true);
		
	}



//----------------GoTo event method-----------------
	
	private void gotoOption() {
		// TODO Auto-generated method stub
		int lineNum=0;
		try
		{
			
			String str=JOptionPane.showInputDialog(frame,"Enter Line Number:",""+lineNum);
			lineNum=textarea.getLineOfOffset(textarea.getCaretPosition())+1;
			lineNum=Integer.parseInt(str);
		
			if(lineNum>0) {
				//System.out.println(gotoline+"  "+lineNum);
				textarea.setCaretPosition(textarea.getLineStartOffset(lineNum-1));
				
			}else {
				JOptionPane.showMessageDialog(frame, "Invalid Line Number");
				
			}
		
		}catch(Exception e){
			JOptionPane.showMessageDialog(frame, e.getMessage());
			//System.out.println(e);
		}
		
	}



//----------------Save event method-----------------
	
	private void save() {
		// TODO Auto-generated method stub

		File myObj = new File(frame.getTitle());
		FileWriter filewriter;
		try {
			filewriter = new FileWriter(myObj, false);
			BufferedWriter br = new BufferedWriter(filewriter);
			br.write(textarea.getText());
			br.close();
			filewriter.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		
	}



//----------------Save As event method-----------------
	
	private void saveas() {
		// TODO Auto-generated method stub
		JFileChooser filechooser=new JFileChooser();
		
		filechooser.addChoosableFileFilter(new fileFilter(".java","Java Source Files(*.java)"));
		filechooser.addChoosableFileFilter(new fileFilter(".txt","Text Files(*.txt)"));
		filechooser.addChoosableFileFilter(new fileFilter(".py","Python Source Files(*.py)"));
		
		int i=filechooser.showSaveDialog(filechooser);

		if(i==JFileChooser.APPROVE_OPTION) {
			
			File file = new File(filechooser.getSelectedFile().getAbsolutePath());
			try {
				System.out.println(file);
				FileWriter filewriter = new FileWriter(file, false);
				BufferedWriter br = new BufferedWriter(filewriter);
				br.write(textarea.getText());
				
				frame.setTitle(file.getName());
				
				br.close();
			}
			catch (Exception e) {
				System.out.println( e.getMessage());
			}
		}
		else
			System.out.println("User cancle the operation");
		
	}
	 
	
}// end line of editorLayour class
