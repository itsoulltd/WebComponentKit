DROP table client IF EXISTS;
DROP table account IF EXISTS;
DROP table transaction_history IF EXISTS;
DROP table transaction_leg IF EXISTS;

CREATE TABLE client (
   ref VARCHAR(250) NOT NULL,
   tenant_ref VARCHAR(250) NOT NULL,
   creation_date TIMESTAMP NOT NULL,
   PRIMARY KEY(ref, tenant_ref)
);

CREATE TABLE account (
  id INTEGER IDENTITY,
  client_ref VARCHAR(250) NOT NULL,
  tenant_ref VARCHAR(250) NOT NULL,
  account_ref VARCHAR(20) NOT NULL,
  amount DECIMAL(20,2) NOT NULL,
  currency VARCHAR(3) NOT NULL
);

CREATE TABLE transaction_history (
  client_ref VARCHAR(250) NOT NULL,
  tenant_ref VARCHAR(250) NOT NULL,
  transaction_ref VARCHAR(20) NOT NULL,
  transaction_type VARCHAR(20) NOT NULL,
  transaction_date TIMESTAMP NOT NULL,
  PRIMARY KEY(client_ref, tenant_ref, transaction_ref)
);

CREATE TABLE transaction_leg (
	client_ref VARCHAR(250) NOT NULL,
	tenant_ref VARCHAR(250) NOT NULL,
	transaction_ref VARCHAR(20) NOT NULL, 
	account_ref VARCHAR(20) NOT NULL,
	entry VARCHAR(2) NOT NULL,
	amount DECIMAL(20,2) NOT NULL, 
	currency VARCHAR(3) NOT NULL,
	balance DECIMAL(20,2) NOT NULL,
    eventTimestamp VARCHAR(512) NULL,
    signature VARCHAR(512) NULL
);
