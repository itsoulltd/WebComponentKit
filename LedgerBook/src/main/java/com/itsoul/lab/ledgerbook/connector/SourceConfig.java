package com.itsoul.lab.ledgerbook.connector;

/**
 *
 */
public enum SourceConfig {

  EMBEDDED_H2("org.h2.Driver", "jdbc:h2:mem:dataSource;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_UPPER=FALSE",
          "sa", "", "h2-schema.sql"),
  EMBEDDED_HSQL("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:dataSource", "sa", "", "h2-schema.sql"),
  EMBEDDED_DERBY("org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:memory:dataSource;create=true",
          "sa", "", "derby-schema.sql"),
  JDBC_MYSQL("com.mysql.jdbc.Driver", "", "", "", "mysql-schema.sql"),
  JDBC_POSTGRES("org.postgresql.Driver", "", "", "", "postgres-schema.sql"),
  JDBC_ORACLE("oracle.jdbc.OracleDriver", "", "", "", "oracle-schema.sql");

  private final String driverClassName;
  private final String url;
  private final String username;
  private final String password;
  private final String schema;

  SourceConfig(String driverClassName, String url, String username, String password,
               String schema) {
    this.driverClassName = driverClassName;
    this.url = url;
    this.username = username;
    this.password = password;
    this.schema = schema;
  }

  public String getDriverClassName() {
    return driverClassName;
  }

  public String getUrl() {
    return url;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getSchema() {
    return schema;
  }
}

