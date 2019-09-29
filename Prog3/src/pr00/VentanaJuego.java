package pr00;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class VentanaJuego extends JFrame{
	
	Coche coche; 

	
	public static void main(String[] args) {
		VentanaJuego v = new VentanaJuego();
		v.setVisible( true );
		Coche coche = new Coche(100, 100, 150, 100, "yo");
		System.out.println(coche);
		JLabelCoche imagenCoche = new JLabelCoche();
		imagenCoche.setIcon("src/img/coche.png");
		v.add(imagenCoche, BorderLayout.CENTER);
	}
	
	public VentanaJuego() {
		JPanel panel = new JPanel(); 
		panel.setLayout(new BorderLayout());
		this.getContentPane().add(panel, "Center");
		panel.setBackground(Color.white);
		setSize(600, 450);
		setTitle( "Practica 0" );
		JPanel pBotonera = new JPanel();
		JButton boton1 = new JButton( "Acelerar" );
		JButton boton2 = new JButton( "Frenar" );
		JButton boton3 = new JButton( "Gira Izq." );
		JButton boton4 = new JButton( "Gira Der." );
		pBotonera.add(boton1);
		pBotonera.add(boton2);
		pBotonera.add(boton3);
		pBotonera.add(boton4);
		panel.add(pBotonera, "South");	
		
		
		
		
		//Escuchadores
		boton1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				coche.acelera(5); 		
			}
			
		});
		
		boton2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				coche.acelera(-5);
				
			}
			
		});
		
		boton3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				coche.gira(-10);
				
				
			}
	});
		boton4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				coche.gira(10);	
			}
	});
	}

}
