package pr00;

import javax.swing.JLabel;

public class CocheJuego extends Coche{
	
	private JLabel JLabelCoche; 

	public CocheJuego(double velocidad, double DireccionActual, double x, 
			double y, String p, JLabel JLabelCoche) {
		super(velocidad, DireccionActual, x, y, p);
		this.JLabelCoche = JLabelCoche; 
		
	}

	public JLabel getJLabelCoche() {
		return JLabelCoche;
	}

	public void setJLabelCoche(JLabel jLabelCoche) {
		JLabelCoche = jLabelCoche;
	}
	
	

}
