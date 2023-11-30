package parquimetros.controlador;

//Interface que define el contrato para un Controlador genérico by Tobias Thiessen & Julian Alconcher
public interface Controlador {
	
	/**
	 * Metodo que define para ejecutar el controlador luego de su creación para no poner dicho comportamiento en el constructor.
	 */
	public void ejecutar();
}
