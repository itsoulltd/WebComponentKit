CREATE TABLE client (
   ref VARCHAR2(250) NOT NULL,
   tenant_ref VARCHAR2(250) NOT NULL,
   creation_date TIMESTAMP(6) NOT NULL,
   PRIMARY KEY(ref, tenant_ref)
);

CREATE TABLE account (
  id NUMBER GENERATED ALWAYS AS IDENTITY,
  client_ref VARCHAR2(250) NOT NULL,
  tenant_ref VARCHAR2(250) NOT NULL,
  account_ref VARCHAR2(20) NOT NULL,
  amount NUMBER(20,2) NOT NULL,
  currency VARCHAR2(3) NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE transaction_history (
  client_ref VARCHAR2(250) NOT NULL,
  tenant_ref VARCHAR2(250) NOT NULL,
  transaction_ref VARCHAR2(20) NOT NULL,
  transaction_type VARCHAR2(20) NOT NULL,
  transaction_date TIMESTAMP(6) NOT NULL,
  PRIMARY KEY(client_ref, tenant_ref, transaction_ref)
);

CREATE TABLE transaction_leg (
  client_ref VARCHAR2(250) NOT NULL,
  tenant_ref VARCHAR2(250) NOT NULL,
  transaction_ref VARCHAR2(20) NOT NULL,
  account_ref VARCHAR2(20) NOT NULL,
  entry VARCHAR2(2) NOT NULL,
  amount NUMBER(20,2) NOT NULL,
  currency VARCHAR2(3) NOT NULL,
  balance NUMBER(20,2) NOT NULL,
  eventTimestamp VARCHAR2(512) NULL,
  signature VARCHAR2(512) NULL
);
