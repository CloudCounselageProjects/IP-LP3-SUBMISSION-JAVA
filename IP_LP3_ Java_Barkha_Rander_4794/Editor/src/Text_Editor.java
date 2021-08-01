import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


//created filehandling class
class FileHandling {

	Text_Editor te;
	boolean saved;
	boolean newFileFlag;
	String fileName;
	String applicationTitle="Text Editor";
	
	File fileRef;
	JFileChooser chooser;
	
	boolean isSave(){return saved;}
	void setSave(boolean saved){this.saved=saved;}
	String getFileName(){return new String(fileName);}
	void setFileName(String fileName){this.fileName=new String(fileName);}
	FileHandling(Text_Editor te)
	{
		this.te=te;
		saved=true;
		newFileFlag=true;
		fileName=new String("New File");
		fileRef=new File(fileName);
		this.te.f.setTitle(fileName+" - "+applicationTitle);
		chooser=new JFileChooser();
		chooser.addChoosableFileFilter(new FileChange(".java","Java Source Files(*.java)"));
		chooser.addChoosableFileFilter(new FileChange(".txt","Text Files(*.txt)"));
		chooser.addChoosableFileFilter(new FileChange(".py","Python Source Files(*.py)"));
		chooser.setCurrentDirectory(new File("."));
	
	}
	//function to check file is save or not 
	boolean saveFile(File save)
	{
		FileWriter fout=null;
		try
		{
		fout=new FileWriter(save);
		fout.write(te.ta.getText());
		}
		catch(IOException ioe){updateStatus(save,false);return false;}
		finally
		{try{fout.close();}catch(IOException excp){}}
		updateStatus(save,true);
		return true;
	}
	boolean saveThisFile()
	{
		if(!newFileFlag)
			{return saveFile(fileRef);}
		
		return saveAsFile();
	}
	//function for save as and check
	boolean saveAsFile()
	{
		File saveas=null;
		chooser.setDialogTitle("Save As");
		chooser.setApproveButtonText("Save Now"); 
		chooser.setApproveButtonMnemonic(KeyEvent.VK_S);
		chooser.setApproveButtonToolTipText("Click Me to Save!");

		do
		{
			if(chooser.showSaveDialog(this.te.f)!=JFileChooser.APPROVE_OPTION)
				return false;
				saveas=chooser.getSelectedFile();
			if(!saveas.exists()) break;
			if(JOptionPane.showConfirmDialog(this.te.f,saveas.getPath()+" already exists.\nDo you want to replace it?","Save As",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
				break;
			}while(true);
		return saveFile(saveas);
		}
	//function for open file
	boolean openFile(File open)
	{
		FileInputStream fin=null;
		BufferedReader din=null;
		
		try
		{
			fin=new FileInputStream(open);
			din=new BufferedReader(new InputStreamReader(fin));
			String str=" ";
			while(str!=null)
			{
				str=din.readLine();
				if(str==null)
				break;
				this.te.ta.append(str+"\n");
			}
		
		}
		catch(IOException ioe){updateStatus(open,false);return false;}
		finally
		{try{din.close();fin.close();}catch(IOException excp){}}
		updateStatus(open,true);
		this.te.ta.setCaretPosition(0);
		return true;
	}
	void openFile()
	{
		if(!confirmSave()) return;
		chooser.setDialogTitle("Open File");
		chooser.setApproveButtonText("Open this"); 
		chooser.setApproveButtonMnemonic(KeyEvent.VK_O);
		chooser.setApproveButtonToolTipText("Click me to open the selected file.!");

		File open1=null;
		do
		{
		if(chooser.showOpenDialog(this.te.f)!=JFileChooser.APPROVE_OPTION)
			return;
		open1=chooser.getSelectedFile();
		
		if(open1.exists())	break;
		
		JOptionPane.showMessageDialog(this.te.f,open1.getName()+"\nfile not found.\n"+"Please verify the correct file name was given.","Open",	JOptionPane.INFORMATION_MESSAGE);
		
		} while(true);
		
		this.te.ta.setText("");
		
		if(!openFile(open1))
			{
			fileName="New File"; saved=true; 
			this.te.f.setTitle(fileName+" - "+applicationTitle);
			}
		if(!open1.canWrite())
			newFileFlag=true;
		
		}
	void updateStatus(File status1,boolean saved)
	{
	if(saved)
	{
		this.saved=true;
		fileName=new String(status1.getName());
		if(!status1.canWrite())
			{fileName+="(Read only)"; newFileFlag=true;}
		fileRef=status1;
		te.f.setTitle(fileName + " - "+applicationTitle);
		te.statusBar.setText("File : "+status1.getPath()+" saved/opened successfully.");
		newFileFlag=false;
	}
	else
	{
		te.statusBar.setText("Failed to save/open : "+status1.getPath());
	}
	}
	//function to show confirm message dialog
	boolean confirmSave()
	{
		String strMsg="The text in the "+fileName+" file has been changed."+"Do you want to save the changes?";
		if(!saved)
		{
			int x=JOptionPane.showConfirmDialog(this.te.f,strMsg,applicationTitle,JOptionPane.YES_NO_CANCEL_OPTION);
			
			if(x==JOptionPane.CANCEL_OPTION) return false;
			if(x==JOptionPane.YES_OPTION && !saveAsFile()) return false;
		}
		return true;
	}
	//function to open new file 
	void newFile()
	{
		if(!confirmSave()) return;
		
		this.te.ta.setText("");
		fileName=new String("New File");
		fileRef=new File(fileName);
		saved=true;
		newFileFlag=true;
		this.te.f.setTitle(fileName+" - "+applicationTitle);
	}
}//end of file handling class

public class Text_Editor  implements ActionListener, Menubar
{

	public JFrame f;
	public JTextArea ta;
	public JLabel statusBar;
	
	private String fileName="Untitled";
	private boolean saved=true;
	String applicationName="Notepad";
	
	String searchString, replaceString;
	int lastSearchIndex;
	
	FileHandling fileHandler;
	FontStyle fontDialog=null;
	FindReplace findReplaceDialog=null; 
	JColorChooser bcolorChooser=null;
	JColorChooser fcolorChooser=null;
	JDialog backgroundDialog=null;
	JDialog foregroundDialog=null;
	JMenuItem cutItem,copyItem, deleteItem, findItem, findNextItem, replaceItem, gotoItem, selectAllItem;

	Text_Editor()
	{
		f=new JFrame(fileName+" - "+applicationName);
		ta=new JTextArea(30,60);
		statusBar=new JLabel("       Line 1, Column 1  ",JLabel.RIGHT);
		f.add(new JScrollPane(ta),BorderLayout.CENTER);
		f.add(statusBar,BorderLayout.SOUTH);
		f.add(new JLabel("  "),BorderLayout.EAST);
		f.add(new JLabel("  "),BorderLayout.WEST);
		createMenuBar(f);
		f.pack();
		f.setLocation(100,50);
		f.setVisible(true);
		f.setLocation(150,50);
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		fileHandler=new FileHandling(this);
		//it shows our text position and we update on status bar
		ta.addCaretListener(
	new CaretListener()
	{
		public void caretUpdate(CaretEvent e)
		{
		int lineNumber=0, column=0, pos=0;
		
		try
		{
			pos=ta.getCaretPosition();
			lineNumber=ta.getLineOfOffset(pos);
			column=pos-ta.getLineStartOffset(lineNumber);
			}catch(Exception excp){}
			if(ta.getText().length()==0){lineNumber=0; column=0;}
			statusBar.setText("       Line "+(lineNumber+1)+", Column "+(column+1));
		}
		});

	DocumentListener myListener = new DocumentListener()
	{
		public void changedUpdate(DocumentEvent e){fileHandler.saved=false;}
		public void removeUpdate(DocumentEvent e){fileHandler.saved=false;}
		public void insertUpdate(DocumentEvent e){fileHandler.saved=false;}
	};
	ta.getDocument().addDocumentListener(myListener);

	WindowListener frameClose=new WindowAdapter()
	{
	public void windowClosing(WindowEvent we)
	{
		if(fileHandler.confirmSave())System.exit(0);
	}
	};
		f.addWindowListener(frameClose);
	}
	//function for goto menu 
	void goTo()
	{
		int lineNumber=0;
		try
		{
			lineNumber=ta.getLineOfOffset(ta.getCaretPosition())+1;
			String tempStr=JOptionPane.showInputDialog(f,"Enter Line Number:",""+lineNumber);
			if(tempStr==null)
				{return;}
			lineNumber=Integer.parseInt(tempStr);
			ta.setCaretPosition(ta.getLineStartOffset(lineNumber-1));
			}catch(Exception e){}
	}

public void actionPerformed(ActionEvent e1)
{
	String cmdText=e1.getActionCommand();
	
	if(cmdText.equals(fileNew))
		fileHandler.newFile();
	else if(cmdText.equals(fileOpen))
		fileHandler.openFile();
	else if(cmdText.equals(fileSave))
		fileHandler.saveThisFile();
	else if(cmdText.equals(fileSaveAs))
		fileHandler.saveAsFile();
	else if(cmdText.equals(fileExit))
		{if(fileHandler.confirmSave())System.exit(0);}
	else if(cmdText.equals(filePrint))
	JOptionPane.showMessageDialog(Text_Editor.this.f,"Get your printer repaired first! It seems that you havn't Connected it","Bad Printer",JOptionPane.INFORMATION_MESSAGE);
	else if(cmdText.equals(editCut))
		ta.cut();
	else if(cmdText.equals(editCopy))
		ta.copy();
	else if(cmdText.equals(editPaste))
		ta.paste();
	else if(cmdText.equals(editDelete))
		ta.replaceSelection("");
	else if(cmdText.equals(editFind))
	{
		if(Text_Editor.this.ta.getText().length()==0)
			return;	
		if(findReplaceDialog==null)
			findReplaceDialog=new FindReplace(Text_Editor.this.ta);
			findReplaceDialog.showDialog(Text_Editor.this.f,true);
	}
	else if(cmdText.equals(editFindNext))
	{
		if(Text_Editor.this.ta.getText().length()==0)
			return;	
		
		if(findReplaceDialog==null)
			statusBar.setText("Nothing to search for, use Find option of Edit Menu first.");
		else
			findReplaceDialog.findNextWithSelection();
	}
	else if(cmdText.equals(editReplace))
	{
		if(Text_Editor.this.ta.getText().length()==0)
		return;
	
		if(findReplaceDialog==null)
			findReplaceDialog=new FindReplace(Text_Editor.this.ta);
			findReplaceDialog.showDialog(Text_Editor.this.f,false);
	}

	else if(cmdText.equals(editGoTo))
	{
	if(Text_Editor.this.ta.getText().length()==0)
		return;	
	goTo();
	}

	else if(cmdText.equals(editSelectAll))
		ta.selectAll();
	else if(cmdText.equals(editTimeDate))
		ta.insert(new Date().toString(),ta.getSelectionStart());
	else if(cmdText.equals(formatWordWrap))
	{
		JCheckBoxMenuItem temp=(JCheckBoxMenuItem)e1.getSource();
		ta.setLineWrap(temp.isSelected());
	}
	else if(cmdText.equals(formatFont))
	{
		if(fontDialog==null)
			fontDialog=new FontStyle(ta.getFont());
		
		if(fontDialog.showDialog(Text_Editor.this.f,"Choose a Font"))
			Text_Editor.this.ta.setFont(fontDialog.createFont());
	}
	else if(cmdText.equals(formatForeground))
		showForegroundColorDialog();
	else if(cmdText.equals(formatBackground))
		showBackgroundColorDialog();
	else if(cmdText.equals(viewStatusBar))
	{
	JCheckBoxMenuItem temp=(JCheckBoxMenuItem)e1.getSource();
	statusBar.setVisible(temp.isSelected());
	}
	else
		statusBar.setText("This "+cmdText+" command is yet to be implemented");
	}
	void showBackgroundColorDialog()
	{
		if(bcolorChooser==null)
			bcolorChooser=new JColorChooser();
		if(backgroundDialog==null)
			backgroundDialog=JColorChooser.createDialog(Text_Editor.this.f,formatBackground,false,bcolorChooser,new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Text_Editor.this.ta.setBackground(bcolorChooser.getColor());}},null);		
					backgroundDialog.setVisible(true);
	}

	void showForegroundColorDialog()
	{
	if(fcolorChooser==null)
		fcolorChooser=new JColorChooser();
	if(foregroundDialog==null)
		foregroundDialog=JColorChooser.createDialog(Text_Editor.this.f,formatForeground,false,fcolorChooser,new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Text_Editor.this.ta.setForeground(fcolorChooser.getColor());}},null);		
				foregroundDialog.setVisible(true);
	}

	JMenuItem createMenuItem(String s, int key,JMenu toMenu,ActionListener a)
	{
		JMenuItem m=new JMenuItem(s,key);
		m.addActionListener(a);
		toMenu.add(m);
		return m;
	}
	JMenuItem createMenuItem(String s, int key,JMenu toMenu,int aclKey,ActionListener al)
	{
		JMenuItem m1=new JMenuItem(s,key);
		m1.addActionListener(al);
		m1.setAccelerator(KeyStroke.getKeyStroke(aclKey,ActionEvent.CTRL_MASK));
		toMenu.add(m1);
		return m1;
	}
	JCheckBoxMenuItem createCheckBoxMenuItem(String s, int key,JMenu toMenu,ActionListener al)
	{
		JCheckBoxMenuItem c=new JCheckBoxMenuItem(s);
		c.setMnemonic(key);
		c.addActionListener(al);
		c.setSelected(false);
		toMenu.add(c);
		return c;
	}
	JMenu createMenu(String s,int key,JMenuBar toMenuBar)
	{
		JMenu menu1=new JMenu(s);
		menu1.setMnemonic(key);
		toMenuBar.add(menu1);
		return menu1;
	}
	//function to create menu bar
	void createMenuBar(JFrame f)
	{
		JMenuBar mb=new JMenuBar();
		JMenuItem menuitem;
		
		JMenu fileMenu=createMenu(fileText,KeyEvent.VK_F,mb);
		JMenu editMenu=createMenu(editText,KeyEvent.VK_E,mb);
		JMenu formatMenu=createMenu(formatText,KeyEvent.VK_O,mb);
		JMenu viewMenu=createMenu(viewText,KeyEvent.VK_V,mb);
		
		createMenuItem(fileNew,KeyEvent.VK_N,fileMenu,KeyEvent.VK_N,this);
		createMenuItem(fileOpen,KeyEvent.VK_O,fileMenu,KeyEvent.VK_O,this);
		createMenuItem(fileSave,KeyEvent.VK_S,fileMenu,KeyEvent.VK_S,this);
		createMenuItem(fileSaveAs,KeyEvent.VK_A,fileMenu,this);
		fileMenu.addSeparator();
		createMenuItem(filePrint,KeyEvent.VK_P,fileMenu,KeyEvent.VK_P,this);
		fileMenu.addSeparator();
		createMenuItem(fileExit,KeyEvent.VK_X,fileMenu,this);
		
		menuitem=createMenuItem(editUndo,KeyEvent.VK_U,editMenu,KeyEvent.VK_Z,this);
		menuitem.setEnabled(false);
		editMenu.addSeparator();
		cutItem=createMenuItem(editCut,KeyEvent.VK_T,editMenu,KeyEvent.VK_X,this);
		copyItem=createMenuItem(editCopy,KeyEvent.VK_C,editMenu,KeyEvent.VK_C,this);
		createMenuItem(editPaste,KeyEvent.VK_P,editMenu,KeyEvent.VK_V,this);
		deleteItem=createMenuItem(editDelete,KeyEvent.VK_L,editMenu,this);
		deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
		editMenu.addSeparator();
		findItem=createMenuItem(editFind,KeyEvent.VK_F,editMenu,KeyEvent.VK_F,this);
		findNextItem=createMenuItem(editFindNext,KeyEvent.VK_N,editMenu,this);
		findNextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0));
		replaceItem=createMenuItem(editReplace,KeyEvent.VK_R,editMenu,KeyEvent.VK_H,this);
		gotoItem=createMenuItem(editGoTo,KeyEvent.VK_G,editMenu,KeyEvent.VK_G,this);
		editMenu.addSeparator();
		selectAllItem=createMenuItem(editSelectAll,KeyEvent.VK_A,editMenu,KeyEvent.VK_A,this);
		createMenuItem(editTimeDate,KeyEvent.VK_D,editMenu,this).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0));
		
		createCheckBoxMenuItem(formatWordWrap,KeyEvent.VK_W,formatMenu,this);
		
		createMenuItem(formatFont,KeyEvent.VK_F,formatMenu,this);
		formatMenu.addSeparator();
		createMenuItem(formatForeground,KeyEvent.VK_T,formatMenu,this);
		createMenuItem(formatBackground,KeyEvent.VK_P,formatMenu,this);
		
		createCheckBoxMenuItem(viewStatusBar,KeyEvent.VK_S,viewMenu,this).setSelected(true);
		LookAndFeel.createLookAndFeelMenuItem(viewMenu,this.f);
	
	MenuListener editMenuListener=new MenuListener()
	{
	   public void menuSelected(MenuEvent e1)
		{
		if(Text_Editor.this.ta.getText().length()==0)
		{
		findItem.setEnabled(false);
		findNextItem.setEnabled(false);
		replaceItem.setEnabled(false);
		selectAllItem.setEnabled(false);
		gotoItem.setEnabled(false);
		}
		else
		{
		findItem.setEnabled(true);
		findNextItem.setEnabled(true);
		replaceItem.setEnabled(true);
		selectAllItem.setEnabled(true);
		gotoItem.setEnabled(true);
		}
		if(ta.getSelectionStart()==ta.getSelectionEnd())
		{
		cutItem.setEnabled(false);
		copyItem.setEnabled(false);
		deleteItem.setEnabled(false);
		}
		else
		{
		cutItem.setEnabled(true);
		copyItem.setEnabled(true);
		deleteItem.setEnabled(true);
		}
		}
	   public void menuDeselected(MenuEvent e2){}
	   public void menuCanceled(MenuEvent e3){}
	};
	editMenu.addMenuListener(editMenuListener);
	f.setJMenuBar(mb);
	}
//main function 
public static  void main(String[] args) 
{
	new Text_Editor();
}
}
