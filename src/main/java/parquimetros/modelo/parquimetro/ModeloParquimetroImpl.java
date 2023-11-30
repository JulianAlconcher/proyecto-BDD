package parquimetros.modelo.parquimetro;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parquimetros.modelo.ModeloImpl;
import parquimetros.modelo.beans.ParquimetroBean;
import parquimetros.modelo.beans.ParquimetroBeanImpl;
import parquimetros.modelo.beans.TarjetaBean;
import parquimetros.modelo.beans.TarjetaBeanImpl;
import parquimetros.modelo.beans.TipoTarjetaBean;
import parquimetros.modelo.beans.TipoTarjetaBeanImpl;
import parquimetros.modelo.beans.AutomovilBean;
import parquimetros.modelo.beans.AutomovilBeanImpl;
import parquimetros.modelo.beans.ConductorBean;
import parquimetros.modelo.beans.ConductorBeanImpl;
import parquimetros.modelo.beans.UbicacionBean;
import parquimetros.modelo.beans.UbicacionBeanImpl;
import parquimetros.modelo.parquimetro.dto.EntradaEstacionamientoDTOImpl;
import parquimetros.modelo.parquimetro.dto.EstacionamientoDTO;
import parquimetros.modelo.parquimetro.dto.SalidaEstacionamientoDTOImpl;
import parquimetros.modelo.parquimetro.exception.ParquimetroNoExisteException;
import parquimetros.modelo.parquimetro.exception.SinSaldoSuficienteException;
import parquimetros.modelo.parquimetro.exception.TarjetaNoExisteException;
import parquimetros.utils.Mensajes;

public class ModeloParquimetroImpl extends ModeloImpl implements ModeloParquimetro {

	private static Logger logger = LoggerFactory.getLogger(ModeloParquimetroImpl.class);
	
	@Override
	public ArrayList<TarjetaBean> recuperarTarjetas() throws Exception {
		logger.info(Mensajes.getMessage("ModeloParquimetroImpl.recuperarTarjetas.logger"));
		/** 
		 *      Debe retornar una lista de TarjetasBean con todas las tarjetas almacenadas en la B.D. 
		 *      Deberia propagar una excepción si hay algún error en la consulta.
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl. 
		 */
		ArrayList<TarjetaBean> tarjetas = new ArrayList<TarjetaBean>();
		String consulta = "select * from tarjetas NATURAL JOIN tipos_tarjeta NATURAL JOIN automoviles NATURAL JOIN conductores;";
		PreparedStatement statement = this.conexion.prepareStatement(consulta);
		ResultSet resultado = statement.executeQuery();
		while(resultado.next()) {
			TarjetaBean tarjeta= new TarjetaBeanImpl();
			tarjeta.setId(resultado.getInt("id_tarjeta"));
			tarjeta.setSaldo(resultado.getInt("saldo"));
			TipoTarjetaBean tipo = new TipoTarjetaBeanImpl();
			tipo.setDescuento(resultado.getDouble("descuento"));
			tipo.setTipo(resultado.getString("tipo"));
			tarjeta.setTipoTarjeta(tipo);
			AutomovilBean auto = new AutomovilBeanImpl();
			auto.setColor(resultado.getString("color"));
			auto.setMarca(resultado.getString("marca"));
			auto.setPatente(resultado.getString("patente"));
			auto.setModelo(resultado.getString("modelo"));
			ConductorBean conductor = new ConductorBeanImpl();
		    conductor.setNombre(resultado.getString("nombre"));
		    conductor.setApellido(resultado.getString("apellido"));
		    conductor.setNroDocumento(resultado.getInt("dni"));
		    conductor.setDireccion(resultado.getString("direccion"));
		    conductor.setRegistro(resultado.getInt("registro"));
		    conductor.setTelefono(resultado.getString("telefono"));
		    auto.setConductor(conductor);
		    tarjeta.setAutomovil(auto);
		    tarjetas.add(tarjeta);
		}
		statement.close();
		resultado.close();
		return tarjetas;
	}

	
	/*
	 * Atención: Este codigo de recuperarUbicaciones (como el de recuperarParquimetros) es igual en el modeloParquimetro 
	 *           y en modeloInspector. Se podría haber unificado en un DAO compartido. Pero se optó por dejarlo duplicado
	 *           porque tienen diferentes permisos ambos usuarios y quizas uno estaría tentado a seguir agregando metodos
	 *           que van a resultar disponibles para ambos cuando los permisos de la BD no lo permiten.
	 */	
	@Override
	public ArrayList<UbicacionBean> recuperarUbicaciones() throws Exception {
		
		logger.info(Mensajes.getMessage("ModeloParquimetroImpl.recuperarUbicaciones.logger"));
		
		/** 
		 *      Debe retornar una lista de UbicacionesBean con todas las ubicaciones almacenadas en la B.D. 
		 *      Deberia propagar una excepción si hay algún error en la consulta.
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl. 
		 */	
		Statement statement=this.conexion.createStatement();
		String consulta="select * from ubicaciones";
		ResultSet resultado=statement.executeQuery(consulta);
		ArrayList<UbicacionBean> ubicaciones = new ArrayList<UbicacionBean>();
		
        while(resultado.next()) {
        	UbicacionBean ubicacion=new UbicacionBeanImpl();
    		ubicacion.setAltura(resultado.getInt("altura"));
        	ubicacion.setCalle(resultado.getString("calle"));
    		ubicacion.setTarifa(resultado.getInt("tarifa"));
    		ubicaciones.add(ubicacion);
    		
        }
        if (ubicaciones.size()==0) {
        	throw new Exception("No existe ninguna ubicacion en la BD");
        }
        resultado.close();
        statement.close();
		return ubicaciones;
	}

	@Override
	public ArrayList<ParquimetroBean> recuperarParquimetros(UbicacionBean ubicacion) throws Exception {
		logger.info(Mensajes.getMessage("ModeloParquimetroImpl.recuperarParquimetros.logger"));
		
		/** 
		 * 		Debe retornar una lista de ParquimetroBean con todos los parquimetros que corresponden a una ubicación.
		 * 		 
		 *      Debería propagar una excepción si hay algún error en la consulta.
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl. 
		 */
		ArrayList<ParquimetroBean> parquimetros = new ArrayList<ParquimetroBean>();
		String consulta = "select * from parquimetros where calle = ? and altura = ?;";
		PreparedStatement statement = this.conexion.prepareStatement(consulta);
		statement.setString(1, ubicacion.getCalle());
		statement.setInt(2, ubicacion.getAltura());
		ResultSet resultado = statement.executeQuery();
	
		while(resultado.next()) {
			ParquimetroBean parquimetro = new ParquimetroBeanImpl();
			parquimetro.setId(resultado.getInt("id_parq"));
			parquimetro.setNumero(resultado.getInt("numero"));
			parquimetro.setUbicacion(ubicacion);
			parquimetros.add(parquimetro);
		}
		statement.close();
		resultado.close();
		return parquimetros;
	}

	@Override
	public EstacionamientoDTO conectarParquimetro(ParquimetroBean parquimetro, TarjetaBean tarjeta)
			throws SinSaldoSuficienteException, ParquimetroNoExisteException, TarjetaNoExisteException, Exception {

		logger.info(Mensajes.getMessage("ModeloParquimetroImpl.conectarParquimetro.logger"),parquimetro.getId(),tarjeta.getId());
		
		/**
		 *      Invoca al stored procedure conectar(...) que se encarga de realizar en una transacción la apertura o cierre 
		 *      de estacionamiento segun corresponda.
		 *      
		 *      Segun la infromacion devuelta por el stored procedure se retorna un objeto EstacionamientoDTO o
		 *      dependiendo del error se produce la excepción correspondiente:
		 *       SinSaldoSuficienteException, ParquimetroNoExisteException, TarjetaNoExisteException     
		 *  
		 */
		String consulta = "call conectar ( ?, ? );";
		PreparedStatement st = this.conexion.prepareStatement(consulta);
		st.setInt(1, tarjeta.getId());
		st.setInt(2, parquimetro.getId());
		ResultSet rs = st.executeQuery();
		rs.next();
		String resultado = rs.getString("resultado");
		if (resultado.equals("Saldo de la tarjeta insuficiente")) { 
			throw new SinSaldoSuficienteException(Mensajes.getMessage("ControladorParquimetroImpl.conectarParquimetro.SinSaldoSuficienteException"));
		}
		if (resultado.equals("Parquimetro inexistente en la base de datos")) {
			throw new ParquimetroNoExisteException(Mensajes.getMessage("ControladorParquimetroImpl.conectarParquimetro.ParquimetroNoExisteException"));
		}
		if (resultado.equals("Tarjeta inexistente en la base de datos")) {
			throw new TarjetaNoExisteException(Mensajes.getMessage("ControladorParquimetroImpl.conectarParquimetro.TarjetaNoExisteException"));
		}
		EstacionamientoDTO estacionamiento = null;
		if(resultado.equals("La operacion se realizo con exito")) {
			String operacion = rs.getString("operacion");
			if (operacion.equals("Apertura")) { 		
				String tiempoRestante = rs.getString("tiempoRestante");	
				String fecha = rs.getString("fecha");	
				String hora = rs.getString("hora");	
				estacionamiento = new EntradaEstacionamientoDTOImpl(tiempoRestante,fecha,hora);
			} else if (operacion.equals("Cierre")) { 
				String tiempo = rs.getString("tiempoTranscurrido");	
				String saldo = rs.getString("saldoActual");
				String fechaEntrada = rs.getString("fechaEntrada");	
				String horaEntrada = rs.getString("horaEntrada");	
				String fechaSalida = rs.getString("fechaSalida");	
				String horaSalida = rs.getString("horaSalida");	
				estacionamiento = new SalidaEstacionamientoDTOImpl(tiempo,
																	saldo,
																	fechaEntrada,
																	horaEntrada,
																	fechaSalida,
																	horaSalida);
			}
		}else {
			throw new Exception();
		}
		
		st.close();
		rs.close();
	
		return estacionamiento;	
	}

}

