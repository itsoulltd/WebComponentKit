package com.infoworks.lab.rest.repository;

import com.infoworks.lab.rest.models.Gender;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.Passenger;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.models.pagination.Pagination;
import com.infoworks.lab.rest.models.pagination.SortOrder;
import com.it.soul.lab.connect.DriverClass;
import com.it.soul.lab.connect.JDBConnection;
import com.it.soul.lab.connect.io.ScriptRunner;
import com.it.soul.lab.sql.SQLExecutor;
import org.junit.*;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SqlRepositoryTest {

    private SQLExecutor executor;

    private SQLExecutor getExecutor() throws Exception {
        if (executor != null) return executor;
        executor = new SQLExecutor.Builder(DriverClass.MYSQL)
                .database(System.getenv("app.db.name"))
                .host(System.getenv("app.db.host"), System.getenv("app.db.port"))
                .credential(System.getenv("app.db.username"), System.getenv("app.db.password"))
                .build();
        runScripts();
        return executor;
    }

    private void runScripts() throws SQLException {
        ScriptRunner runner = new ScriptRunner();
        Connection conn = new JDBConnection.Builder(DriverClass.MYSQL)
                .host(System.getenv("app.db.host"), System.getenv("app.db.port"))
                .database(System.getenv("app.db.name"))
                .credential(System.getenv("app.db.username"), System.getenv("app.db.password"))
                .build();
        //
        File file = new File("testDB.sql");
        String[] cmds = runner.commands(runner.createStream(file));
        runner.execute(cmds, conn);
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

    private List<Integer> insert(PassengerSQLRepository repository){
        String[] names = new String[]{"Tina","Rina","James","Gomaje","Nahin"};
        Integer[] ages = new Integer[] {15, 18, 28, 26, 32, 34, 25, 67};
        //
        Random rand = new Random();
        List<Integer> uuids = new ArrayList<>();
        //
        Passenger created1 = repository.insert(new Passenger(names[rand.nextInt(names.length)], Gender.FEMALE, ages[rand.nextInt(names.length)]));
        uuids.add(created1.getId());
        Passenger created2 = repository.insert(new Passenger(names[rand.nextInt(names.length)], Gender.FEMALE, ages[rand.nextInt(names.length)]));
        uuids.add(created2.getId());
        Passenger created3 = repository.insert(new Passenger(names[rand.nextInt(names.length)], Gender.NONE, ages[rand.nextInt(names.length)]));
        uuids.add(created3.getId());
        Passenger created4 = repository.insert(new Passenger(names[rand.nextInt(names.length)], Gender.TRANSGENDER, ages[rand.nextInt(names.length)]));
        uuids.add(created4.getId());
        Passenger created5 = repository.insert(new Passenger(names[rand.nextInt(names.length)], Gender.MALE, ages[rand.nextInt(names.length)]));
        uuids.add(created5.getId());
        Passenger created6 = repository.insert(new Passenger(names[rand.nextInt(names.length)], Gender.NONE, ages[rand.nextInt(names.length)]));
        uuids.add(created6.getId());
        Passenger created7 = repository.insert(new Passenger(names[rand.nextInt(names.length)], Gender.MALE, ages[rand.nextInt(names.length)]));
        uuids.add(created7.getId());
        //
        return uuids;
    }

    private void deleteInserted(PassengerSQLRepository repository, List<Integer> ids){
        ids.forEach(id -> {
            try {
                repository.delete(id);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void search() throws Exception {
        PassengerSQLRepository repository = new PassengerSQLRepository(getExecutor());
        List<Integer> ids = insert(repository);
        //
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
            System.out.println("========================");
            offset++;
        }
        //
        deleteInserted(repository, ids);
    }

    @Test
    public void fetch() throws Exception {
        PassengerSQLRepository repository = new PassengerSQLRepository(getExecutor());
        List<Integer> ids = insert(repository);
        //
        ItemCount count = repository.rowCount();
        int max = count.getCount().intValue();
        int offset = 0;
        int limit = 2;
        int numOfPage = (max / limit) + 1;
        while (offset < numOfPage){
            List<Passenger> riders = repository.fetch(offset, limit);
            riders.forEach(rider -> System.out.println(rider.getName()));
            System.out.println("========================");
            offset++;
        }
        //
        deleteInserted(repository, ids);
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
            ItemCount count = repository.rowCount();
            System.out.println("After Inserted Count: " + count.getCount());
            //Update:
            created.setName("Tictoc-up");
            Passenger update = repository.update(created, created.getId());
            if (update != null){
                System.out.println("Updated: " + update.getName());
                //Delete:
                boolean isDeleted = repository.delete(update.getId());
                System.out.println("Is Deleted : " + isDeleted);
                ItemCount count2 = repository.rowCount();
                System.out.println("After Deleted Count: " + count2.getCount());
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