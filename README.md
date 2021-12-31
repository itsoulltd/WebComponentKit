## Setup Using Jitpack:

[![](https://jitpack.io/v/itsoulltd/WebComponentKit.svg)](https://jitpack.io/#itsoulltd/WebComponentKit/1.0-RELEASE)

        Step 1. Add the JitPack repository to your build file
                <repositories>
                    <repository>
                        <id>jitpack.io</id>
                        <url>https://jitpack.io</url>
                    </repository>
                </repositories>
                
        Step 2. Add the dependency as you required
                2.1: HttpRestClient API
                <dependency>
                    <groupId>com.github.itsoulltd.WebComponentKit</groupId>
                    <artifactId>http-rest-client</artifactId>
                    <version>1.0-RELEASE</version>
                </dependency>
                
                2.2: JsqlEditorComponenet for webapp
                <dependency>
                    <groupId>com.github.itsoulltd.WebComponentKit</groupId>
                    <artifactId>jsql-editor-components</artifactId>
                    <version>1.0-RELEASE</version>
                </dependency>
                
                2.3: JJWTWebToken parser for webapp
                <dependency>
                    <groupId>com.github.itsoulltd.WebComponentKit</groupId>
                    <artifactId>jjwt-web-token</artifactId>
                    <version>1.0-RELEASE</version>
                </dependency>

                2.4: Vaadin Custom Component Library for Vaddin webapp
                <dependency>
                    <groupId>com.github.itsoulltd.WebComponentKit</groupId>
                    <artifactId>vaadin-component</artifactId>
                    <version>1.0-RELEASE</version>
                </dependency>
                
## How To Use API:
        
        ###Let's know about Message.java:
        ###Message is derived from Entity.java
        Message message = new Message();
        message.setEvent(new Event()
                .setEventType(EventType.ADD)
                .setUuid(UUID.randomUUID().toString())
                .setTimestamp(String.valueOf(new Date().getTime())));

        String str = Message.getJsonSerializer().writeValueAsString(message);
        String str2 = message.toString();
        System.out.println("Message was: " + message.toString());

        ###Custom Event:
        Message messageC = new Message();
        messageC.setEvent(new MyCustomEvent()
                .setPassenger(new Passenger())
                .setEventType(EventType.ACTIVATE)
                .setUuid(UUID.randomUUID().toString())
                .setTimestamp(String.valueOf(new Date().getTime())));
        System.out.println("Custom Event Message was: " + messageC.toString());
        
        ###Now recreate Message from Json:
        String remoteJson = messageC.toString();
        Message myRemoteMessage = Message.unmarshal(Message.class, remoteJson);
        System.out.println("Both Custom Message is same: " + ( myRemoteMessage.getEvent().getUuid().equals(messageC.getEvent().getUuid()) ? "YES" : "NO" ));
        
        ###Let's know about Response.java:
        ###Response is derived from Message.java
        Response response = new Response().setStatus(200).setMessage("Successful Transmission");
        System.out.println("Response was: " + response.toString());
        
        ###Let's know about PagingQuery.java & SearchQuery.java:
        SearchQuery query = Pagination.createQuery(SearchQuery.class
                , 10
                , SortOrder.ASC
                , "CLUSTER_NAME","REGION_NAME", "AM_NAME");

        query.add("ROLE_NAME").isEqualTo("Gittu")
                .or("PERSON_MOBILE").isEqualTo("01712645571")
                .and("AGE").isGreaterThen(32);

        System.out.println("Newly-Created: " + query.toString());
        ###Output:
        {
            "page":0,"size":10,
            "descriptors":[{"order":"ASC","keys":["CLUSTER_NAME","REGION_NAME","AM_NAME"]}],
            "properties":[
                {"key":"ROLE_NAME","value":"Gittu","operator":"EQUAL","type":"STRING","nextKey":"PERSON_MOBILE","logic":"OR"},
                {"key":"PERSON_MOBILE","value":"01712645571","operator":"EQUAL","type":"STRING","nextKey":"AGE","logic":"AND"},
                {"key":"AGE","value":"32","operator":"GREATER_THAN","type":"INT"}]
        }
        
        ###Now Assume we have a Json String: (carrying over Http Request @Body)
        String json = "{\"page\":0,\"size\":10,\"descriptors\":[{\"order\":\"ASC\",\"keys\":[\"CLUSTER_NAME\",\"REGION_NAME\",\"AM_NAME\"]}],\"properties\":[{\"key\":\"ROLE_NAME\",\"value\":\"Gittu\",\"operator\":\"EQUAL\",\"type\":\"STRING\",\"nextKey\":\"PERSON_MOBILE\",\"logic\":\"OR\"},{\"key\":\"PERSON_MOBILE\",\"value\":\"01712645571\",\"operator\":\"EQUAL\",\"type\":\"STRING\",\"nextKey\":\"AGE\",\"logic\":\"AND\"},{\"key\":\"AGE\",\"value\":\"32\",\"operator\":\"GREATER_THAN\",\"type\":\"INT\"}]}\n";
        SearchQuery recreated = Message.unmarshal(SearchQuery.class, json);
        System.out.println("Re-Created: " + recreated.toString());
        ###Output:
        {
            "page":0,"size":10,
            "descriptors":[{"order":"ASC","keys":["CLUSTER_NAME","REGION_NAME","AM_NAME"]}],
            "properties":[
                {"key":"ROLE_NAME","value":"Gittu","operator":"EQUAL","type":"STRING","nextKey":"PERSON_MOBILE","logic":"OR"},
                {"key":"PERSON_MOBILE","value":"01712645571","operator":"EQUAL","type":"STRING","nextKey":"AGE","logic":"AND"},
                {"key":"AGE","value":"32","operator":"GREATER_THAN","type":"INT"}]
        }

### Task & Task-Runtime:
        
#### How to define a Task:
        
        public class ExampleTask extends AbstractTask<Message, Response> {
        
            //Either override default constructor:
            public ExampleTask() {super();}
            //OR
            //Provide an custom constructor:
            public ExampleTask(String data) {
                super(new Property("data", data));
            }
    
            @Override
            public Response execute(Message message) throws RuntimeException {
                String savedData = getPropertyValue("data").toString();
                //....
                //....
                return new Response().setMessage(savedData).setStatus(200);
            }
    
            @Override
            public Response abort(Message message) throws RuntimeException {
                String reason = message != null ? message.getPayload() : "UnknownError!";
                return new Response().setMessage(reason).setStatus(500);
            }
        }
        
        
#### Create and Running Task Using TaskStack:
        
        ###Defining a TaskStack:
        private TaskStack stack = TaskStack.createSynch(false);
        
        stack.push(new SimpleTask("Wow bro! I am Adams"));
        
        stack.push(new SimpleTask("Hello bro! I am Hayes"));
        
        stack.push(new SimpleTask("Hi there! I am Cris", (message) -> {
            Event event = message.getEvent(Event.class);
            event.setMessage("Converted Message");
            event.setStatus(201);
            message.setEvent(event);
            return message;
        }));
        
        stack.push(new SimpleTask("Let's bro! I am James"));
        
        stack.commit(false, (result, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(result.toString());
            latch.countDown();
        });
        
        ### Output:
        Doing jobs...Let's bro! I am James
        Doing jobs...Hi there! I am Cris
        Doing jobs...Hello bro! I am Hayes
        {"message":"Converted Message","status":201}
        Doing jobs...Wow bro! I am Adams
        State: Finished
        {"payload":"{\"message\":\"Converted Message\",\"status\":201}","status":200}
        
        ### Doing Abort
        
        stack.push(new SimpleTask("Wow bro! I am Adams"));
        
        stack.push(new AbortTask("Hello bro! I am Hayes"));
        
        stack.push(new SimpleTask("Hi there! I am Cris"));
        
        stack.push(new SimpleTask("Let's bro! I am James"));
        
        stack.commit(false, (result, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(result.toString());
            latch.countDown();
        });
        
        ### Output:
        Doing jobs...Let's bro! I am James
        Doing jobs...Hi there! I am Cris
        Doing revert ...:Hello bro! I am Hayes
        Doing revert ...:Hi there! I am Cris
        Doing revert ...:Let's bro! I am James
        State: Failed
        {"payload":"{\"status\":500,\"error\":\"I AM Aborting! Critical Error @ (Hello bro! I am Hayes)\"}","status":502}
        
#### Make a Registration Task Flow:
        TaskStack regStack = TaskStack.createSync(true);
        
        regStack.push(new CheckUserExistTask("ahmed@yahoo.com"));
        
        regStack.push(new RegistrationTask("ahmed@yahoo.com"
                , "5467123879"
                , "ahmed@yahoo.com"
                , "0101991246"
                , new Date()
                , 32));
                
        regStack.push(new SendEmailTask("xbox-support@msn.com"
                , "ahmed@yahoo.com"
                , "Hi There! .... Greetings"
                , "new-reg-email-temp-01"));
                
        regStack.push(new SendSMSTask("01100909001"
                , "01786987908"
                , "Your Registration Completed! Plz check your email."
                , "new-reg-sms-temp-01"));
                
        regStack.commit(true, (message, state) -> {
            System.out.println("Registration Status: " + state.name());
        });
        
##### To know more about Task & TaskStack & TaskQueue, visit test classes. Thank you!
        
#### Please Contact for extended support.
      email@ m.towhid.islam@gmail.com
      call@  +8801712645571
##### [Towhidul Islam @linkedin](https://www.linkedin.com/in/mtowhidislam/)
      Available for Hiring (full-time or contractual)
##### Tech-Experience: 
      Spring-5.0, Spring-CoreReactor, SpringBoot 2.0, Redis, ActiveMQ, Cassandra & Kafka, 
      Mysql/PostgresQL/Aws-RDS 
##### Apps Development:      
      iOS, Android, Vaadin-8,10,14
##### AWS Solution Architect Associate Level (Practitioner)
      EC2, S3, RDS, HA-Architecture, CloudFormation, Multi AG Replication, VPC-Config, Security Analysis, IAM/SecurityGroup/NACL.
##### Orchestration: 
      Docker, Docker-SWARM, kubernetes
##### Tools: 
      Eclipse, IntelliJ Idea, Xcode, AndroidStudio
##### Language:
      Java, Swift, Objective-C, C/C++, Android-Java, Scala, Kotlin, Phython, Node-JS, JS       

