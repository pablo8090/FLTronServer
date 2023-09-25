CREATE TABLE IF NOT EXISTS FLTRON.USER (
	id BIGSERIAl PRIMARY KEY,
   username character varying(50) NOT NULL,
   email character varying(250) NOT NULL,
   password character varying(128) NOT NULL
)	