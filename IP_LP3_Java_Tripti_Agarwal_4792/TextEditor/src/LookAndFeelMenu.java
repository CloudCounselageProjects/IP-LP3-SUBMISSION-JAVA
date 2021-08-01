import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


class LookAndFeel extends JFrame
{
	JLabel l1;
	JMenuBar j;
	JMenu fileMenu;
	
	LookAndFeel()
	{
		super("Look and Feel");
		add(l1=new JLabel("This is a Label"));
		add(new JButton("Button")); 
		add(new JCheckBox("CheckBox"));
		add(new JRadioButton("RadioButton"));
		setLayout(new FlowLayout());
		setSize(350,350);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		j=new JMenuBar();
		setJMenuBar(j);
		fileMenu=new JMenu("Look and Feel");
		j.add(fileMenu);
		LookAndFeelMenu.createLookAndFeelMenuItem(fileMenu,this);
		setVisible(true);
	}
	public static void main(String[] args)
	{
		new LookAndFeel();
	}
	
}
public class LookAndFeelMenu {

	public static void createLookAndFeelMenuItem(JMenu jmenu,Component cmp)
	{
		final UIManager.LookAndFeelInfo[] info=UIManager.getInstalledLookAndFeels();
		class LookAndFeelMenuListener implements ActionListener
		{
			String classname;
			Component jf;
			LookAndFeelMenuListener(String cln,Component jf)
			{
			this.jf=jf;
			classname=new String(cln);
			}
			public void actionPerformed(ActionEvent e1)
			{
			try
			{
				UIManager.setLookAndFeel(classname);
				SwingUtilities.updateComponentTreeUI(jf);
			}
			catch(Exception e){System.out.println(e);}
		}

	}
	
		JRadioButtonMenuItem rbm[]=new JRadioButtonMenuItem[info.length];
		ButtonGroup bg=new ButtonGroup();
		JMenu menu1=new JMenu("Change Look and Feel");
		menu1.setMnemonic('C');
		for(int i=0; i<info.length; i++)
		{
			rbm[i]=new JRadioButtonMenuItem(info[i].getName());
			rbm[i].setMnemonic(info[i].getName().charAt(0));
			menu1.add(rbm[i]);
			bg.add(rbm[i]);
			rbm[i].addActionListener(new LookAndFeelMenuListener(info[i].getClassName(),cmp));
		}

	rbm[0].setSelected(true);
	jmenu.add(menu1);
	
	}
}