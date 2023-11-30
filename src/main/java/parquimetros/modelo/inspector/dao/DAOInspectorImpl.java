package parquimetros.modelo.inspector.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parquimetros.modelo.beans.InspectorBean;
import parquimetros.modelo.beans.InspectorBeanImpl;
import parquimetros.modelo.inspector.exception.InspectorNoAutenticadoException;
import parquimetros.utils.Mensajes;

public class DAOInspectorImpl implements DAOInspector {

	private static Logger logger = LoggerFactory.getLogger(DAOInspectorImpl.class);
	
	private Connection conexion;
	
	public DAOInspectorImpl(Connection c) {
		this.conexion = c;
	}

	@Override
	public InspectorBean autenticar(String legajo, String password) throws InspectorNoAutenticadoException, Exception {
		/** 
		 * 		Código que autentica que exista en la B.D. un legajo de inspector y que el password corresponda a ese legajo
		 *      (recuerde que el password guardado en la BD está encriptado con MD5) 
		 *      En caso exitoso deberá retornar el inspectorBean.
		 *      Si la autenticación no es exitosa porque el legajo no es válido o el password es incorrecto
		 *      deberá generar una excepción InspectorNoAutenticadoException 
		 *      y si hubo algún otro error deberá producir y propagar una Exception.
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se inicializa en el constructor.      
		 */
	
		String consulta = "SELECT * FROM inspectores WHERE legajo = ? AND password = MD5(?)";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setString(1, legajo);
		ps.setString(2, password);
		ResultSet resultado= ps.executeQuery();
		
        InspectorBean inspector;
        boolean encontre = resultado.next();
		if(!encontre) {
			throw new InspectorNoAutenticadoException(Mensajes.getMessage("DAOInspectorImpl.autenticar.inspectorNoAutenticado"));
		}	
		else if (encontre) { 
			inspector = new InspectorBeanImpl();
			inspector.setLegajo(Integer.parseInt(legajo));
			inspector.setApellido(resultado.getString("apellido"));
			inspector.setNombre(resultado.getString("nombre"));
			inspector.setDNI(resultado.getInt("dni"));	
			inspector.setPassword(resultado.getString("password"));
		}
		else { 
			throw new Exception(Mensajes.getMessage("DAOInspectorImpl.autenticar.errorConexion"));
		}
		ps.close();
		resultado.close();
		return inspector;
		
	}	


}
