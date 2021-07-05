package com.infoworks.lab.simulator;

import java.util.Calendar;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Runtime {

    protected static Logger LOG = Logger.getLogger(Runtime.class.getSimpleName());

    protected abstract void run();

    protected void onShutdown(){
        LOG.info("Application is shutting down.");
    }

    protected synchronized void waitMethod(String name, int round, long timeout) {
        int counter = 0;
        while (true) {
            try {
                LOG.info(name + " running ==> " + Calendar.getInstance().getTime());
                this.wait(timeout);
                if (round > 0 && counter++ > round) break;
            } catch (Exception e) {
                LOG.info(e.getMessage());
            }
        }
    }

    protected static Optional<Object> getSystemProperty(String property, Class type){
        if (System.getProperty(property) == null){
            return Optional.ofNullable(null);
        }
        if (type.isAssignableFrom(String.class)){
            return Optional.ofNullable(System.getProperty(property));
        }else if (type.isAssignableFrom(Integer.class)){
            try {
                Integer value = Integer.valueOf(System.getProperty(property));
                return Optional.ofNullable(value);
            } catch (NumberFormatException e) {
                LOG.log(Level.WARNING, e.getMessage(), e);
            }
        }else if (type.isAssignableFrom(Long.class)){
            try {
                Long value = Long.valueOf(System.getProperty(property));
                return Optional.ofNullable(value);
            } catch (NumberFormatException e) {
                LOG.log(Level.WARNING, e.getMessage(), e);
            }
        }
        return Optional.ofNullable(null);
    }

    protected int numberScanner(String cmdStr, Integer defaultValue) {
        int size = defaultValue;
        while (true){
            try{
                System.out.print(cmdStr);
                String commend = new Scanner(System.in).nextLine();
                if (commend.startsWith("@")) break;
                size = Integer.valueOf(commend);
                break;
            }catch (NumberFormatException e) {continue;}
        }
        return size;
    }

    protected String stringScanner(String cmdStr, Integer max){
        if (max <= 0) max = 1;
        String commend = "@";
        while (true){
            System.out.print(cmdStr);
            commend = new Scanner(System.in).nextLine();
            if (commend.length() <= max) break;
        }
        return commend;
    }

    protected boolean boolScanner(String cmdStr){
        System.out.print(cmdStr + "? :(yes/no)");
        String commend = new Scanner(System.in).nextLine();
        return commend.toLowerCase().startsWith("y");
    }

    protected boolean randomBoolean(){
        int rand = new Random().nextInt(2);
        boolean accept = (rand >= 1 ? true : false);
        return accept;
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Optional optClassName = getSystemProperty("classname", String.class);
        if (optClassName.isPresent() == false){
            LOG.info("classname can't be null");
            System.exit(0);
        }
        String className = optClassName.get().toString();
        int round =  0;
        Optional optRound = getSystemProperty("round", Integer.class);
        if (optRound.isPresent()){
            round = (int) optRound.get();
        }
        long timeout = 60000;
        Optional optTimeout = getSystemProperty("timeout", Long.class);
        if (optTimeout.isPresent()){
            timeout = (long)optTimeout.get();
        }
        if (className != null && !className.isEmpty()){
            Class type = Class.forName(className);
            Object device = type.newInstance();
            if (device instanceof Runtime){
                java.lang.Runtime.getRuntime().addShutdownHook(new Thread(() -> ((Runtime)device).onShutdown()));
                ((Runtime)device).run();
                ((Runtime)device).waitMethod(type.getSimpleName(), round, timeout);
            }
        }
    }
}
