package pr00;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class JLabelCoche extends JLabel{


	public void setIcon(String string) {
		JLabel imagen = new JLabel(new ImageIcon("src/img/coche.png"));
		imagen.setBounds(200, 10, 100, 100);
	
		
	}
	
	
	
	

}

