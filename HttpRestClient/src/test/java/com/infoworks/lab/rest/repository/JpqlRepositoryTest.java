package com.infoworks.lab.rest.repository;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.Passenger;
import com.it.soul.lab.jpql.service.JPQLExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import javax.persistence.EntityManager;

public class JpqlRepositoryTest {

    JPQLExecutor getExecutor(){
        //Need a Test EntityManager em:
        EntityManager em = null;
        return new JPQLExecutor(em, false);
    }

    @Rule
    public final EnvironmentVariables env = new EnvironmentVariables();

    @Before
    public void before() {
        env.set("app.db.host", "localhost");
        env.set("app.db.port", "3306");
        env.set("app.db.name", "testDB");
        env.set("app.db.username", "root");
        env.set("app.db.password", "root@123");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void search() {
    }

    @Test
    public void fetch() {
    }

    @Test
    public void rowCount() {
    }

    @Test
    public void doa() throws HttpInvocationException {
    }

    public static class PassengerJPQLRepository implements JpqlRepository<Passenger, Integer>{

        private JPQLExecutor executor;

        public PassengerJPQLRepository(JPQLExecutor executor) {
            this.executor = executor;
        }

        @Override
        public JPQLExecutor getExecutor() {
            return executor;
        }

        @Override
        public Class<Passenger> getEntityType() {
            return Passenger.class;
        }

        @Override
        public String getPrimaryKeyName() {
            return "id";
        }
    }
}