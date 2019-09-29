package pr00;

public class Coche {
	
	protected double miVelocidad; // Velocidad en pixels/segundo
	protected double miDireccionActual; // Dirección en la que estoy mirando en grados (de 0 a 360)
	protected double posX; // Posición en X (horizontal)
	protected double posY; // Posición en Y (vertical)
	private String piloto; // Nombre de piloto
	
	public Coche(double velocidad, double DireccionActual, double x,
			double y, String p) {
		
		miVelocidad = velocidad;
		miDireccionActual = DireccionActual;
		posX = x;
		posY = y;
		piloto = p; 	
	}
	
	
	/** Cambia la velocidad actual del coche
	* @param aceleracion Incremento de la velocidad en pixels/segundo
	*/
	public void acelera( double aceleracion ) {
		miVelocidad =+ aceleracion; 
		System.out.println(miVelocidad);
		
	}
	
	/** Cambia la dirección actual del coche
	* @param giro Angulo de giro a sumar o restar de la dirección actual, en grados (-180 a +180)
	*/
	public void gira( double giro ) {
		setMiDireccionActual(miDireccionActual + giro);
		System.out.println(miDireccionActual);
	}
	
	/** Cambia la posición del coche dependiendo de su velocidad y dirección
	* @param tiempoDeMovimiento Tiempo transcurrido, en segundos
	*/
	public void mueve( double tiempoDeMovimiento ) {
		posX = miVelocidad * tiempoDeMovimiento * Math.cos(miDireccionActual);
		posY = miVelocidad * tiempoDeMovimiento * Math.sin(miDireccionActual);
		System.out.println(posX);
		System.out.println(posY);
		
		
		
	}

	@Override
	public String toString() {
		return "Coche [miVelocidad=" + miVelocidad + ", miDireccionActual=" + miDireccionActual + ", posX=" + posX
				+ ", posY=" + posY + ", piloto=" + piloto + "]";
	}

	public double getMiVelocidad() {
		return miVelocidad;
	}

	public void setMiVelocidad(double miVelocidad) {
		this.miVelocidad = miVelocidad;
	}

	public double getMiDireccionActual() {
		return miDireccionActual;
	}

	public void setMiDireccionActual(double miDireccionActual) {
		this.miDireccionActual = miDireccionActual;
	}

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public String getPiloto() {
		return piloto;
	}

	public void setPiloto(String piloto) {
		this.piloto = piloto;
	}
	


}
