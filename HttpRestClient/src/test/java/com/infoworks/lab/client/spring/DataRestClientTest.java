package com.infoworks.lab.client.spring;

import com.infoworks.lab.client.data.rest.Any;
import com.infoworks.lab.client.data.rest.Links;
import com.infoworks.lab.client.data.rest.Page;
import com.infoworks.lab.client.data.rest.PaginatedResponse;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

public class DataRestClientTest {

    @Test
    public void doLoadTest() throws IOException {
        //
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);

        dataSource.setEnableLogging(true);
        PaginatedResponse response = dataSource.load();
        Assert.assertTrue(response != null);

        Page page = response.getPage();
        Assert.assertTrue(page != null);

        Links links = response.getLinks();
        Assert.assertTrue(links != null);
    }

    @Test
    public void doAsyncLoadTest() throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        //
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        dataSource.load((response) -> {
            //In-case of exception:
            if (response.getStatus() >= 400) {
                System.out.println(response.getError());
                latch.countDown();
            }
            //When success:
            Assert.assertTrue(response != null);

            Page page = response.getPage();
            Assert.assertTrue(page != null);

            Links links = response.getLinks();
            Assert.assertTrue(links != null);
            //
            latch.countDown();
        });

        latch.await();
    }

    @Test
    public void addSingleItem() throws MalformedURLException {
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        Passenger newPassenger = new Passenger();
        newPassenger.setName("Sohana Islam Khan");
        newPassenger.setAge(28);
        newPassenger.setSex("FEMALE");
        newPassenger.setActive(true);
        newPassenger.setDob(new Date(Instant.now().plus(28 * 365, ChronoUnit.DAYS).toEpochMilli()));
        //Create:
        Object id = dataSource.add(newPassenger);
        Assert.assertTrue(id != null);
    }

    @Test
    public void readTest() throws MalformedURLException {
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        dataSource.load();
        //
        Passenger passenger = dataSource.read(1l);
        Assert.assertTrue(passenger != null);
        System.out.println(passenger.getName());
    }

    @Test
    public void sizeTest() throws MalformedURLException {
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        dataSource.load();
        //
        int size = dataSource.size();
        Assert.assertTrue(size >= 0);
        System.out.println("Size is: " + size);
    }

    @Test
    public void readNextTest() throws MalformedURLException {
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        Optional<List<Passenger>> passengers = dataSource.next();
        Assert.assertTrue(passengers.isPresent());
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
    }

    @Test
    public void readAsyncNextTest() throws MalformedURLException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        //
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        dataSource.next((passengers) -> {
            Assert.assertTrue(passengers.isPresent());
            latch.countDown();
        });
        latch.await();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
    }

    @Test
    public void CRUDTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        Passenger newPassenger = new Passenger();
        newPassenger.setName("Sohana Islam Khan");
        newPassenger.setAge(28);
        newPassenger.setSex("FEMALE");
        newPassenger.setActive(true);
        newPassenger.setDob(new Date(Instant.now().plus(28 * 365, ChronoUnit.DAYS).toEpochMilli()));
        //Create:
        Object id = dataSource.add(newPassenger);
        Assert.assertTrue(id != null);
        //Read One:
        Passenger read = dataSource.read(id);
        Assert.assertTrue(read != null);
        //Read from local:
        Object[] items = dataSource.readSync(0, dataSource.size());
        Stream.of(items).forEach(item -> {
            if (item instanceof Passenger)
                System.out.println(((Passenger) item).getName());
        });
        //Update:
        newPassenger.setName("Dr. Sohana Islam Khan");
        dataSource.put(id, newPassenger);
        //Read again: (will read from local)
        Passenger readAgain = dataSource.read(id);
        System.out.println(readAgain.getName());
        //Delete:
        System.out.println("Count before delete: " + dataSource.size());
        dataSource.remove(id);
        System.out.println("Count after delete: " + dataSource.size());
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        dataSource.close();
    }

    @Test
    public void readAllPages() throws MalformedURLException {
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        //Read All Pages Until last page:
        dataSource.load();
        Optional<List<Passenger>> opt;
        do {
            opt = dataSource.next();
            System.out.println("Current Page: " + dataSource.currentPage());
            System.out.println("Local Size: " + dataSource.size());
        } while (opt.isPresent());
        //
        Object[] all = dataSource.readSync(0, dataSource.size());
        Stream.of(all).forEach(item -> {
            if (item instanceof Passenger)
                System.out.println(((Passenger) item).getName());
        });
    }

    @Test
    public void whenCreatesEmptyOptional_thenCorrect() {
        Optional<String> empty = Optional.empty();
        Assert.assertFalse(empty.isPresent());
        //Available on Java-11:
        //Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void givenOptional_whenIsPresentWorks_thenCorrect() {
        Optional<String> opt = Optional.of("Baeldung");
        Assert.assertTrue(opt.isPresent());

        opt = Optional.ofNullable(null);
        Assert.assertFalse(opt.isPresent());
    }

    /////////////////////////////////////////////////////////////////////////////

    public static class Passenger extends Any<Long> {
        private String name;
        private String sex = "NONE";
        private int age = 18;
        private Date dob = new java.sql.Date(new Date().getTime());
        private boolean active;

        public Passenger() {}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Date getDob() {
            return dob;
        }

        public void setDob(Date dob) {
            this.dob = dob;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    /////////////////////////////////////////////////////////////////////////////
}
