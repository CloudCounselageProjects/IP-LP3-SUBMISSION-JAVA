import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class LookAndFeel {

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