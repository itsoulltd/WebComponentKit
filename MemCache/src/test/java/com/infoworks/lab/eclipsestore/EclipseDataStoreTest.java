package com.infoworks.lab.eclipsestore;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.infoworks.lab.cache.models.Person;
import org.eclipse.serializer.afs.types.ADirectory;
import org.eclipse.serializer.persistence.binary.jdk8.types.BinaryHandlersJDK8;
import org.eclipse.store.afs.aws.s3.types.S3Connector;
import org.eclipse.store.afs.azure.storage.types.AzureStorageConnector;
import org.eclipse.store.afs.blobstore.types.BlobStoreFileSystem;
import org.eclipse.store.afs.nio.types.NioFileSystem;
import org.eclipse.store.afs.redis.types.RedisConnector;
import org.eclipse.store.afs.sql.types.SqlConnector;
import org.eclipse.store.afs.sql.types.SqlFileSystem;
import org.eclipse.store.afs.sql.types.SqlProviderPostgres;
import org.eclipse.store.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.eclipse.store.storage.exceptions.StorageExceptionInitialization;
import org.junit.Assert;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Duration;
import java.util.Locale;

public class EclipseDataStoreTest {

    @Test
    public void initTest() throws Exception {
        String storagePath = "target/EclipseStore/EclipseDataStoreTest";
        //Single Cons:
        EclipseDataStore<String, Person> mData = new EclipseDataStore<>(storagePath + "/One");
        mData.close();
        //Double Cons: [Will Not Set]
        EclipseDataStore<String, Person> mData2 = new EclipseDataStore<>(storagePath + "/Two", true);
        mData2.close();
        //Three Cons: [Will Not Set]
        EclipseDataStore<String, Person> mData3 = new EclipseDataStore<>(storagePath + "/Three", true, Duration.ofMinutes(0));
        mData3.close();
        //Same: [Will Not Set]
        EclipseDataStore<String, Person> mData4 = new EclipseDataStore<>(storagePath + "/Four", true, Duration.ofMinutes(-1));
        mData4.close();
        //Same: [Will Not Set]
        EclipseDataStore<String, Person> mData5 = new EclipseDataStore<>(storagePath + "/Five", false, Duration.ofMinutes(20));
        mData5.close();
        //Three Cons: [Will Be Set]
        EclipseDataStore<String, Person> mData6 = new EclipseDataStore<>(storagePath + "/Six", true, Duration.ofMinutes(1));
        mData6.close();
    }

    /**
     * https://docs.microstream.one/manual/storage/application-life-cycle.html
     * The consequence of this is:
     * If two EmbeddedStorageManager instances are started,
     * each one with a different location for its persistend data,
     * then the application has two live databases! If three or ten or 100 are started,
     * then that’s the number of live databases the application has.
     * There is no limit and no conflict between different databases inside the same application process.
     * ****************
     * The only important thing is that no two running StorageManagers can access the same data location.
     * ****************
     */
    @Test(expected = StorageExceptionInitialization.class)
    public void TwoInstanceToSameStore() {
        String storagePath = "target/EclipseStore/EclipseDataStoreTest";
        //
        EclipseDataStore<String, Person> mData = new EclipseDataStore<>(storagePath + "/TwoInstance");
        mData.put("james", new Person().setName("james").setAge(29));
        mData.save(false);
        //
        EclipseDataStore<String, Person> mData2 = new EclipseDataStore<>(storagePath + "/TwoInstance");
        Person person = mData2.read("james");
        Assert.assertNotNull(person);
    }

    @Test
    public void simpleStoreTest() throws Exception {
        String storagePath = "target/EclipseStore/EclipseDataStoreTest";
        //
        EclipseDataStore<String, Person> mData = new EclipseDataStore<>(storagePath + "/SimpleStore");
        mData.put("james", new Person().setName("james").setEmail("james@gmail.com").setAge(29));
        //mData.save(false);
        mData.close(); //close() -> also do save(...) then shutdown().
        //
        //After closing a running db, we can re-open and access any data:
        //
        EclipseDataStore<String, Person> mData2 = new EclipseDataStore<>(storagePath + "/SimpleStore");
        Person person = mData2.read("james");
        Assert.assertNotNull(person);
        System.out.println(person.getName());
        mData2.close();
    }

    @Test
    public void localFileSystem() {
        NioFileSystem fileSystem = NioFileSystem.New();
        ADirectory directory = fileSystem.ensureDirectoryPath("target"
                , "EclipseStore", "EclipseDataStoreTest", "SimpleStore");
        //Create storage manager:
        //EmbeddedStorageManager storageManager = EmbeddedStorage.start(directory);
        //Using EclipseDataStore: Create and Save:-
        try (EclipseDataStore<String, Person> mData = new EclipseDataStore<>(directory)) {
            mData.put("gosling"
                    , new Person().setName("James Gosling")
                            .setEmail("james.gosling@gmail.com").setAge(70)
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //Second Reload and Test:-
        try (EclipseDataStore<String, Person> mData = new EclipseDataStore<>(directory)) {
            Person person = mData.read("gosling");
            Assert.assertNotNull(person);
            System.out.println(person.getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //@Test
    public void localFileSystem_using_serializer_jdk_8() {
        EmbeddedStorageFoundation<?> foundation = EmbeddedStorageConfiguration.Builder()
                .setStorageDirectory("data/storage")
                .setChannelCount(Math.max(
                        1, // minimum one channel, if only 1 core is available
                        Integer.highestOneBit(Runtime.getRuntime().availableProcessors() - 1)
                ))
                .createEmbeddedStorageFoundation();

        foundation.onConnectionFoundation(BinaryHandlersJDK8::registerJDK8TypeHandlers);
        //Create storage manager:
        EmbeddedStorageManager storageManager = foundation.createEmbeddedStorageManager().start();
        EclipseDataStore store = new EclipseDataStore(storageManager, true, Duration.ofMillis(1000));
    }

    //@Test
    public void sqlFileSystem() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/mydb");
        dataSource.setUser("postgres");
        dataSource.setPassword("secret");
        SqlFileSystem fileSystem = SqlFileSystem.New(SqlConnector.Caching(SqlProviderPostgres.New(dataSource)));
        ADirectory directory = fileSystem.ensureDirectoryPath("storage");
        //Create storage manager:
        //EmbeddedStorageManager storageManager = EmbeddedStorage.start(directory);
        EclipseDataStore store = new EclipseDataStore(directory);
    }

    //@Test
    public void blobFileSystem_redis() {
        String redisUri = "redis://localhost:6379/0";
        BlobStoreFileSystem fileSystem = BlobStoreFileSystem.New(RedisConnector.Caching(redisUri));
        ADirectory directory = fileSystem.ensureDirectoryPath("storage");
        //Create storage manager:
        //EmbeddedStorageManager storageManager = EmbeddedStorage.start(directory);
        EclipseDataStore store = new EclipseDataStore(directory);
    }

    //@Test
    public void blobFileSystem_s3() {
        S3Client client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("ACCESS_KEY", "SECRET_ACCESS_KEY")
                ))
                .region(Region.EU_NORTH_1)
                .build();
        BlobStoreFileSystem fileSystem = BlobStoreFileSystem.New(S3Connector.Caching(client));
        ADirectory directory = fileSystem.ensureDirectoryPath("bucket-name", "folder", "subfolder");
        //Create storage manager:
        //EmbeddedStorageManager storageManager = EmbeddedStorage.start(directory);
        EclipseDataStore store = new EclipseDataStore(directory);
    }

    //@Test
    public void blobFileSystem_azure() {
        StorageSharedKeyCredential credential = new StorageSharedKeyCredential("accountName", "accountKey");
        String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", "accountName");
        BlobServiceClient client = new BlobServiceClientBuilder().endpoint(endpoint).credential(credential).buildClient();
        BlobStoreFileSystem fileSystem = BlobStoreFileSystem.New(AzureStorageConnector.Caching(client));
        ADirectory directory = fileSystem.ensureDirectoryPath("storage");
        //Create storage manager:
        //EmbeddedStorageManager storageManager = EmbeddedStorage.start(directory);
        EclipseDataStore store = new EclipseDataStore(directory);
    }

}