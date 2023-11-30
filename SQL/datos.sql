USE parquimetros;

INSERT INTO conductores VALUES(42831094,"Julian","Gori","corrientes 123",2914042945,000001);
INSERT INTO conductores VALUES(41345433,"Walter","Fake","Av alem 80",291345467,000002);
INSERT INTO conductores VALUES(40293829,"Francisco","Rat","pilmayquen 1024",567890123,000003);
INSERT INTO conductores VALUES(32039483,"Antonio","Car","12 de octubre 12",345678901,000004);
INSERT INTO conductores VALUES(09283920,"Tobias","Bot","19 de mayo 456",234567890,000005);

INSERT INTO automoviles VALUES("ABC123","Chevrolet","Agile","Negro",42831094);
INSERT INTO automoviles VALUES("DYD123","Chevrolet","Cruze","Azul",41345433);
INSERT INTO automoviles VALUES("KVT191","Ford","Raptor","Azul",40293829);
INSERT INTO automoviles VALUES("QWE123","Renault","Clio","Rojo",32039483);
INSERT INTO automoviles VALUES("ASD321","Fiat","Toro","Blanco",09283920);

INSERT INTO tipos_tarjeta VALUES ("peda",0.34);
INSERT INTO tipos_tarjeta VALUES ("neta",0.00);
INSERT INTO tipos_tarjeta VALUES ("premium",0.50);

INSERT INTO tarjetas VALUES (NULL,900.00,"peda","ABC123");
INSERT INTO tarjetas VALUES (NULL,000.00,"neta","DYD123");
INSERT INTO tarjetas VALUES (NULL,000.00,"neta","KVT191");
INSERT INTO tarjetas VALUES (NULL,100.00,"neta","QWE123");
INSERT INTO tarjetas VALUES (NULL,090.00,"premium","ASD321");

INSERT INTO recargas VALUES (NULL,'2023-09-4','12:01:23',12,512);
INSERT INTO recargas VALUES (NULL,'2023-03-13','09:01:53',0,200);
INSERT INTO recargas VALUES (NULL,'2023-02-4','11:01:24',0,500);
INSERT INTO recargas VALUES (NULL,'2023-01-23','13:01:21',100,750);
INSERT INTO recargas VALUES (NULL,'2023-06-12','14:01:13',90,990);

INSERT INTO inspectores VALUES(000001,42091188,"Raul","Hernandez",MD5("1234"));
INSERT INTO inspectores VALUES(000002,20945123,"Daniela","Garcia",MD5("Pupi789"));
INSERT INTO inspectores VALUES(000003,37841945,"Oscar","Marquez",MD5("LauraTeAmo23"));
INSERT INTO inspectores VALUES(000004,29431841,"Martin","Cano",MD5("Turquesa001"));
INSERT INTO inspectores VALUES(000005,30089212,"Micala","Zarate",MD5("Tayl0r904"));

INSERT INTO ubicaciones VALUES ("Belgrano",200,10.50);
INSERT INTO ubicaciones VALUES ("Alem",1000,09.00);
INSERT INTO ubicaciones VALUES ("Paraguay",700,07.50);
INSERT INTO ubicaciones VALUES ("Sarmiento",400,11.90);
INSERT INTO ubicaciones VALUES ("Caronti",1600,08.50);

INSERT INTO parquimetros VALUES (000001,245,"Belgrano",200);
INSERT INTO parquimetros VALUES (000002,1423,"Alem",1000);
INSERT INTO parquimetros VALUES (000003,723,"Paraguay",700);
INSERT INTO parquimetros VALUES (000004,423,"Sarmiento",400);
INSERT INTO parquimetros VALUES (000005,1678,"Caronti",1600);

INSERT INTO estacionamientos VALUES(000001,000002,'2023-10-23','12:01:23',NULL,NULL);
INSERT INTO estacionamientos VALUES(000002,000002,'2023-07-20','15:03:17','2023-07-20','18:45:10');

INSERT INTO accede VALUES(000001,000002,'2023-07-23','08:04:15');
INSERT INTO accede VALUES(000005,000004,'2022-12-2','11:17:01');
INSERT INTO accede VALUES(000003,000001,'2023-09-11','09:00:00');

INSERT INTO asociado_con VALUES(NULL,000001,"Alem",1000,"ju","m");
INSERT INTO asociado_con VALUES(NULL,000001,"Alem",1000,"ju","t");
INSERT INTO asociado_con VALUES(NULL,000005,"Sarmiento",400,"sa","m");
INSERT INTO asociado_con VALUES(NULL,000003,"Belgrano",200,"mi","t");

INSERT INTO multa VALUES(NULL,'2023-09-11','11:34:01',"KVT191",000002);
INSERT INTO multa VALUES(NULL,'2023-08-12','10:45:11',"ABC123",000001);