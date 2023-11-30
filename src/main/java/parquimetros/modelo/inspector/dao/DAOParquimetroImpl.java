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
import parquimetros.modelo.beans.ParquimetroBean;
import parquimetros.modelo.beans.UbicacionBean;
import parquimetros.modelo.beans.UbicacionBeanImpl;
import parquimetros.modelo.inspector.dao.datosprueba.DAOParquimetrosDatosPrueba;

public class DAOParquimetroImpl implements DAOParquimetro {

	private static Logger logger = LoggerFactory.getLogger(DAOParquimetroImpl.class);
	
	private Connection conexion;
	
	public DAOParquimetroImpl(Connection c) {
		this.conexion = c;
	}

	@Override
	public UbicacionBean recuperarUbicacion(ParquimetroBean parquimetro) throws Exception {
		/**
		 *      Recuperar  de la B.D. la ubicación de un parquimetro a patir de su ID
		 * 
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se inicializa en el constructor.   
		 */

		String consulta = "select calle,altura,tarifa from inspectores where id= ? ";
		PreparedStatement statement = conexion.prepareStatement(consulta);
		statement.setInt(1, parquimetro.getId());
		ResultSet resultado = statement.executeQuery();
		
		UbicacionBean ubicacion = new UbicacionBeanImpl() ;
		ubicacion.setCalle(resultado.getString("calle"));
		ubicacion.setAltura(resultado.getInt("altura"));
		ubicacion.setTarifa(resultado.getInt("tarifa"));
		
		statement.close();
		resultado.close();
		return ubicacion;
	}
}
