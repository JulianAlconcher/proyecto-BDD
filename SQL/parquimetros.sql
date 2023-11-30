#Creacion bdd parquimetros.
CREATE DATABASE parquimetros;

USE parquimetros;

# CREACION DE TABLAS PARA ENTIDADES

CREATE TABLE conductores(
    dni INT UNSIGNED NOT NULL,
    nombre VARCHAR(45) NOT NULL,
    apellido VARCHAR(45) NOT NULL,
    direccion VARCHAR(45) NOT NULL,
    telefono VARCHAR(45),
    registro INT UNSIGNED NOT NULL,

    CONSTRAINT lp_conductores 
    PRIMARY KEY (dni)
 ) ENGINE=InnoDB;

CREATE TABLE automoviles(    
    patente VARCHAR(6) NOT NULL,
    marca VARCHAR(45) NOT NULL,
    modelo VARCHAR(45) NOT NULL,
    color VARCHAR(45) NOT NULL,
    dni INT UNSIGNED NOT NULL,

    CONSTRAINT lp_automoviles 
    PRIMARY KEY (patente),

    CONSTRAINT lf_automoviles_dni 
    FOREIGN KEY(dni) REFERENCES conductores(dni)
    ON UPDATE CASCADE 
 ) ENGINE=InnoDB;

CREATE TABLE tipos_tarjeta(
    tipo VARCHAR(45) NOT NULL,
    descuento DECIMAL(3,2) UNSIGNED NOT NULL, 

    CONSTRAINT chk_descuento
    CHECK (descuento >= 0 and descuento <=1),

    CONSTRAINT lp_tipo 
    PRIMARY KEY (tipo)

 ) ENGINE=InnoDB;

CREATE TABLE tarjetas (
    id_tarjeta INT UNSIGNED NOT NULL AUTO_INCREMENT,
    saldo DECIMAL(5,2) NOT NULL,
    tipo VARCHAR(45) NOT NULL,
    patente VARCHAR(6) NOT NULL,

    #Llave primaria id de tarjeta.
    CONSTRAINT lp_tarjetas
    PRIMARY KEY (id_tarjeta),

    #Llave foranea tipo de tarjeta.
    CONSTRAINT lf_tarjetas_tipo 
    FOREIGN KEY (tipo) REFERENCES tipos_tarjeta(tipo)
    ON UPDATE CASCADE,

    #Llave foranea patente.
    CONSTRAINT lf_tarjetas_patente
    FOREIGN KEY (patente) REFERENCES automoviles(patente)
    ON UPDATE CASCADE 

 ) ENGINE=InnoDB;

CREATE TABLE recargas(
    id_tarjeta INT UNSIGNED NOT NULL AUTO_INCREMENT,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    saldo_anterior DECIMAL(5,2) NOT NULL,
    saldo_posterior DECIMAL(5,2) NOT NULL,

    CONSTRAINT lp_recargas_id_fecha_hora
    PRIMARY KEY (id_tarjeta,fecha,hora), 

    CONSTRAINT lf_recargas_id
    FOREIGN KEY (id_tarjeta) REFERENCES tarjetas(id_tarjeta)
    ON UPDATE CASCADE 

) ENGINE=InnoDB;

CREATE TABLE inspectores(
    legajo INT UNSIGNED NOT NULL,
    dni INT UNSIGNED NOT NULL,
    nombre VARCHAR(45) NOT NULL,
    apellido VARCHAR(45) NOT NULL,
    password VARCHAR(32) NOT NULL,
    
    CONSTRAINT lp_inspectores_legajo
    PRIMARY KEY (legajo)
) ENGINE=InnoDB;

CREATE TABLE ubicaciones(
    calle VARCHAR(45) NOT NULL,
    altura INT UNSIGNED NOT NULL,
    tarifa DECIMAL(5,2) UNSIGNED NOT NULL,

    CONSTRAINT lp_ubicaciones_calle_altura
    PRIMARY KEY (calle,altura)
) ENGINE=InnoDB;

CREATE TABLE parquimetros(
    id_parq INT UNSIGNED NOT NULL,
    numero INT UNSIGNED NOT NULL,
    calle VARCHAR(45) NOT NULL,
    altura INT UNSIGNED NOT NULL,

    CONSTRAINT lp_parquimetros_id
    PRIMARY KEY (id_parq),

    CONSTRAINT lf_parquimetro_calle_altura
    FOREIGN KEY (calle,altura) REFERENCES ubicaciones(calle,altura)
    ON UPDATE CASCADE 
) ENGINE=InnoDB;

CREATE TABLE estacionamientos(
   id_tarjeta INT UNSIGNED NOT NULL,
   id_parq INT UNSIGNED NOT NULL,
   fecha_ent DATE NOT NULL,
   hora_ent TIME NOT NULL,
   fecha_sal DATE,
   hora_sal TIME,
   
   CONSTRAINT lp_estacionamientos_id_fecha_hora_ent
   PRIMARY KEY (id_parq,fecha_ent,hora_ent),

   CONSTRAINT lf_estacionamientos_id_tarjeta
   FOREIGN KEY (id_tarjeta) REFERENCES tarjetas(id_tarjeta)
   ON UPDATE CASCADE,

   CONSTRAINT lf_estacionamientos_id_parq
   FOREIGN KEY (id_parq) REFERENCES parquimetros(id_parq)
   ON UPDATE CASCADE 
)ENGINE=InnoDB;

CREATE TABLE accede(
    legajo INT UNSIGNED NOT NULL,
    id_parq INT UNSIGNED NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,

    CONSTRAINT lp_accede_id_fecha_hora
    PRIMARY KEY (id_parq,fecha,hora),

    CONSTRAINT lf_accede_legajo
    FOREIGN KEY (legajo) REFERENCES inspectores(legajo)
    ON UPDATE CASCADE ,

    CONSTRAINT lf_accede_id_parq
    FOREIGN KEY (id_parq) REFERENCES parquimetros(id_parq)
    ON UPDATE CASCADE 
)ENGINE=InnoDB;

CREATE TABLE asociado_con(
    id_asociado_con INT UNSIGNED NOT NULL AUTO_INCREMENT,
    legajo INT UNSIGNED NOT NULL,
    calle VARCHAR(45) NOT NULL,
    altura INT UNSIGNED NOT NULL,
    dia ENUM('do','lu','ma','mi','ju','vi','sa') NOT NULL,
    turno ENUM('m','t') NOT NULL,

    CONSTRAINT lp_asociado_con_id
    PRIMARY KEY (id_asociado_con),

    CONSTRAINT lf_asociado_con_legajo
    FOREIGN KEY (legajo) REFERENCES inspectores(legajo)
    ON UPDATE CASCADE ,

    CONSTRAINT lf_asociado_con_calle_altura
    FOREIGN KEY (calle,altura) REFERENCES ubicaciones(calle,altura)
    ON UPDATE CASCADE 
)ENGINE=InnoDB;

CREATE TABLE multa(
    numero INT UNSIGNED NOT NULL AUTO_INCREMENT,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    patente VARCHAR(6) NOT NULL,
    id_asociado_con INT UNSIGNED NOT NULL,

    CONSTRAINT lp_multa_numero
    PRIMARY KEY (numero),

    CONSTRAINT lf_multa_patente
    FOREIGN KEY (patente) REFERENCES automoviles(patente)
    ON UPDATE CASCADE,

    CONSTRAINT lf_multa_id_asociado_con
    FOREIGN KEY (id_asociado_con) REFERENCES asociado_con(id_asociado_con)
    ON UPDATE CASCADE
)ENGINE=InnoDB;

#---------CREACION DEL TRIGGER-------------------
DELIMITER !
CREATE TRIGGER tr_recarga_tarjeta
AFTER UPDATE ON tarjetas
FOR EACH ROW
BEGIN
    DECLARE saldo_anterior DECIMAL(5,2);
    DECLARE saldo_posterior DECIMAL(5,2);

    IF NEW.saldo > OLD.saldo THEN
        SET saldo_anterior = OLD.saldo;
        SET saldo_posterior = NEW.saldo;

        INSERT INTO recargas (id_tarjeta, fecha, hora, saldo_anterior, saldo_posterior)
        VALUES (NEW.id_tarjeta, CURDATE(), CURTIME(), saldo_anterior, saldo_posterior);
    END IF;
END;
!
DELIMITER ;

#---------CREACION DEL PROCEDURE---------------------
delimiter !
CREATE PROCEDURE conectar(IN id_tarjeta INTEGER,IN id_parq INTEGER)
BEGIN
    DECLARE fechaEntrada , fechaSalida DATE;
    DECLARE horaEntrada, horaSalida TIME;
    DECLARE tarjeta ,parquimetro INTEGER;
    DECLARE saldoActual DECIMAL(5,2);
    DECLARE descuentoAplicado DECIMAL(3,2);
    DECLARE tiempoTranscurrido INTEGER;
    DECLARE tiempoRestante INTEGER;
    DECLARE tarifaActual DECIMAL(5,2);
    DECLARE id_parquimetro INTEGER;
    DECLARE codigo_SQL CHAR(5) DEFAULT '00000';
    DECLARE codigo_MYSQL INT DEFAULT 0;
    DECLARE mensaje_error TEXT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            GET DIAGNOSTICS CONDITION 1 codigo_MYSQL= MYSQL_ERRNO,
            codigo_SQL= RETURNED_SQLSTATE,
            mensaje_error= MESSAGE_TEXT;
            SELECT 'SQLEXCEPTION, transaccion abortada' AS resultado,
            codigo_MySQL, codigo_SQL, mensaje_error;
            ROLLBACK;
        END ;

    START TRANSACTION;

        if(id_tarjeta is not null and id_parq is not null ) then
            if(EXISTS(SELECT * FROM tarjetas t WHERE id_tarjeta = t.id_tarjeta)) then
                    if(EXISTS(SELECT * FROM parquimetros p WHERE id_parq = p.id_parq)) then
                        set tarjeta=id_tarjeta;
                        set parquimetro=id_parq;
                        SELECT saldo,descuento INTO saldoActual, descuentoAplicado FROM tarjetas t natural join tipos_tarjeta tt where tarjeta=t.id_tarjeta;
                        if(EXISTS(SELECT fecha_ent,hora_ent,fecha_sal,hora_sal FROM estacionamientos e WHERE tarjeta=e.id_tarjeta and fecha_sal is NULL and hora_sal is NULL)) then
                            SELECT tarifa into tarifaActual FROM estacionamientos e NATURAL JOIN parquimetros NATURAL JOIN ubicaciones where e.id_tarjeta=tarjeta and e.hora_sal is NULL and e.fecha_sal is NULL;
                            SELECT fecha_ent,hora_ent,fecha_sal,hora_sal INTO fechaEntrada,horaEntrada,fechaSalida,horaSalida FROM estacionamientos e WHERE e.id_tarjeta=tarjeta and e.fecha_sal is NULL and e.hora_sal is NULL;
                            set fechaSalida=CURDATE();
                            set horaSalida=CURTIME();
                            set tiempoTranscurrido = TIMESTAMPDIFF(MINUTE,CONCAT(fechaEntrada, ' ', horaEntrada),CONCAT(fechaSalida, ' ', horaSalida));
                            set saldoActual = GREATEST(-999.99, TRUNCATE(saldoActual-(tiempoTranscurrido*(tarifaActual)*(1-descuentoAplicado)),2));
                            UPDATE estacionamientos e set e.fecha_sal=fechaSalida,e.hora_sal=horaSalida where e.id_tarjeta=tarjeta and e.fecha_ent=fechaEntrada and e.hora_ent=horaEntrada;
                            UPDATE tarjetas t set t.saldo=saldoActual where t.id_tarjeta=tarjeta;
                            SELECT 'Cierre' as operacion, 'La operacion se realizo con exito' as resultado, tiempoTranscurrido, saldoActual,fechaEntrada,horaEntrada,fechaSalida,horaSalida;
                        else
                        begin
                            if(not EXISTS(SELECT fecha_ent,hora_ent, fecha_sal, hora_sal FROM estacionamientos e WHERE tarjeta=e.id_tarjeta and parquimetro=e.id_parq and fecha_sal is NULL and hora_sal is NULL limit 1)) then
                                if(saldoActual>0) then
                                    SELECT tarifa INTO tarifaActual FROM parquimetros p NATURAL JOIN ubicaciones u WHERE p.id_parq=parquimetro;
                                    set fechaEntrada=CURDATE();
                                    set horaEntrada=CURTIME();
                                    set tiempoRestante = (saldoActual/(tarifaActual*(1-descuentoAplicado)));
                                    INSERT INTO estacionamientos(id_tarjeta,id_parq,fecha_ent,hora_ent,fecha_sal,hora_sal)VALUES (tarjeta,parquimetro,fechaEntrada,horaEntrada,null,null);
                                    SELECT 'Apertura' as operacion, 'La operacion se realizo con exito' as resultado ,tiempoRestante,fechaEntrada as fecha, horaEntrada as hora;
                                else
                                    begin
                                        SELECT 'Saldo de la tarjeta insuficiente' as resultado;
                                    end;
                                end if;
                            end if ;
                        end;
                        end if;
                    else
                    begin
                        SELECT 'Parquimetro inexistente en la base de datos' as resultado;
                    end;
                    end if ;
            else
            begin
                SELECT 'Tarjeta inexistente en la base de datos' as resultado;
            end;
            end if ;
        else
        begin
            SELECT 'Tarjeta o parquimetro NULO' as resultado;
        end;
        end if ;
    COMMIT;
end; !
DELIMITER ;

#--------------------------------VISTAS---------------------------------
#VISTA estacionados
CREATE VIEW estacionados AS
SELECT calle,altura,patente,fecha_ent,hora_ent
FROM parquimetros NATURAL JOIN estacionamientos NATURAL JOIN tarjetas
WHERE estacionamientos.fecha_sal IS NULL AND estacionamientos.hora_sal IS NULL;

SHOW WARNINGS;
#--------------------------------USUARIOS-------------------------------
#Administrador, psw:"admin"
DROP USER IF EXISTS 'admin'@'localhost';
#Creo usuario
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
#Otorgo privilegios (todos)
GRANT ALL PRIVILEGES ON parquimetros.* TO 'admin'@'localhost' WITH GRANT OPTION;

#Venta, psw:"venta"
DROP USER IF EXISTS 'venta'@'localhost';
#Creo usuario
CREATE USER 'venta'@'localhost' IDENTIFIED BY 'venta';
#Otorgo privilegios
GRANT INSERT ON parquimetros.tarjetas TO 'venta'@'localhost';
GRANT UPDATE ON parquimetros.tarjetas TO 'venta'@'localhost';
GRANT SELECT ON parquimetros.tarjetas TO 'venta'@'localhost';
GRANT UPDATE ON parquimetros.recargas TO 'venta'@'localhost';

#Inspector, psw:"inspector"
DROP USER IF EXISTS 'inspector'@'localhost';
#Creo usuario
CREATE USER 'inspector'@'localhost' IDENTIFIED BY 'inspector';
#Otorgo privilegios
GRANT SELECT ON parquimetros.inspectores TO 'inspector'@'localhost';
GRANT SELECT ON parquimetros.parquimetros TO 'inspector'@'localhost';
GRANT SELECT ON parquimetros.asociado_con TO 'inspector'@'localhost';
GRANT SELECT ON parquimetros.automoviles TO 'inspector'@'localhost';
GRANT SELECT ON parquimetros.estacionados TO 'inspector'@'localhost';
GRANT SELECT ON parquimetros.ubicaciones TO 'inspector'@'localhost';
GRANT SELECT ON parquimetros.multa TO 'inspector'@'localhost';
GRANT INSERT ON parquimetros.multa TO 'inspector'@'localhost';
GRANT SELECT ON parquimetros.accede TO 'inspector'@'localhost';
GRANT INSERT ON parquimetros.accede TO 'inspector'@'localhost';

#Parquimetro, psw:"parq"
DROP USER IF EXISTS 'parquimetro'@'localhost';
#Creo usuario
CREATE USER 'parquimetro'@'localhost' IDENTIFIED BY 'parq';
#Otorgo privilegios
GRANT SELECT ON parquimetros.parquimetros TO 'parquimetro'@'localhost';
GRANT SELECT ON parquimetros.ubicaciones TO 'parquimetro'@'localhost';
GRANT SELECT ON parquimetros.automoviles TO 'parquimetro'@'localhost';
GRANT SELECT ON parquimetros.tarjetas TO 'parquimetro'@'localhost';
GRANT SELECT ON parquimetros.tipos_tarjeta TO 'parquimetro'@'localhost';
GRANT SELECT ON parquimetros.conductores TO 'parquimetro'@'localhost';
GRANT EXECUTE on procedure parquimetros.conectar to 'parquimetro'@'localhost';

