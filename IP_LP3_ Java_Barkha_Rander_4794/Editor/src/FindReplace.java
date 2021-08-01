import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class FindReplace extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextArea ta1;
	public int lastIndex;
	JLabel replaceLabel;
	
	private TextField findWhat;
	private JTextField replaceWith;
	
	private JCheckBox matchCase;
	
	JRadioButton up, down;
	
	JButton findNextButton, replaceButton, replaceAllButton, cancelButton;
	
	JPanel direction,buttonPanel, findButtonPanel, replaceButtonPanel;
	CardLayout card;
	
	private boolean ok;
	private JDialog dialog;
	
	public FindReplace(JTextArea texta)
	{
	
		this.ta1=texta;
		findWhat=new TextField(20);
		replaceWith=new JTextField(20);
		
		matchCase=new JCheckBox("Match Case");
		
		up=new JRadioButton("Up");
		down=new JRadioButton("Down");
		
		down.setSelected(true);
		ButtonGroup bg=new ButtonGroup();
		bg.add(up);
		bg.add(down);
		
		direction=new JPanel();
		Border etched=BorderFactory.createEtchedBorder();
		Border titled=BorderFactory.createTitledBorder(etched,"Direction");
		direction.setBorder(titled);
		direction.setLayout(new GridLayout(1,2));
		direction.add(up);
		direction.add(down);
		
		JPanel southPanel=new JPanel();
		southPanel.setLayout(new GridLayout(1,2));
		southPanel.add(matchCase);
		southPanel.add(direction);
		
		
		findNextButton=new JButton("Find Next");
		replaceButton=new JButton("Replace");
		replaceAllButton=new JButton("Replace All");
		cancelButton=new JButton("Cancel");
	
		replaceButtonPanel=new JPanel();
		replaceButtonPanel.setLayout(new GridLayout(4,1));
		replaceButtonPanel.add(findNextButton);
		replaceButtonPanel.add(replaceButton);
		replaceButtonPanel.add(replaceAllButton);
		replaceButtonPanel.add(cancelButton);
	
		JPanel textPanel=new JPanel();
		textPanel.setLayout(new GridLayout(3,2));
		textPanel.add(new JLabel("Find What "));
		textPanel.add(findWhat);
		textPanel.add(replaceLabel=new JLabel("Replace With "));
		textPanel.add(replaceWith);
		textPanel.add(new JLabel(" "));
		textPanel.add(new JLabel(" "));
		
		setLayout(new BorderLayout());
		
		add(new JLabel("       "),BorderLayout.NORTH);
		add(textPanel,BorderLayout.CENTER);
		add(replaceButtonPanel,BorderLayout.EAST);
		add(southPanel,BorderLayout.SOUTH);
		
		setSize(200,200);
	
	findNextButton.addActionListener(this);
	replaceButton.addActionListener(this);
	replaceAllButton.addActionListener(this);
	
	cancelButton.addActionListener(new ActionListener()
		{public void actionPerformed(ActionEvent ev){dialog.setVisible(false);}});
	
	findWhat.addFocusListener(
		new FocusAdapter(){public void focusLost(FocusEvent te){enableDisableButtons();}});
	findWhat.addTextListener(
		new TextListener(){public void textValueChanged(TextEvent te){enableDisableButtons();}});
	
	}
	
	void enableDisableButtons()
	{
	if(findWhat.getText().length()==0)
	{
	findNextButton.setEnabled(false);
	replaceButton.setEnabled(false);
	replaceAllButton.setEnabled(false);
	}
	else
	{
	findNextButton.setEnabled(true);
	replaceButton.setEnabled(true);
	replaceAllButton.setEnabled(true);
	}
	}
	
	public void actionPerformed(ActionEvent ev)
	{
	
	if(ev.getSource()==findNextButton)
		findNextWithSelection();
	else if(ev.getSource()==replaceButton)
		replaceNext();
	else if(ev.getSource()==replaceAllButton)
		JOptionPane.showMessageDialog(null,"Total replacements made= "+replaceAllNext());
	
	}
	
	int findNext()
	{
	
	String s1=ta1.getText();
	String s2=findWhat.getText();
	
	lastIndex=ta1.getCaretPosition();
	
	int selStart=ta1.getSelectionStart();
	int selEnd=ta1.getSelectionEnd();
	
	if(up.isSelected())
	{
	if(selStart!=selEnd)
		lastIndex=selEnd-s2.length()-1;
	
	if(!matchCase.isSelected())
		lastIndex=s1.toUpperCase().lastIndexOf(s2.toUpperCase(),lastIndex);
	else
		lastIndex=s1.lastIndexOf(s2,lastIndex);	
	}
	else
	{
	if(selStart!=selEnd)
		lastIndex=selStart+1;
	if(!matchCase.isSelected())
		lastIndex=s1.toUpperCase().indexOf(s2.toUpperCase(),lastIndex);
	else
		lastIndex=s1.indexOf(s2,lastIndex);	
	}
	
	return lastIndex;
	}
	public void findNextWithSelection()
	{
	int idx=findNext();
	if(idx!=-1)
	{
	ta1.setSelectionStart(idx);
	ta1.setSelectionEnd(idx+findWhat.getText().length());
	}
	else
		JOptionPane.showMessageDialog(this,"Cannot find"+" \""+findWhat.getText()+"\"","Find",JOptionPane.INFORMATION_MESSAGE);
	}
	void replaceNext()
	{
	if(ta1.getSelectionStart()==ta1.getSelectionEnd()) 
		{findNextWithSelection();return;}
	
	String searchText=findWhat.getText();
	String selected=ta1.getSelectedText();	
	if((matchCase.isSelected() && selected.equals(searchText))||(!matchCase.isSelected() && selected.equalsIgnoreCase(searchText)))
		ta1.replaceSelection(replaceWith.getText());
		findNextWithSelection();
	}
	int replaceAllNext()
	{
	if(up.isSelected())
		ta1.setCaretPosition(ta1.getText().length()-1);
	else
		ta1.setCaretPosition(0);
	
	int idx=0;
	int counter=0;
	do
	{
	idx=findNext();
	if(idx==-1) break;
	counter++;
	ta1.replaceRange(replaceWith.getText(),idx,idx+findWhat.getText().length());
	}while(idx!=-1);
	
	return counter;
	}
	public boolean showDialog(Component parent, boolean isFind )
	{
	
	Frame owner=null;
	if(parent instanceof Frame) 
		owner=(Frame)parent;
	else
		owner=(Frame)SwingUtilities.getAncestorOfClass(Frame.class,parent);
	if(dialog==null || dialog.getOwner()!=owner)
	{
	dialog=new JDialog(owner,false);
	dialog.add(this);
	dialog.getRootPane().setDefaultButton(findNextButton);
	}
	
	if(findWhat.getText().length()==0)
		findNextButton.setEnabled(false);
	else
		findNextButton.setEnabled(true);
	
	replaceButton.setVisible(false);
	replaceAllButton.setVisible(false);
	replaceWith.setVisible(false);
	replaceLabel.setVisible(false);
	
	if(isFind)
	{
	dialog.setSize(460,180);
	dialog.setTitle("Find");
	}
	else
	{
	replaceButton.setVisible(true);
	replaceAllButton.setVisible(true);
	replaceWith.setVisible(true);
	replaceLabel.setVisible(true);
	
	dialog.setSize(450,200);
	dialog.setTitle("Replace");
	}
	
	dialog.setVisible(true);
	
	return ok;
	}
}
