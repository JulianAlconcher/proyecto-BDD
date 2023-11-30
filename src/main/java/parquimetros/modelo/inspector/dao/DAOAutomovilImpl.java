package parquimetros.modelo.inspector.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parquimetros.modelo.beans.InspectorBean;
import parquimetros.modelo.beans.InspectorBeanImpl;
import parquimetros.modelo.inspector.exception.AutomovilNoEncontradoException;
import parquimetros.modelo.inspector.exception.InspectorNoAutenticadoException;
import parquimetros.utils.Mensajes;

public class DAOAutomovilImpl implements DAOAutomovil {

	private static Logger logger = LoggerFactory.getLogger(DAOAutomovilImpl.class);
	
	private Connection conexion;
	
	public DAOAutomovilImpl(Connection conexion) {
		this.conexion = conexion;
	}

	@Override
	public void verificarPatente(String patente) throws AutomovilNoEncontradoException, Exception {
		/** 
		 *      Debe verificar que exista la patente en la tabla automoviles. 
		 * 		Deberá generar una excepción AutomovilNoEncontradoException en caso de no encontrarlo. 
		 *      Si hay algún error en la consulta o en la conexión deberá propagar la excepción.    
		 *          
		 */
		String consulta = "select * from automoviles where patente=?";
		PreparedStatement statement = conexion.prepareStatement(consulta);
		statement.setString(1, patente);
		ResultSet resultado = statement.executeQuery();
        boolean encontre = resultado.next();

		statement.close();
		resultado.close();
		
		if(!encontre) {
			throw new AutomovilNoEncontradoException(Mensajes.getMessage("DAOAutomovilImpl.recuperarAutomovilPorPatente.AutomovilNoEncontradoException"));			
		}	
	}

}
