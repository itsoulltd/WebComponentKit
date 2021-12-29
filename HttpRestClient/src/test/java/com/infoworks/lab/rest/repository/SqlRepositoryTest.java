package com.infoworks.lab.rest.repository;

import com.infoworks.lab.rest.models.Gender;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.Passenger;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.models.pagination.Pagination;
import com.infoworks.lab.rest.models.pagination.SortOrder;
import com.it.soul.lab.connect.DriverClass;
import com.it.soul.lab.sql.SQLExecutor;
import org.junit.*;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.util.List;

public class SqlRepositoryTest {

    private SQLExecutor executor;

    private SQLExecutor getExecutor() throws Exception {
        if (executor != null) return executor;
        executor = new SQLExecutor.Builder(DriverClass.H2_EMBEDDED)
                .database(System.getenv("app.db.name"))
                .host(System.getenv("app.db.host"), System.getenv("app.db.port"))
                .credential(System.getenv("app.db.username"), System.getenv("app.db.password"))
                .build();
        return executor;
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
        executor.close();
        executor = null;
    }

    @Test
    public void search() throws Exception {
        PassengerSQLRepository repository = new PassengerSQLRepository(getExecutor());
        ItemCount count = repository.rowCount();
        int max = count.getCount().intValue();
        int offset = 0;
        int limit = 2;
        int numOfPage = (max / limit) + 1;
        while (offset < numOfPage){
            SearchQuery query = Pagination.createQuery(SearchQuery.class, limit, SortOrder.ASC);
            query.setPage(offset + 1);
            List<Passenger> riders = repository.search(query);
            riders.forEach(rider -> System.out.println(rider.getName()));
            offset++;
        }
    }

    @Test
    public void fetch() throws Exception {
        PassengerSQLRepository repository = new PassengerSQLRepository(getExecutor());
        ItemCount count = repository.rowCount();
        int max = count.getCount().intValue();
        int offset = 0;
        int limit = 2;
        int numOfPage = (max / limit) + 1;
        while (offset < numOfPage){
            List<Passenger> riders = repository.fetch(offset, limit);
            riders.forEach(rider -> System.out.println(rider.getName()));
            offset++;
        }
    }

    @Test
    public void rowCount() throws Exception {
        PassengerSQLRepository repository = new PassengerSQLRepository(getExecutor());
        ItemCount count = repository.rowCount();
        Assert.assertTrue(count != null);
        System.out.println("Count is " + count.getCount());
    }

    @Test
    public void doa() throws Exception {
        PassengerSQLRepository repository = new PassengerSQLRepository(getExecutor());
        //Create & Insert:
        Passenger created = repository.insert(new Passenger("Tictoc", Gender.NONE, 18));
        if(created != null) {
            System.out.println("Created: " + created.getName());
            //Update:
            created.setName("Tictoc-up");
            Passenger update = repository.update(created, created.getId());
            if (update != null){
                System.out.println("Updated: " + update.getName());
                //Delete:
                boolean isDeleted = repository.delete(update.getId());
                System.out.println("Is Deleted : " + isDeleted);
            }
        }
    }

    public static class PassengerSQLRepository implements SqlRepository<Passenger, Integer> {

        private SQLExecutor executor;

        public PassengerSQLRepository(SQLExecutor executor) {
            this.executor = executor;
        }

        @Override
        public SQLExecutor getExecutor() {
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