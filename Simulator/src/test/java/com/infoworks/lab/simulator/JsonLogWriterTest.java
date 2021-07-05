package com.infoworks.lab.simulator;

import com.it.soul.lab.sql.entity.Entity;
import org.junit.Test;

public class JsonLogWriterTest {

    @Test
    public void test(){
        JsonLogWriter writer = new JsonLogWriter(JsonLogWriterTest.class);
        Inner in = new Inner().setFname("Sohana Islam").setLname("Khan").setAge(27);
        writer.write(in);
    }

    private static class Inner extends Entity{
        private String fname;
        private String lname;
        private Integer age;

        public String getFname() {
            return fname;
        }

        public Inner setFname(String fname) {
            this.fname = fname;
            return this;
        }

        public String getLname() {
            return lname;
        }

        public Inner setLname(String lname) {
            this.lname = lname;
            return this;
        }

        public Integer getAge() {
            return age;
        }

        public Inner setAge(Integer age) {
            this.age = age;
            return this;
        }
    }
}