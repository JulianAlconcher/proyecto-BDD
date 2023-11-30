package parquimetros;

import java.awt.EventQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parquimetros.controlador.login.ControladorLogin;
import parquimetros.controlador.login.ControladorLoginImpl;
import parquimetros.modelo.login.ModeloLogin;
import parquimetros.modelo.login.ModeloLoginImpl;
import parquimetros.vista.login.VentanaLogin;
import parquimetros.vista.login.VentanaLoginImpl;

// Tobias Thiessen- Julian Alconcher - BDD 2023 - UNS
// Clase para iniciar la aplicacion.


public class Parquimetros {

	private static Logger logger = LoggerFactory.getLogger(Parquimetros.class);
	
	/**
	 * Método para iniciar la aplicación
	 * 
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					logger.debug("Se inicia la aplicación.");
					
					ModeloLogin modelo = new ModeloLoginImpl();  
					VentanaLogin ventana = new VentanaLoginImpl();
					@SuppressWarnings("unused")
					ControladorLogin controlador = new ControladorLoginImpl(ventana, modelo);
					
				} catch (Exception e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}
}
