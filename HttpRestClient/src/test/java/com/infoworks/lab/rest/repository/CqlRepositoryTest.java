package com.infoworks.lab.rest.repository;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.Gender;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.PassengerCQL;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.models.pagination.Pagination;
import com.infoworks.lab.rest.models.pagination.SortOrder;
import com.it.soul.lab.cql.CQLExecutor;
import com.it.soul.lab.cql.query.ReplicationStrategy;
import org.junit.*;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CqlRepositoryTest {

    private CQLExecutor executor = null;

    public CQLExecutor getExecutor(){
        if (executor != null) return executor;
        try {
            executor = new CQLExecutor.Builder()
                    .connectTo(Integer.valueOf(System.getenv("cassandra.db.port")), System.getenv("cassandra.db.host")).build();
            Boolean newKeyspace = executor.createKeyspace(System.getenv("cassandra.db.keyspace"), ReplicationStrategy.SimpleStrategy, 3);
            if (newKeyspace){
                executor.switchKeyspace(System.getenv("cassandra.db.keyspace"));
                dropsTables(executor, PassengerCQL.class);
                createTables(executor, PassengerCQL.class);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return executor;
    }

    private void createTables(CQLExecutor cqlExecutor, Class...classes) {
        Arrays.stream(classes).forEach(aClass -> {
            try {
                boolean created = cqlExecutor.createTable(aClass);
                System.out.println(String.format("%s is created %s", aClass.getSimpleName(), (created ? "YES" : "NO")));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void dropsTables(CQLExecutor cqlExecutor, Class...classes){
        Arrays.stream(classes).forEach(aClass -> {
            try {
                boolean dropped = cqlExecutor.dropTable(aClass);
                System.out.println(String.format("%s is dropped %s", aClass.getSimpleName(), (dropped ? "YES" : "NO")));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Rule
    public final EnvironmentVariables env = new EnvironmentVariables();

    @Before
    public void before() {
        env.set("cassandra.db.host", "localhost");
        env.set("cassandra.db.port", "9042");
        env.set("cassandra.db.keyspace", "cassandradb");
        env.set("cassandra.db.username", "root");
        env.set("cassandra.db.password", "root@123");
    }

    @After
    public void tearDown() throws Exception {
        executor.close();
        executor = null;
    }

    private List<String> insert(PassengerCQLRepository repository){
        String[] names = new String[]{"Tina","Rina","James","Gomaje","Nahin"};
        Integer[] ages = new Integer[] {15, 18, 28, 26, 32, 34, 25, 67};
        //
        Random rand = new Random();
        List<String> uuids = new ArrayList<>();
        //
        PassengerCQL created1 = repository.insert(new PassengerCQL(names[rand.nextInt(names.length)], Gender.FEMALE, ages[rand.nextInt(names.length)]));
        uuids.add(created1.getUuid());
        PassengerCQL created2 = repository.insert(new PassengerCQL(names[rand.nextInt(names.length)], Gender.FEMALE, ages[rand.nextInt(names.length)]));
        uuids.add(created2.getUuid());
        PassengerCQL created3 = repository.insert(new PassengerCQL(names[rand.nextInt(names.length)], Gender.NONE, ages[rand.nextInt(names.length)]));
        uuids.add(created3.getUuid());
        PassengerCQL created4 = repository.insert(new PassengerCQL(names[rand.nextInt(names.length)], Gender.TRANSGENDER, ages[rand.nextInt(names.length)]));
        uuids.add(created4.getUuid());
        PassengerCQL created5 = repository.insert(new PassengerCQL(names[rand.nextInt(names.length)], Gender.MALE, ages[rand.nextInt(names.length)]));
        uuids.add(created5.getUuid());
        PassengerCQL created6 = repository.insert(new PassengerCQL(names[rand.nextInt(names.length)], Gender.NONE, ages[rand.nextInt(names.length)]));
        uuids.add(created6.getUuid());
        PassengerCQL created7 = repository.insert(new PassengerCQL(names[rand.nextInt(names.length)], Gender.MALE, ages[rand.nextInt(names.length)]));
        uuids.add(created7.getUuid());
        //
        return uuids;
    }

    @Test
    public void search() {
        PassengerCQLRepository repository = new PassengerCQLRepository(getExecutor());
        insert(repository);
        //
        ItemCount count = repository.rowCount();
        int max = count.getCount().intValue();
        int offset = 0;
        int limit = 2;
        int numOfPage = (max / limit) + 1;
        //
        SearchQuery query = Pagination.createQuery(SearchQuery.class, limit, SortOrder.ASC);
        //query.add("uuid").isEqualTo("pass-uuid-here");
        //
        while (offset < numOfPage){
            query.setPage(offset + 1);
            List<PassengerCQL> riders = repository.search(query);
            riders.forEach(rider -> System.out.println(rider.getName()));
            System.out.println("========================");
            offset++;
        }
    }

    @Test
    public void fetch() {
        PassengerCQLRepository repository = new PassengerCQLRepository(getExecutor());
        insert(repository);
        //
        ItemCount count = repository.rowCount();
        int max = count.getCount().intValue();
        int offset = 0;
        int limit = 2;
        int numOfPage = (max / limit) + 1;
        while (offset < numOfPage){
            List<PassengerCQL> riders = repository.fetch(offset, limit);
            riders.forEach(rider -> System.out.println(rider.getName()));
            System.out.println("========================");
            offset++;
        }
    }

    @Test
    public void rowCount() {
        PassengerCQLRepository repository = new PassengerCQLRepository(getExecutor());
        ItemCount count = repository.rowCount();
        Assert.assertTrue(count != null);
        System.out.println("Count is " + count.getCount());
    }

    @Test
    public void doa() throws HttpInvocationException {
        PassengerCQLRepository repository = new PassengerCQLRepository(getExecutor());
        //Create & Insert:
        PassengerCQL created = repository.insert(new PassengerCQL("Tictoc", Gender.NONE, 18));
        if(created != null) {
            System.out.println("Created: " + created.getName());
            ItemCount count = repository.rowCount();
            System.out.println("After Inserted Count: " + count.getCount());
            //Update:
            created.setName("Tictoc-up");
            PassengerCQL update = repository.update(created, created.getUuid());
            if (update != null){
                System.out.println("Updated: " + update.getName());
                //Delete:
                boolean isDeleted = repository.delete(update.getUuid());
                System.out.println("Is Deleted : " + isDeleted);
                ItemCount count2 = repository.rowCount();
                System.out.println("After Deleted Count: " + count2.getCount());
            }
        }
    }

    public static class PassengerCQLRepository implements CqlRepository<PassengerCQL, String>{

        private CQLExecutor executor;

        public PassengerCQLRepository(CQLExecutor executor) {
            this.executor = executor;
        }

        @Override
        public CQLExecutor getExecutor() {
            return executor;
        }

        @Override
        public Class<PassengerCQL> getEntityType() {
            return PassengerCQL.class;
        }

        @Override
        public String getPrimaryKeyName() {
            return "uuid";
        }
    }
}