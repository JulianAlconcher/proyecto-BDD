package parquimetros.modelo.inspector;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parquimetros.modelo.ModeloImpl;
import parquimetros.modelo.beans.InspectorBean;
import parquimetros.modelo.beans.ParquimetroBean;
import parquimetros.modelo.beans.ParquimetroBeanImpl;
import parquimetros.modelo.beans.UbicacionBean;
import parquimetros.modelo.beans.UbicacionBeanImpl;
import parquimetros.modelo.inspector.dao.DAOParquimetro;
import parquimetros.modelo.inspector.dao.DAOParquimetroImpl;
import parquimetros.modelo.inspector.dao.DAOInspector;
import parquimetros.modelo.inspector.dao.DAOInspectorImpl;
import parquimetros.modelo.inspector.dao.DAOAutomovil;
import parquimetros.modelo.inspector.dao.DAOAutomovilImpl;
import parquimetros.modelo.inspector.dto.EstacionamientoPatenteDTO;
import parquimetros.modelo.inspector.dto.EstacionamientoPatenteDTOImpl;
import parquimetros.modelo.inspector.dto.MultaPatenteDTO;
import parquimetros.modelo.inspector.dto.MultaPatenteDTOImpl;
import parquimetros.modelo.inspector.exception.AutomovilNoEncontradoException;
import parquimetros.modelo.inspector.exception.ConexionParquimetroException;
import parquimetros.modelo.inspector.exception.InspectorNoAutenticadoException;
import parquimetros.modelo.inspector.exception.InspectorNoHabilitadoEnUbicacionException;
import parquimetros.utils.Mensajes;

public class ModeloInspectorImpl extends ModeloImpl implements ModeloInspector {

	private static Logger logger = LoggerFactory.getLogger(ModeloInspectorImpl.class);	
	
	public ModeloInspectorImpl() {
		logger.debug(Mensajes.getMessage("ModeloInspectorImpl.constructor.logger"));
	}

	@Override
	public InspectorBean autenticar(String legajo, String password) throws InspectorNoAutenticadoException, Exception {
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.autenticar.logger"), legajo, password);

		if (legajo==null || legajo.isEmpty() || password==null || password.isEmpty()) {
			throw new InspectorNoAutenticadoException(Mensajes.getMessage("ModeloInspectorImpl.autenticar.parametrosVacios"));
		}
		DAOInspector dao = new DAOInspectorImpl(this.conexion);
		return dao.autenticar(legajo, password);		
	}
	
	@Override
	public ArrayList<UbicacionBean> recuperarUbicaciones() throws Exception {
		/** 
		 *  	Debe retornar una lista de UbicacionesBean con todas las ubicaciones almacenadas en la B.D. 
		 *      Debería propagar una excepción si hay algún error en la consulta. 
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl.       
		 *      
		 */
		
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.recuperarUbicaciones.logger"));
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
		
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.recuperarParquimetros.logger"),ubicacion.toString());
		
		/** 
		 *      Debe retornar una lista de ParquimetroBean con todos los parquimetros que corresponden a una ubicación.
		 * 		Debería propagar una excepción si hay algún error en la consulta.
		 *            
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl.      
		 *      
		 */

		String consulta="select id_parq,numero from parquimetros WHERE calle= ? and altura=?";
		PreparedStatement statement=this.conexion.prepareStatement(consulta);
		statement.setString(1, ubicacion.getCalle());
		statement.setInt(2, ubicacion.getAltura());
		ResultSet resultado=statement.executeQuery();
		ArrayList<ParquimetroBean> parquimetros = new ArrayList<ParquimetroBean>();
		
        while(resultado.next()) {
        	ParquimetroBean parquimetro=new ParquimetroBeanImpl();
    		parquimetro.setId(resultado.getInt("id_parq"));
    		parquimetro.setNumero(resultado.getInt("numero"));
    		parquimetro.setUbicacion(ubicacion);
    		parquimetros.add(parquimetro);
    		
        }
        if (parquimetros.size()==0) {
        	 throw new Exception("No existe ninguna ubicacion en la BD");
        }
        resultado.close();
        statement.close();
		return parquimetros;
	}

	@Override
	public void conectarParquimetro(ParquimetroBean parquimetro, InspectorBean inspectorLogueado) throws ConexionParquimetroException, Exception {
		// es llamado desde Controlador.conectarParquimetro
  
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.conectarParquimetro.logger"),parquimetro.toString());
		
		/**      Simula la conexión al parquímetro con el inspector que se encuentra logueado en el momento 
		 *       en que se ejecuta la acción. 
		 *       
		 *       Debe verificar si el inspector está habilitado a acceder a la ubicación del parquímetro 
		 *       en el dia y hora actual, segun la tabla asociado_con. 
		 *       Sino puede deberá producir una excepción ConexionParquimetroException.     
		 *       En caso exitoso se registra su acceso en la tabla ACCEDE y retorna exitosamente.		         
		 *     
		 *       Si hay un error no controlado se produce una Exception genérica.
		 *       
		 *       Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *       que se hereda al extender la clase ModeloImpl.
		 *  
		 * @param parquimetro
		 * @throws ConexionParquimetroException
		 * @throws Exception
		 */		
		Statement st = conexion.createStatement();
		ResultSet rs = st.executeQuery("select curdate(),curtime()");
		rs.next();
		Date fechaActual = rs.getDate("curdate()");
		Time horaActual = rs.getTime("curtime()");
		
		String turno = getTurno(horaActual);
		String dia = getDia(fechaActual);
		String calleParquimetro = parquimetro.getUbicacion().getCalle();
		Integer alturaParquimetro = parquimetro.getUbicacion().getAltura();

		String consulta = "select * from asociado_con where legajo = ? and calle= ? and altura = ? and turno=? and dia= ?";
		PreparedStatement statement = this.conexion.prepareStatement(consulta);
		statement.setInt(1, inspectorLogueado.getLegajo());
		statement.setString(2, calleParquimetro);
		statement.setInt(3, alturaParquimetro);
		statement.setString(4, turno);
		statement.setString(5, dia);
		ResultSet resultado=statement.executeQuery();
		boolean encontre = resultado.next();
		if(encontre) {
			statement.executeUpdate("insert into accede(legajo,id_parq,fecha,hora) values (" + inspectorLogueado.getLegajo() + "," + parquimetro.getId() + ",'"
					+ fechaActual + "','" + horaActual.toString() + "')");
		}
		else if(!encontre) {
			throw new ConexionParquimetroException(Mensajes.getMessage("ControladorInspectorImpl.registraMultas.inspectorNoHabilitado"));
		}
		else {
			throw new Exception(Mensajes.getMessage("ControladorInspectorImpl.conectarParquimetro.Exception"));
		}
		statement.close();
		resultado.close();
		st.close();
		rs.close();
	}

	/**
	 * Retorna M si la hora se encuentra entre las 8hs y las 14hs.
	 * Retorna T si la hora se encuentra entre las 14hs y las 20hs.
	 * @param hora
	 * @return
	 * @throws SQLException
	 */
	private String getTurno(Time horaActual) throws SQLException {
		Time horaInicio = Time.valueOf("08:00:00");
        Time horaMedio = Time.valueOf("14:00:00");
        Time horaFin = Time.valueOf("20:00:00");
		
	    if (horaActual.after(horaMedio) && horaActual.before(horaFin)) {
	        return "t";
	    } else if (horaActual.after(horaInicio) && horaActual.before(horaMedio)) {
	        return "m";
	    }else {
	    	return "indefinido";
	    }
    }
	
	/**
	 * Retorna dos letras que representan el dia actual.
	 * Lu si es lunes, ma si es martes, etc. 
	 * @param dia
	 * @return
	 * @throws SQLException
	 */
	private String getDia(Date fecha) throws SQLException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        int diaSemana = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String[] diasDeLaSemana = {"do", "lu", "ma", "mi", "ju", "vi", "sa"};
        return diasDeLaSemana[diaSemana];
	}
	
	@Override
	public UbicacionBean recuperarUbicacion(ParquimetroBean parquimetro) throws Exception {
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.recuperarUbicacion.logger"),parquimetro.getId());
		UbicacionBean ubicacion = parquimetro.getUbicacion();
		if (Objects.isNull(ubicacion)) {
			DAOParquimetro dao = new DAOParquimetroImpl(this.conexion);
			ubicacion = dao.recuperarUbicacion(parquimetro);
		}			
		return ubicacion; 
	}

	@Override
	public void verificarPatente(String patente) throws AutomovilNoEncontradoException, Exception {
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.verificarPatente.logger"),patente);
		DAOAutomovil dao = new DAOAutomovilImpl(this.conexion);
		dao.verificarPatente(patente); 
	}	
	
	@Override
	public EstacionamientoPatenteDTO recuperarEstacionamiento(String patente, UbicacionBean ubicacion) throws Exception {

		logger.info(Mensajes.getMessage("ModeloInspectorImpl.recuperarEstacionamiento.logger"),patente,ubicacion.getCalle(),ubicacion.getAltura());
		/**
		 *      Verifica si existe un estacionamiento abierto registrado la patente en la ubicación, y
		 *	    de ser asi retorna un EstacionamientoPatenteDTO con estado Registrado (EstacionamientoPatenteDTO.ESTADO_REGISTRADO), 
		 * 		y caso contrario sale con estado No Registrado (EstacionamientoPatenteDTO.ESTADO_NO_REGISTRADO).
		 * 
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl.
		 */
		
		String consulta = "select * from estacionados where patente= ? and calle= ? and altura= ?";
		PreparedStatement statement = this.conexion.prepareStatement(consulta);
		statement.setString(1, patente);
		statement.setString(2, ubicacion.getCalle());
		statement.setInt(3, ubicacion.getAltura());
		ResultSet resultado=statement.executeQuery();
		boolean encontre = resultado.next();
		
		String fechaEntrada, horaEntrada, estado;
		
		if (encontre) {
			estado = EstacionamientoPatenteDTO.ESTADO_REGISTRADO;
	        fechaEntrada = resultado.getString("fecha_ent");
	        horaEntrada = resultado.getString("hora_ent");
			
		} else {
			estado = EstacionamientoPatenteDTO.ESTADO_NO_REGISTRADO;
	        fechaEntrada = "";
	        horaEntrada = "";
		}
		statement.close();
		resultado.close();
		
		return new EstacionamientoPatenteDTOImpl(patente, ubicacion.getCalle(), String.valueOf(ubicacion.getAltura()), fechaEntrada, horaEntrada, estado);
	}
	

	@Override
	public ArrayList<MultaPatenteDTO> generarMultas(ArrayList<String> listaPatentes, 
													UbicacionBean ubicacion, 
													InspectorBean inspectorLogueado) 
									throws InspectorNoHabilitadoEnUbicacionException, Exception {

		logger.info(Mensajes.getMessage("ModeloInspectorImpl.generarMultas.logger"),listaPatentes.size());		
		
		/** 
		 *      Primero verificar si el inspector puede realizar una multa en esa ubicacion el dia y hora actual 
		 *      segun la tabla asociado_con. Sino puede deberá producir una excepción de 
		 *      InspectorNoHabilitadoEnUbicacionException. 
		 *            
		 * 		Luego para cada una de las patentes suministradas, si no tiene un estacionamiento abierto en dicha 
		 *      ubicación, se deberá cargar una multa en la B.D. 
		 *      
		 *      Debe retornar una lista de las multas realizadas (lista de objetos MultaPatenteDTO).
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl.      
		 */
		Statement statmentFechaYHora = conexion.createStatement();
		ResultSet resultadoFechaYHora = statmentFechaYHora.executeQuery("select curdate(),curtime()");
		resultadoFechaYHora.next();
		Date fechaActual = resultadoFechaYHora.getDate("curdate()");
		Time horaActual = resultadoFechaYHora.getTime("curtime()");
		
		String turno = getTurno(horaActual);
		String dia = getDia(fechaActual);
		String calle = ubicacion.getCalle();
		Integer altura = ubicacion.getAltura();
		String consulta = "select * from asociado_con where legajo =? and calle= ? and altura = ? and turno= ? and dia= ? ";
		PreparedStatement statement = this.conexion.prepareStatement(consulta);
		statement.setInt(1, inspectorLogueado.getLegajo());
		statement.setString(2, calle);
		statement.setInt(3, altura);
		statement.setString(4, turno);
		statement.setString(5, dia);
		ResultSet resultado = statement.executeQuery();
		boolean encontre = resultado.next();
		Integer id = resultado.getInt("id_asociado_con");
		ArrayList<MultaPatenteDTO> multas = new ArrayList<MultaPatenteDTO>();
		if(encontre) {
			for (String patente : listaPatentes) {
				EstacionamientoPatenteDTO estacionamiento = this.recuperarEstacionamiento(patente,ubicacion);
				if (estacionamiento.getEstado() == EstacionamientoPatenteDTO.ESTADO_NO_REGISTRADO) {
					statement.executeUpdate("insert into multa(numero,fecha,hora,patente,id_asociado_con) values (NULL,'" + fechaActual + 
							"','" + horaActual + "','" + patente + "'," + id + ");");
					
					ResultSet resultSetMulta = statement.executeQuery("SELECT LAST_INSERT_ID()");
                    int numeroMulta = -1;
                    if (resultSetMulta.next()) {
                        numeroMulta = resultSetMulta.getInt(1);
                    }
                    
					MultaPatenteDTO multa = new MultaPatenteDTOImpl(String.valueOf(numeroMulta),
																	patente, 
																	calle, 
																	String.valueOf(altura), 
																	fechaActual.toString(), 
																	horaActual.toString(), 
																	String.valueOf(inspectorLogueado.getLegajo()));
					multas.add(multa);
				}
			}
		}
		statmentFechaYHora.close();
		resultadoFechaYHora.close();
		statement.close();
		resultado.close();
		
		return multas;		
	}
	
}
