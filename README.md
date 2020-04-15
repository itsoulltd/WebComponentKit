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

### TaskStack:

#### Create and Running Task.java

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
        
        ### Doing Search Query to Server:
        
        SearchQuery query = Pagination.createQuery(SearchQuery.class, 10, SortOrder.DESC, "name","age","salary");
        
        query.add("center")
                .isEqualTo("#geohash-id")
                .and("radius")
                .isEqualTo(500.0);

        String json = query.toString();
        
        System.out.println(json);
        
        ### Output:
        {   
            "page":0,
            "size":10,
            "descriptors":[{"order":"DESC","keys":["name","age","salary"]}],
            "properties":[
                {"key":"center","value":"#geohash-id","operator":"EQUAL","type":"STRING","nextKey":"radius","logic":"AND"},
                {"key":"radius","value":"500.0","operator":"EQUAL","type":"DOUBLE"}]
        }
        
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

