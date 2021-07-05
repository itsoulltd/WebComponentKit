CREATE TABLE IF NOT EXISTS client (
   ref VARCHAR(250) NOT NULL,
   tenant_ref VARCHAR(250) NOT NULL,
   creation_date DATETIME NOT NULL,
   PRIMARY KEY(ref, tenant_ref)
);

CREATE TABLE IF NOT EXISTS account (
  id INTEGER AUTO_INCREMENT,
  client_ref VARCHAR(250) NOT NULL,
  tenant_ref VARCHAR(250) NOT NULL,
  account_ref VARCHAR(20) NOT NULL,
  amount DECIMAL(20,2) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS transaction_history (
  client_ref VARCHAR(250) NOT NULL,
  tenant_ref VARCHAR(250) NOT NULL,
  transaction_ref VARCHAR(20) NOT NULL,
  transaction_type VARCHAR(20) NOT NULL,
  transaction_date DATETIME NOT NULL,
  PRIMARY KEY(client_ref, tenant_ref, transaction_ref)
);

CREATE TABLE IF NOT EXISTS transaction_leg (
  client_ref VARCHAR(250) NOT NULL,
  tenant_ref VARCHAR(250) NOT NULL,
  transaction_ref VARCHAR(20) NOT NULL,
  account_ref VARCHAR(20) NOT NULL,
  amount DECIMAL(20,2) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  eventTimestamp VARCHAR(512) NULL,
  signature VARCHAR(512) NULL
);
