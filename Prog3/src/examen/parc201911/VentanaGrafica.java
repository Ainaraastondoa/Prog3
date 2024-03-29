package examen.parc201911;
import java.awt.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/** Clase ventana sencilla para dibujado directo a la ventana
 * v 1.1.5 - Incorpora método para cambiar el tipo de letra de la línea de mensajes, método para consultar el mensaje actual
* v 1.1.4 - Incorpora métodos para pedir datos desde teclado
 */
public class VentanaGrafica {

	// ====================================================
	//   Parte estática - pruebas de funcionamiento
	// ====================================================
	
		private static VentanaGrafica v;
	/** Método main de prueba de la clase
	 * @param args	No utilizado
	 */
	public static void main(String[] args) {
		v = new VentanaGrafica( 800, 600, "Test Ventana Gráfica" );
		v.anyadeBoton( "Pon/quita dibujado inmediato", new ActionListener() {  // Para ver cómo se ve con flickering si se dibujan cosas una a una
			@Override
			public void actionPerformed(ActionEvent e) {
				v.setDibujadoInmediato( !v.isDibujadoInmediato() );
			}
		});
		v.setDibujadoInmediato( false );
		Object opcion = JOptionPane.showInputDialog( v.getJFrame(), "¿Qué quieres probar?",
			"Selección de test", JOptionPane.QUESTION_MESSAGE, null, 
			new String[] { "Movimiento", "Giros", "Tiro", "Texto" }, "Movimiento" );
		if ( "Movimiento".equals( opcion ) ) {
			movimiento();
		} else if ( "Giros".equals( opcion ) ) {
			giros();
		} else if ( "Tiro".equals( opcion ) ) {
			tiro();
		} else if ( "Texto".equals( opcion ) ) {
			texto();
		}
	}

	// Prueba 1: escudo verde que se mueve y sube y baja
	private static void movimiento() {
		int altura = v.getAltura();
		boolean subiendo = true;
		for (int i=0; i<=800; i++) {
			v.borra();
			v.dibujaTexto( i+100, 100+(i/4), "texto móvil", new Font( "Arial", Font.PLAIN, 16 ), Color.black );
			v.dibujaImagen( "/img/UD-green.png", i, altura, 1.0, 0.0, 1.0f );
			if (subiendo) {
				altura--;
				if (altura<=0) subiendo = false;
			} else {
				altura++;
				if (altura>=v.getAltura()) subiendo = true;
			}
			v.repaint();
			v.espera( 10 );
		}
		v.espera( 5000 );
		v.acaba();
	}
	
	// Prueba 2: escudos y formas girando y zoomeando
	private static void giros() {
		for (int i=0; i<=1000; i++) {
			v.borra();
			v.dibujaImagen( "/img/UD-green.png", 100, 100, 0.5+i/200.0, Math.PI / 100 * i, 0.9f );
			v.dibujaImagen( "/img/UD-magenta.png", 500, 100, 100, 50, 1.2, Math.PI / 100 * i, 0.1f );
			v.dibujaRect( 20, 20, 160, 160, 0.5f, Color.red );
			v.dibujaRect( 0, 0, 100, 100, 1.5f, Color.blue );
			v.dibujaCirculo( 500, 100, 50, 1.5f, Color.orange );
			v.dibujaPoligono( 2.3f, Color.magenta, true, 
				new Point(200,250), new Point(300,320), new Point(400,220) );
			v.repaint();
			v.espera( 10 );
		}
		v.espera( 5000 );
		v.acaba();
	}

	// Prueba 3: tiro parabólico
	private static void tiro() {
		boolean seguir = true;
		v.setMensaje( "Click ratón para disparar (con fuerza y ángulo)");
		double xLanz = 20;
		double yLanz = v.getAltura()-20;
		while (seguir) {
			Point pMovto = v.getRatonMovido();
			Point pPuls = v.getRatonPulsado();
			v.borra();
			v.dibujaCirculo( xLanz, yLanz, 10, 3.0f, Color.MAGENTA );
			if (pPuls!=null) {  // Se hace click: disparar!
				disparar( xLanz, yLanz, pPuls.getX(), pPuls.getY() );
			} else if (pMovto!=null) {  // No se hace click: dibujar flecha
				v.dibujaFlecha( xLanz, yLanz, pMovto.getX(), pMovto.getY(), 2.0f, Color.GREEN, 25 );
			}
			v.repaint();
			if (v.estaCerrada()) seguir = false;  // Acaba cuando el usuario cierra la ventana
			v.espera( 20 ); // Pausa 20 msg (aprox 50 frames/sg)
		}
	}
		// Hace un disparo con la velocidad marcada por el vector con gravedad
		private static void disparar( double x1, double y1, double x2, double y2 ) {
			double G = 9.8; // Aceleración de la gravedad
			double velX = x2-x1; double velY = y2-y1;
			v.setMensaje( "Calculando disparo con velocidad (" + velX + "," + velY + ")" );
			double x = x1; double y = y1;  // Punto de disparo
			int pausa = 10; // msg de pausa de animación
			double tempo = pausa / 1000.0; // tiempo entre frames de animación (en segundos)
			do {
				v.dibujaCirculo( x, y, 1.0, 1.0f, Color.blue );  // Dibuja punto
				x = x + velX * tempo; // Mueve x (según la velocidad)
				y = y + velY * tempo;// Mueve y (según la velocidad)
				velY = velY + G * 10 * tempo; // Cambia la velocidad de y (por efecto de la gravedad)
				v.repaint();
				v.espera( pausa ); // Pausa 20 msg (aprox 50 frames/sg)
			} while (y<y1);  // Cuando pasa hacia abajo la vertical se para
			v.espera( 2000 ); // Pausa de 2 segundos
			v.setMensaje( "Vuelve a disparar!" );
		}
	
	// Prueba 4: petición de texto en la ventana
	private static void texto() {
		v.setDibujadoInmediato( true );
		v.dibujaImagen( "/img/UD-roller.jpg", 400, 300, 1.0, 0.0, 1.0f );
		Font f = new Font( "Arial", Font.PLAIN, 30 );
		String t1 = v.leeTexto( 100, 100, 200, 50, "Modifica texto", f, Color.magenta );
		v.setMensaje( "Introduce texto" );
		v.dibujaTexto( 100, 200, "Texto introducido: " + t1, f, Color.white );
		v.setMensaje( "Introduce texto otra vez" );
		t1 = v.leeTexto( 100, 300, 200, 50, "", f, Color.blue );
		v.setMensaje( "Textos leídos." );
		v.dibujaTexto( 100, 400, "Texto introducido: " + t1, f, Color.white );
		v.espera( 5000 );
		v.acaba();
	}
	
	// Parte estática de datos comunes 
		
		private static int codTeclaTecleada = 0;
		private static int codTeclaActualmentePulsada = 0;
		private static HashSet<Integer> teclasPulsadas = new HashSet<Integer>();
		private static boolean controlActivo = false;
	// Inicializa el control de teclado
	private static void init() {
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher( new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					if (keyListeners.size()>0) for (KeyListener kl : keyListeners) kl.keyPressed( e );
					teclasPulsadas.add( e.getKeyCode() );
					codTeclaActualmentePulsada = e.getKeyCode();
					if (e.getKeyCode() == KeyEvent.VK_CONTROL) controlActivo = true; 
				} else if (e.getID() == KeyEvent.KEY_RELEASED) {
					if (keyListeners.size()>0) for (KeyListener kl : keyListeners) kl.keyReleased( e );
					teclasPulsadas.remove( e.getKeyCode() );
					if (e.getKeyCode() == KeyEvent.VK_CONTROL) controlActivo = false; 
					codTeclaTecleada = e.getKeyCode();
					codTeclaActualmentePulsada = 0;
				} else if (e.getID() == KeyEvent.KEY_TYPED) {
					if (keyListeners.size()>0) for (KeyListener kl : keyListeners) kl.keyTyped( e );
				}
				return false;   // false = enviar el evento al comp
			} } );
	}
	static {  // Inicializar en la carta de la clase
		init();
	}
	
	
	// ====================================================
	//   Parte no estática - objeto VentanaGrafica
	// ====================================================
	
	private JFrame ventana;       // Ventana que se visualiza
	private boolean cerrada;      // Lógica de cierre (false al inicio)
	private JPanel panel;         // Panel principal
	private JLabel lMens;         // Etiqueta de texto de mensajes en la parte inferior
	private BufferedImage buffer; // Buffer gráfico de la ventana
	private Graphics2D graphics;  // Objeto gráfico sobre el que dibujar (del buffer)
	private Point pointPressed;   // Coordenada pulsada de ratón (si existe)
	private Point pointMoved;     // Coordenada pasada de ratón (si existe)
	private Point pointMovedPrev; // Coordenada pasada anterior de ratón (si existe)
	private boolean dibujadoInmediato = true; // Refresco de dibujado en cada orden de dibujado

		private Object lock = new Object();  // Objeto de sincronización de hilos para el acceso como si no los hubiera
	
	/** Construye una nueva ventana gráfica con fondo blanco y la visualiza en el centro de la pantalla
	 * @param anchura	Anchura en píxels (valor positivo) de la zona de pintado
	 * @param altura	Altura en píxels (valor positivo) de la zona de pintado
	 * @param titulo	Título de la ventana
	 */
	@SuppressWarnings("serial")
	public VentanaGrafica( int anchura, int altura, String titulo ) {
		setLookAndFeel();
		cerrada = false;
		ventana = new JFrame( titulo );
		ventana.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		// ventana.setSize( anchura, altura ); -- se hace en función del tamaño preferido del panel de dibujo
		buffer = new BufferedImage( 2000, 1500, BufferedImage.TYPE_INT_ARGB );
		graphics = buffer.createGraphics();
		graphics.setPaint ( Color.white );
		graphics.fillRect ( 0, 0, 2000, 1500 );
		panel = new JPanel() {
			{
				setLayout( new BorderLayout() );
			}
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				((Graphics2D)g).drawImage( buffer, null, 0, 0 );
			}
		};
		panel.setPreferredSize( new Dimension( anchura, altura ));
		panel.setLayout( null );  // Layout nulo para cuando saquemos componentes encima del dibujo
		lMens = new JLabel( " " );
		ventana.getContentPane().add( panel, BorderLayout.CENTER );
		ventana.getContentPane().add( lMens, BorderLayout.SOUTH );
		ventana.pack();
		ventana.setLocationRelativeTo( null );
		ventana.addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cerrada = true;
			}
		});
		panel.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				synchronized (lock) {
					pointPressed = null;
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				synchronized (lock) {
					pointPressed = e.getPoint();
				}
			}
		});
		panel.addMouseMotionListener( new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				synchronized (lock) {
					pointMoved = e.getPoint();
				}
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				synchronized (lock) {
					pointPressed = e.getPoint();
				}
			}
		});
		Runnable r = new Runnable() {
			@Override
			public void run() {
				ventana.setVisible( true );
			}
		};
		if (SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait( r );
			} catch (InvocationTargetException | InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	// Intenta poner el look & feel nimbus (solo la primera vez que se crea una ventana)
		private static boolean lookAndFeelIntentado = false;
	private void setLookAndFeel() {
		if (lookAndFeelIntentado) return;
		lookAndFeelIntentado = true;
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            return;
		        }
		    }
		} catch (Exception e) {} // Si no está disponible nimbus, no se hace nada
	}
	
	/** Espera un tiempo y sigue
	 * @param milis	Milisegundos a esperar
	 */
	public void espera( long milis ) {
		try {
			Thread.sleep( milis );
		} catch (InterruptedException e) {
		}
	}
	/** Cierra la ventana (también ocurre cuando se pulsa el icono de cierre)
	 */
	public void acaba() {
		if (!cerrada) ventana.dispose();
		cerrada = true;
	}
	
	/** Devuelve el objeto ventana (JFrame) correspondiente a la ventana gráfica
	 * @return	Objeto JFrame de la ventana
	 */
	public JFrame getJFrame() {
		return ventana;
	}
	
	/** Consultor de estado de visibilidad de la ventana
	 * @return	false si sigue activa, true si ya se ha cerrado
	 */
	public boolean estaCerrada() {
		return cerrada;
	}
	
	/** Devuelve el punto donde está el ratón pulsado en este momento
	 * @return	Punto relativo a la ventana, null si el ratón no está siendo pulsado
	 */
	public Point getRatonPulsado() {
		synchronized (lock) {
			return pointPressed;
		}
	}
	
	/** Devuelve el punto donde está el ratón en este momento
	 * @return	Punto relativo a la ventana, null si el ratón no se ha movido nunca
	 */
	public Point getRatonMovido() {
		synchronized (lock) {
			return pointMoved;
		}
	}
	
	/** Devuelve el último vector de movimiento del ratón (desde la última vez que se llamó a este mismo método)
	 * @return	Punto relativo a la ventana, null si el ratón no se ha movido nunca
	 */
	public Point getVectorRatonMovido() {
		synchronized (lock) {
			Point ret = new Point( 0, 0 );
			if (pointMovedPrev!=null) {
				ret.setLocation( pointMoved.getX()-pointMovedPrev.getX(), pointMoved.getY()- pointMovedPrev.getY() );
			}
			pointMovedPrev = pointMoved;
			return ret;
		}
	}	
	
	/** Cambia el mensaje de la ventana (línea inferior de mensajes)
	 * @param mensaje	Texto de mensaje
	 */
	public void setMensaje( String mensaje ) {
		if (mensaje==null || mensaje.isEmpty())
			lMens.setText( " " );
		else
			lMens.setText( mensaje );
	}
	
	/** Devuelve el mensaje actual de la ventana (línea inferior de mensajes)
	 * @return	Mensaje actual
	 */
	public String getMensaje() {
		return lMens.getText();
	}
	
	/** Cambia el tipo de letra de la línea inferior de mensajes
	 * @param font	Tipo de letra a utilizar
	 */
	public void setMensajeFont( Font font ) {
		lMens.setFont( font );
	}
	
	/** Devuelve la altura del panel de dibujo de la ventana
	 * @return	Altura del panel principal (última coordenada y) en píxels
	 */
	public int getAltura() {
		return panel.getHeight()-1;
	}
	
	/** Devuelve la anchura del panel de dibujo de la ventana
	 * @return	Anchura del panel principal (última coordenada x) en píxels
	 */
	public int getAnchura() {
		return panel.getWidth()-1;
	}
	
	/** Borra toda la ventana (pinta de color blanco)
	 */
	public void borra() {
		graphics.setColor( Color.white );
		graphics.fillRect( 0, 0, panel.getWidth()+2, panel.getHeight()+2 );
		if (dibujadoInmediato) repaint();
	}
	
	/** Dibuja un polígono relleno en la ventana
	 * @param x	Coordenadas x de las esquinas del polígono
	 * @param y	Coordenadas y de las esquinas del polígono
	 * @param grosor	Grueso del polígono (en píxels)
	 * @param color  	Color de la línea del polígono
	 * @param colorRell	Color del relleno del polígono
	 */
	public void dibujaPoli( double[] x, double[] y, float grosor, Color color, Color colorRell ) {
		Polygon poly = new Polygon();
		for (int i=0; i<x.length; i++) poly.addPoint( (int) Math.round(x[i]), (int) Math.round(y[i]) );
		graphics.setColor( colorRell );
		graphics.setStroke( new BasicStroke( grosor ));
		graphics.fillPolygon( poly );
		graphics.setColor( color );
		graphics.drawPolygon( poly );
		if (dibujadoInmediato) repaint();
	}
	
	/** Dibuja un rectángulo en la ventana
	 * @param rectangulo	Rectángulo a dibujar
	 * @param grosor	Grueso de la línea del rectángulo (en píxels)
	 * @param color  	Color del rectángulo
	 */
	public void dibujaRect( Rectangle rectangulo, float grosor, Color color ) {
		dibujaRect( rectangulo.getX(), rectangulo.getY(), rectangulo.getX()+rectangulo.getWidth(), rectangulo.getY()+rectangulo.getHeight(), grosor, color );
	}
	
	/** Dibuja un rectángulo en la ventana
	 * @param x	Coordenada x de la esquina superior izquierda del rectángulo
	 * @param y	Coordenada y de la esquina superior izquierda del rectángulo
	 * @param anchura	Anchura del rectángulo (en píxels) 
	 * @param altura	Altura del rectángulo (en píxels)
	 * @param grosor	Grueso del rectángulo (en píxels)
	 * @param color  	Color del rectángulo
	 */
	public void dibujaRect( double x, double y, double anchura, double altura, float grosor, Color color ) {
		graphics.setColor( color );
		graphics.setStroke( new BasicStroke( grosor ));
		graphics.drawRect( (int)Math.round(x), (int)Math.round(y), (int)Math.round(anchura), (int)Math.round(altura) );
		if (dibujadoInmediato) repaint();
	}
	
	/** Dibuja un rectángulo relleno en la ventana
	 * @param x	Coordenada x de la esquina superior izquierda del rectángulo
	 * @param y	Coordenada y de la esquina superior izquierda del rectángulo
	 * @param anchura	Anchura del rectángulo (en píxels) 
	 * @param altura	Altura del rectángulo (en píxels)
	 * @param grosor	Grueso del rectángulo (en píxels)
	 * @param color  	Color de la línea del rectángulo
	 * @param colorRell	Color del relleno del rectángulo
	 */
	public void dibujaRect( double x, double y, double anchura, double altura, float grosor, Color color, Color colorRell ) {
		graphics.setColor( colorRell );
		graphics.setStroke( new BasicStroke( grosor ));
		graphics.fillRect( (int)Math.round(x), (int)Math.round(y), (int)Math.round(anchura), (int)Math.round(altura) );
		graphics.setColor( color );
		graphics.drawRect( (int)Math.round(x), (int)Math.round(y), (int)Math.round(anchura), (int)Math.round(altura) );
		if (dibujadoInmediato) repaint();
	}
	
	/** Dibuja un rectángulo azul en la ventana
	 * @param x	Coordenada x de la esquina superior izquierda del rectángulo
	 * @param y	Coordenada y de la esquina superior izquierda del rectángulo
	 * @param anchura	Anchura del rectángulo (en píxels) 
	 * @param altura	Altura del rectángulo (en píxels)
	 * @param grosor	Grueso del rectángulo (en píxels)
	 */
	public void dibujaRect( double x, double y, double anchura, double altura, float grosor ) {
		dibujaRect( x, y, anchura, altura, grosor, Color.blue );
	}
	
	/** Borra un rectángulo en la ventana
	 * @param x	Coordenada x de la esquina superior izquierda del rectángulo
	 * @param y	Coordenada y de la esquina superior izquierda del rectángulo
	 * @param anchura	Anchura del rectángulo (en píxels) 
	 * @param altura	Altura del rectángulo (en píxels)
	 * @param grosor	Grueso del rectángulo (en píxels)
	 */
	public void borraRect( double x, double y, double anchura, double altura, float grosor ) {
		dibujaRect( x, y, anchura, altura, grosor, Color.white );
	}
	
	/** Dibuja un círculo en la ventana
	 * @param x	Coordenada x del centro del círculo
	 * @param y	Coordenada y del centro del círculo
	 * @param radio	Radio del círculo (en píxels) 
	 * @param grosor	Grueso del círculo (en píxels)
	 * @param color  	Color del círculo
	 */
	public void dibujaCirculo( double x, double y, double radio, float grosor, Color color ) {
		graphics.setColor( color );
		graphics.setStroke( new BasicStroke( grosor ));
		graphics.drawOval( (int)Math.round(x-radio), (int)Math.round(y-radio), (int)Math.round(radio*2), (int)Math.round(radio*2) );
		if (dibujadoInmediato) repaint();
	}
	
	/** Dibuja un círculo relleno en la ventana
	 * @param x	Coordenada x del centro del círculo
	 * @param y	Coordenada y del centro del círculo
	 * @param radio	Radio del círculo (en píxels) 
	 * @param grosor	Grueso del círculo (en píxels)
	 * @param colorL 	Color de la línea del círculo
	 * @param colorR 	Color de relleno del círculo
	 */
	public void dibujaCirculo( double x, double y, double radio, float grosor, Color colorL, Color colorR ) {
		graphics.setStroke( new BasicStroke( grosor ));
		graphics.setColor( colorR );
		graphics.fillOval( (int)Math.round(x-radio), (int)Math.round(y-radio), (int)Math.round(radio*2), (int)Math.round(radio*2) );
		graphics.setColor( colorL );
		graphics.drawOval( (int)Math.round(x-radio), (int)Math.round(y-radio), (int)Math.round(radio*2), (int)Math.round(radio*2) );
		if (dibujadoInmediato) repaint();
	}
	
	/** Dibuja un círculo azul en la ventana
	 * @param x	Coordenada x del centro del círculo
	 * @param y	Coordenada y del centro del círculo
	 * @param radio	Radio del círculo (en píxels) 
	 * @param grosor	Grueso del círculo (en píxels)
	 */
	public void dibujaCirculo( double x, double y, double radio, float grosor ) {
		dibujaCirculo( x, y, radio, grosor, Color.blue );
	}
	
	/** Borra un círculo en la ventana
	 * @param x	Coordenada x del centro del círculo
	 * @param y	Coordenada y del centro del círculo
	 * @param radio	Radio del círculo (en píxels) 
	 * @param grosor	Grueso del círculo (en píxels)
	 */
	public void borraCirculo( double x, double y, double radio, float grosor ) {
		dibujaCirculo( x, y, radio, grosor, Color.white );
	}
	
	/** Dibuja una línea en la ventana
	 * @param linea	a dibujar
	 * @param grosor	Grueso de la línea (en píxels)
	 * @param color  	Color de la línea
	 */
	public void dibujaLinea( Line2D linea, float grosor, Color color ) {
		dibujaLinea( linea.getX1(), linea.getY1(), linea.getX2(), linea.getY2(), grosor, color );
	}
	
	/** Dibuja una línea en la ventana
	 * @param x	Coordenada x de un punto de la línea 
	 * @param y	Coordenada y de un punto de la línea
	 * @param x2	Coordenada x del segundo punto de la línea 
	 * @param y2	Coordenada y del segundo punto de la línea
	 * @param grosor	Grueso de la línea (en píxels)
	 * @param color  	Color de la línea
	 */
	public void dibujaLinea( double x, double y, double x2, double y2, float grosor, Color color ) {
		graphics.setColor( color );
		graphics.setStroke( new BasicStroke( grosor ));
		graphics.drawLine( (int)Math.round(x), (int)Math.round(y), (int)Math.round(x2), (int)Math.round(y2) );
		if (dibujadoInmediato) repaint();
	}
	
	/** Dibuja una línea azul en la ventana
	 * @param x	Coordenada x de un punto de la línea 
	 * @param y	Coordenada y de un punto de la línea
	 * @param x2	Coordenada x del segundo punto de la línea 
	 * @param y2	Coordenada y del segundo punto de la línea
	 * @param grosor	Grueso de la línea (en píxels)
	 */
	public void dibujaLinea( double x, double y, double x2, double y2, float grosor ) {
		dibujaLinea( x, y, x2, y2, grosor, Color.blue );
	}
	
	/** Borra una línea en la ventana
	 * @param x	Coordenada x de un punto de la línea 
	 * @param y	Coordenada y de un punto de la línea
	 * @param x2	Coordenada x del segundo punto de la línea 
	 * @param y2	Coordenada y del segundo punto de la línea
	 * @param grosor	Grueso de la línea (en píxels)
	 */
	public void borraLinea( double x, double y, double x2, double y2, float grosor ) {
		dibujaLinea( x, y, x2, y2, grosor, Color.white );
	}

	/** Dibuja una flecha en la ventana
	 * @param linea	a dibujar (el segundo punto es la punta de la flecha)
	 * @param grosor	Grueso de la línea (en píxels)
	 * @param color  	Color de la línea
	 */
	public void dibujaFlecha( Line2D linea, float grosor, Color color ) {
		dibujaFlecha( linea.getX1(), linea.getY1(), linea.getX2(), linea.getY2(), grosor, color );
	}
	
	/** Dibuja una flecha en la ventana
	 * @param x	Coordenada x de un punto de la línea 
	 * @param y	Coordenada y de un punto de la línea
	 * @param x2	Coordenada x del segundo punto de la línea (el de la flecha)
	 * @param y2	Coordenada y del segundo punto de la línea (el de la flecha)
	 * @param grosor	Grueso de la línea (en píxels)
	 * @param color  	Color de la línea
	 */
	public void dibujaFlecha( double x, double y, double x2, double y2, float grosor, Color color ) {
		dibujaFlecha( x, y, x2, y2, grosor, color, 10 );
	}
	
	/** Dibuja una flecha en la ventana
	 * @param x	Coordenada x de un punto de la línea 
	 * @param y	Coordenada y de un punto de la línea
	 * @param x2	Coordenada x del segundo punto de la línea (el de la flecha)
	 * @param y2	Coordenada y del segundo punto de la línea (el de la flecha)
	 * @param grosor	Grueso de la línea (en píxels)
	 * @param color  	Color de la línea
	 * @param largoFl	Pixels de largo de la flecha
	 */
	public void dibujaFlecha( double x, double y, double x2, double y2, float grosor, Color color, int largoFl ) {
		graphics.setColor( color );
		graphics.setStroke( new BasicStroke( grosor ));
		graphics.drawLine( (int)Math.round(x), (int)Math.round(y), (int)Math.round(x2), (int)Math.round(y2) );
		double angulo = Math.atan2( y2-y, x2-x ) + Math.PI;
		double angulo1 = angulo - Math.PI / 10;  // La flecha se forma rotando 1/10 de Pi hacia los dos lados
		double angulo2 = angulo + Math.PI / 10;
		graphics.drawLine( (int)Math.round(x2), (int)Math.round(y2), 
				(int)Math.round(x2+largoFl*Math.cos(angulo1)), (int)Math.round(y2+largoFl*Math.sin(angulo1)) );
		graphics.drawLine( (int)Math.round(x2), (int)Math.round(y2), 
				(int)Math.round(x2+largoFl*Math.cos(angulo2)), (int)Math.round(y2+largoFl*Math.sin(angulo2)) );
		if (dibujadoInmediato) repaint();
	}
	
	/** Dibuja una línea azul en la ventana
	 * @param x	Coordenada x de un punto de la línea 
	 * @param y	Coordenada y de un punto de la línea
	 * @param x2	Coordenada x del segundo punto de la línea (el de la flecha)
	 * @param y2	Coordenada y del segundo punto de la línea (el de la flecha)
	 * @param grosor	Grueso de la línea (en píxels)
	 */
	public void dibujaFlecha( double x, double y, double x2, double y2, float grosor ) {
		dibujaFlecha( x, y, x2, y2, grosor, Color.blue );
	}
	
	/** Borra una línea en la ventana
	 * @param x	Coordenada x de un punto de la línea 
	 * @param y	Coordenada y de un punto de la línea
	 * @param x2	Coordenada x del segundo punto de la línea (el de la flecha)
	 * @param y2	Coordenada y del segundo punto de la línea (el de la flecha)
	 * @param grosor	Grueso de la línea (en píxels)
	 */
	public void borraFlecha( double x, double y, double x2, double y2, float grosor ) {
		dibujaFlecha( x, y, x2, y2, grosor, Color.white );
	}
	
	/** Dibuja un polígono en la ventana
	 * @param grosor	Grueso de la línea (en píxels)
	 * @param color  	Color de la línea
	 * @param cerrado	true si el polígono se cierra (último punto con el primero), false en caso contrario
	 * @param punto		Puntos a dibujar (cada punto se enlaza con el siguiente)
	 */
	public void dibujaPoligono( float grosor, Color color, boolean cerrado, Point2D... punto ) {
		graphics.setColor( color );
		graphics.setStroke( new BasicStroke( grosor ));
		if (punto.length<2) return;
		Point2D puntoIni = punto[0];
		Point2D puntoAnt = punto[0];
		Point2D pto = null;
		int numPto = 1;
		do {
			pto = punto[numPto];
			graphics.drawLine( (int)Math.round(puntoAnt.getX()), (int)Math.round(puntoAnt.getY()), (int)Math.round(pto.getX()), (int)Math.round(pto.getY()) );
			puntoAnt = pto;
			numPto++;
		} while (numPto<punto.length);
		if (cerrado) {
			graphics.drawLine( (int)Math.round(pto.getX()), (int)Math.round(pto.getY()), (int)Math.round(puntoIni.getX()), (int)Math.round(puntoIni.getY()) );
		}
		if (dibujadoInmediato) repaint();
	}
	
	/** Borra un polígono en la ventana
	 * @param grosor	Grueso de la línea (en píxels)
	 * @param cerrado	true si el polígono se cierra (último punto con el primero), false en caso contrario
	 * @param punto		Puntos a borrar (cada punto se enlaza con el siguiente)
	 */
	public void borraPoligono( float grosor, boolean cerrado, Point2D... punto ) {
		dibujaPoligono( grosor, Color.white, cerrado, punto );
	}

	/** Dibuja un texto en la ventana
	 * @param x	Coordenada x de la esquina superior izquierda del rectángulo
	 * @param y	Coordenada y de la esquina superior izquierda del rectángulo
	 * @param texto	Texto a dibujar 
	 * @param font	Tipo de letra con el que dibujar el texto
	 * @param color	Color del texto
	 */
	public void dibujaTexto( double x, double y, String texto, Font font, Color color ) {
		graphics.setColor( color );
		graphics.setFont( font );
		graphics.drawString( texto, (int)Math.round(x), (int)Math.round(y) );
		if (dibujadoInmediato) repaint();
	}
	
	
	
	
	
	/** Devuelve el objeto de gráfico sobre el que pintar, correspondiente al 
	 * panel principal de la ventana. Después de actualizar graphics hay que llamar a {@link #repaint()}
	 * si se quiere que se visualice en pantalla
	 * @return	Objeto gráfico principal de la ventana
	 */
	public Graphics2D getGraphics() {
		return graphics;
	}
	
	/** Devuelve el panel gráfico principal de la ventana
	 * @return	Panel gráfico principal
	 */
	public JPanel getPanel() {
		return panel;
	}
	
	/** Repinta la ventana. En caso de que el dibujado inmediato esté desactivado,
	 * es imprescindible llamar a este método para que la ventana gráfica se refresque.
	 */
	public void repaint() {
		panel.repaint();
		// panel.paintImmediately( 0, 0, panel.getWidth(), panel.getHeight() );
	}
	
		private static ArrayList<KeyListener> keyListeners = new ArrayList<>();
	/** Añade un escuchador GENERAL de teclado (se lanzará estando Java activo, aunque no haya una ventana gráfica activa -e independientemente de cuál esté activa-)
	 * @param kl
	 */
	public static void addKeyListener( KeyListener kl ) {
		keyListeners.add( kl );
	}

	/** Indica si la tecla Ctrl está siendo pulsada en este momento
	 * @return	true si está pulsada, false en caso contrario
	 */
	public boolean isControlPulsado() {
		return controlActivo;
	}
	
	/** Devuelve el código de la tecla pulsada actualmente
	 * @return	código de tecla pulsada, 0 si no hay ninguna. Si hay varias, se devuelve la última que se pulsó.<br>
	 * 			Si se pulsan varias a la vez, tras soltar la primera este método devuelve 0.
	 */
	public int getCodTeclaQueEstaPulsada() {
		return codTeclaActualmentePulsada;
	}
	
	/** Devuelve el código de la última tecla tecleada (pulsada y soltada). Tras eso, borra la tecla (solo se devuelve una vez)
	 * @return	Código de la última tecla tecleada. Si no ha sido tecleada ninguna o ya se ha consultado, se devuelve 0.
	 */
	public int getCodUltimaTeclaTecleada() {
		int ret = codTeclaTecleada;
		codTeclaTecleada = 0;
		return ret;
	}

	/** Devuelve la información de si una tecla está o no pulsada actualmente
	 * @param codTecla	Código de tecla a chequear
	 * @return	true si la tecla está pulsada, false en caso contrario.
	 */
	public boolean isTeclaPulsada( int codTecla ) {
		return teclasPulsadas.contains(codTecla);
	}
	
	/** Pone modo de dibujado (por defecto el modo es de dibujado inmediato = true)
	 * @param dibujadoInmediato	true si cada orden de dibujado inmediatamente pinta la ventana. false si se
	 * van acumulando las órdenes y se pinta la ventana solo al hacer un {@link #repaint()}.
	 */
	public void setDibujadoInmediato( boolean dibujadoInmediato ) {
		this.dibujadoInmediato = dibujadoInmediato;
	}

	/** Informa del modo de dibujado (por defecto el modo es de dibujado inmediato = true)
	 * @return true si cada orden de dibujado inmediatamente pinta la ventana. false si se
	 * van acumulando las órdenes y se pinta la ventana solo al hacer un {@link #repaint()}.
	 */
	public boolean isDibujadoInmediato() {
		return dibujadoInmediato;
	}

		// Variable local para guardar las imágenes y no recargarlas cada vez
		private static volatile HashMap<String,ImageIcon> recursosGraficos = new HashMap<>();
		
	/** Carga una imagen de un fichero gráfico y la dibuja en la ventana. Si la imagen no puede cargarse, no se dibuja nada.
	 * @param recursoGrafico	Nombre del fichero (path absoluto desde la carpeta raíz de clases del proyecto)  (p. ej. "/img/prueba.png")
	 * @param centroX	Coordenada x de la ventana donde colocar el centro de la imagen 
	 * @param centroY	Coordenada y de la ventana donde colocar el centro de la imagen
	 * @param anchuraDibujo	Pixels de anchura con los que dibujar la imagen (se escala de acuerdo a su tamaño)
	 * @param alturaDibujo	Pixels de altura con los que dibujar la imagen (se escala de acuerdo a su tamaño)
	 * @param zoom	Zoom a aplicar (mayor que 1 aumenta la imagen, menor que 1 y mayor que 0 la disminuye)
	 * @param radsRotacion	Rotación en radianes
	 * @param opacity	Opacidad (0.0f = totalmente transparente, 1.0f = totalmente opaca)
	 */
	public void dibujaImagen( String recursoGrafico, double centroX, double centroY, 
			int anchuraDibujo, int alturaDibujo, double zoom, double radsRotacion, float opacity ) {
		ImageIcon ii = recursosGraficos.get( recursoGrafico );
		if (ii==null) {
			try {
				ii = new ImageIcon( VentanaGrafica.class.getResource( recursoGrafico ));
			} catch (NullPointerException e) {
				return;
			}
			recursosGraficos.put( recursoGrafico, ii );
		}
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR); // Configuración para mejor calidad del gráfico escalado
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);	
		graphics.translate( centroX-anchuraDibujo/2, centroY-alturaDibujo/2 );
		graphics.rotate( radsRotacion, anchuraDibujo/2, alturaDibujo/2 );  // Incorporar al gráfico la rotación definida
		graphics.setComposite(AlphaComposite.getInstance( AlphaComposite.SRC_OVER, opacity ) ); // Incorporar la transparencia definida
        int anchoDibujado = (int)Math.round(anchuraDibujo*zoom);  // Calcular las coordenadas de dibujado con el zoom, siempre centrado en el label
        int altoDibujado = (int)Math.round(alturaDibujo*zoom);
        int difAncho = (anchuraDibujo - anchoDibujado) / 2;  // Offset x para centrar
        int difAlto = (alturaDibujo - altoDibujado) / 2;     // Offset y para centrar
        graphics.drawImage( ii.getImage(), difAncho, difAlto, anchoDibujado, altoDibujado, null);  // Dibujar la imagen con el tamaño calculado tras aplicar el zoom
		graphics.setTransform( new AffineTransform() );  // Restaurar graphics  (sin rotación ni traslación)
		graphics.setComposite(AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ));  // Restaurar graphics (pintado sin transparencia)
		if (dibujadoInmediato) repaint();
	}

	/** Carga una imagen de un fichero gráfico y la dibuja en la ventana. Si la imagen no puede cargarse, no se dibuja nada.
	 * @param recursoGrafico	Nombre del fichero (path absoluto desde la carpeta raíz de clases del proyecto)  (p. ej. "/img/prueba.png")
	 * @param centroX	Coordenada x de la ventana donde colocar el centro de la imagen 
	 * @param centroY	Coordenada y de la ventana donde colocar el centro de la imagen
	 * @param zoom	Zoom a aplicar (mayor que 1 aumenta la imagen, menor que 1 y mayor que 0 la disminuye)
	 * @param radsRotacion	Rotación en radianes
	 * @param opacity	Opacidad (0.0f = totalmente transparente, 1.0f = totalmente opaca)
	 */
	public void dibujaImagen( String recursoGrafico, double centroX, double centroY, 
			double zoom, double radsRotacion, float opacity ) {
		ImageIcon ii = recursosGraficos.get( recursoGrafico );
		if (ii==null) {
			try {
				ii = new ImageIcon( VentanaGrafica.class.getResource( recursoGrafico ));
			} catch (NullPointerException e) {
				return;
			}
			recursosGraficos.put( recursoGrafico, ii );
		}
		dibujaImagen( recursoGrafico, centroX, centroY, ii.getIconWidth(), ii.getIconHeight(), zoom, radsRotacion, opacity);
	}
	
		private transient JPanel pBotonera = null;
	/** Añade un botón de acción a la botonera superior
	 * @param texto	Texto del botón
	 * @param evento	Evento a lanzar en la pulsación del botón
	 */
	public void anyadeBoton( String texto, ActionListener evento ) {
		JButton b = new JButton( texto );
		if (pBotonera==null) {
			pBotonera = new JPanel();
			pBotonera.add( b );
			ventana.getContentPane().add( pBotonera, BorderLayout.NORTH );
			ventana.revalidate();
		} else {
			pBotonera.add( b );
			pBotonera.revalidate();
		}
		b.addActionListener( evento );
	}

	/** Añade un escuchador al cambio de tamaño del panel de dibujado de la ventana
	 * @param l	Escuchador de cambio de tamaño a añadir
	 */
	public void addComponentListener( ComponentListener l ) {
		panel.addComponentListener( l );
	}
	
	/** Elimina un escuchador de cambio de tamaño del panel de dibujado de la ventana
	 * @param l	Escuchador de cambio de tamaño a eliminar
	 */
	public void removeComponentListener( ComponentListener l ) {
		panel.removeComponentListener( l );
	}

		// Variable local para cuadro de texto
		private volatile JTextField tfEntrada;
		private volatile boolean finLecturaTexto;
		private volatile String retornoLecturaTexto;
		
	/** Saca un cuadro en la ventana y permite que el usuario introduzca un texto. La llamada
	 * solo retorna después de que el usuario introduzca el texto y pulse <Enter> o <Escape> (o salga del cuadro de texto).
	 * Si se pulsa <Escape> se devuelve null
	 * @param x	Coordenada x de la esquina superior izquierda del cuadro
	 * @param y	Coordenada y de la esquina superior izquierda del cuadro
	 * @param anchura	Anchura en píxels del cuadro de texto
	 * @param altura	Altura en píxels del cuadro de texto
	 * @param texto	Texto inicial en el cuadro
	 * @param font	Tipo de letra con el que sacar el texto en ese cuadro
	 * @param color	Color del texto
	 * @return	Texto leído antes de la pulsación de enter, null si se pulsa escape o se saca el foco del cuadro (por ejemplo al cambiar de ventana)
	 */
	public String leeTexto( double x, double y, int anchura, int altura, String texto, Font font, Color color ) {
		if (tfEntrada==null) { 
			tfEntrada = new JTextField();
			tfEntrada.addKeyListener( new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode()==KeyEvent.VK_ENTER) {  // Se acaba cuando se pulsa enter
						finLecturaTexto = true;
						retornoLecturaTexto = tfEntrada.getText();
						tfEntrada.setVisible( false );
						panel.repaint();
					} else if (e.getKeyCode()==KeyEvent.VK_ESCAPE) {  // Se acaba cuando se pulsa escape
						finLecturaTexto = true;
						retornoLecturaTexto = null;
						tfEntrada.setVisible( false );
						panel.repaint();
					}
				}
			});
			tfEntrada.addFocusListener( new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					if (!finLecturaTexto) {  // Si ya ha acabado la edición no se considera el evento de foco
						finLecturaTexto = true;
						retornoLecturaTexto = null;
						tfEntrada.setVisible( false );
						panel.repaint();
					}
				}
			});
			panel.add( tfEntrada );
		}
		finLecturaTexto = false;
		tfEntrada.setBounds( (int)x, (int)y, anchura, altura );
		tfEntrada.setFont( font );
		tfEntrada.setForeground( color );
		tfEntrada.setText( texto==null ? "" : texto );
		tfEntrada.setSelectionStart( 0 );
		tfEntrada.setSelectionEnd( tfEntrada.getText().length() );
		tfEntrada.setVisible( true );
		tfEntrada.requestFocus();
		while (!finLecturaTexto) {
			try { Thread.sleep( 100 ); } catch (Exception e) {}
		}
		return retornoLecturaTexto;
	}
	
	/** Muestra un cuadro de diálogo encima de la ventana y espera a que el usuario lo pulse
	 * @param titulo	Título del diálogo
	 * @param mensaje	Texto a mostrar
	 */
	public void sacaDialogo( String titulo, String mensaje ) {
		JOptionPane.showMessageDialog( ventana, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE );
	}
	
}
