package com.infoworks.lab.rest.models;

import com.infoworks.lab.rest.models.pagination.SortOrder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BinarySearchTest {

    @Before
    public void setup(){

    }

    @After
    public void teardown(){

    }

    @Test
    public void searchBiggerItemIndex(){
        List<SearchQuery.QueryProperty> props = new ArrayList<>();
        SearchQuery.QueryProperty prop = new SearchQuery.QueryProperty();
        prop.setKey("timeToLive");
        props.add(prop);
        prop = new SearchQuery.QueryProperty();
        prop.setKey("enable_tracking");
        props.add(prop);
        prop = new SearchQuery.QueryProperty();
        prop.setKey("update_search_index");
        props.add(prop);
        prop = new SearchQuery.QueryProperty();
        prop.setKey("emission-interval");
        props.add(prop);
        prop = new SearchQuery.QueryProperty();
        prop.setKey("from");
        props.add(prop);
        prop = new SearchQuery.QueryProperty();
        prop.setKey("to");
        props.add(prop);
        props.forEach(sItem -> System.out.println(sItem.getKey()));
        System.out.println("=====================================");

        //Sort in Action
        props.sort((o1, o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()));
        List<SearchQuery.QueryProperty> sorted = props;

        sorted.forEach(sItem -> System.out.println(sItem.getKey()));
        System.out.println("=====================================");
        //

        SearchQuery.QueryProperty goingToInserted = new SearchQuery.QueryProperty();
        goingToInserted.setKey("zcenter");
        //goingToInserted.setKey("radius");
        //goingToInserted.setKey("emission-interval");
        System.out.println("New Key: " + goingToInserted.getKey());

        //This guarantees that the return value will be >= 0 if and only if the key is found.
        //And this must be the index of the search key, if it is contained in the list;
        //Otherwise, -(returnIndex) = (-(insertionPoint) - 1). The insertion point is defined as the point at which the key would be inserted into the list:
        //So, insertionPoint = returnIndex - 1;
        int index = Collections.binarySearch(sorted, goingToInserted, (o1, o2) -> {
            int res = o1.getKey().compareToIgnoreCase(o2.getKey());
            return res;
        });
        if (index < 0) {
            int insertIndex = ((index * -1) - 1);
            if (insertIndex >= 0 && insertIndex < sorted.size()) {
                sorted.add(insertIndex, goingToInserted);
                System.out.println("Insert Index: " + insertIndex);
                sorted.forEach(sItem -> System.out.println(sItem.getKey()));
                System.out.println("=====================================");
            }else{
                sorted.add(goingToInserted);
                System.out.println("Insert Index: " + insertIndex);
                sorted.forEach(sItem -> System.out.println(sItem.getKey()));
                System.out.println("=====================================");
            }
        }else{
            //The New Item's compare value is already exist at this index.
            //So just insert at this index, because we support duplicate insert.
            sorted.add(index, goingToInserted);
            System.out.println("Insert Index: " + index);
            sorted.forEach(sItem -> System.out.println(sItem.getKey()));
            System.out.println("=====================================");
        }

    }

    @Test
    public void binarySearchIntoAList(){

        List<UserInfo> infoList = new ArrayList<>();
        UserInfo info = new UserInfo();
        info.setEmail("demo-2@gmail.com");
        infoList.add(info);
        info = new UserInfo();
        info.setEmail("aeto@gmail.com");
        infoList.add(info);
        info = new UserInfo();
        info.setEmail("bemo-1@gmail.com");
        infoList.add(info);
        info = new UserInfo();
        info.setEmail("zremo-1@gmail.com");
        infoList.add(info);
        info = new UserInfo();
        info.setEmail("eemo-1@gmail.com");
        infoList.add(info);

        //Sort in Action
        UserInfo[] items = infoList.toArray(new UserInfo[0]);
        Arrays.sort(items, (o1, o2) ->
                o1.getEmail().compareToIgnoreCase(o2.getEmail())
        );
        List<UserInfo> sorted = Arrays.asList(items);
        //Search in Action
        UserInfo searchFor = new UserInfo();
        searchFor.setEmail("bemo-1@gmail.com");
        //
        Integer index = Collections.binarySearch(sorted, searchFor, (o1, o2) -> o1.getEmail().compareToIgnoreCase(o2.getEmail()));
        UserInfo searched = null;
        if (index > -1) searched = sorted.get(index);

        Assert.assertTrue(searchFor.getEmail().equalsIgnoreCase(searched.getEmail()));
    }

    @Test
    public void binarySearchIntoAList2(){

        List<UserInfo> infoList = new ArrayList<>();
        UserInfo info = new UserInfo();
        info.setEmail("demo-2@gmail.com");
        infoList.add(info);
        info = new UserInfo();
        info.setEmail("aeto@gmail.com");
        infoList.add(info);
        info = new UserInfo();
        info.setEmail("bemo-1@gmail.com");
        infoList.add(info);
        info = new UserInfo();
        info.setEmail("zremo-1@gmail.com");
        infoList.add(info);
        info = new UserInfo();
        info.setEmail("eemo-1@gmail.com");
        infoList.add(info);

        UserInfoList userInfoList = new UserInfoList();
        userInfoList.setUsers(infoList);

        //Sorting in Action
        userInfoList.sort(SortOrder.ASC, "username");
        List<UserInfo> sorted = userInfoList.getUsers();
        sorted.forEach(emailInfo -> System.out.println(emailInfo.getUsername()));
        //Search in Action
        UserInfo searchFor = new UserInfo();
        searchFor.setEmail("bemo-1@gmail.com");
        //
        Integer index = Collections.binarySearch(sorted, searchFor, (o1, o2) -> o1.getEmail().compareToIgnoreCase(o2.getEmail()));
        UserInfo searched = null;
        if (index > -1) {
            searched = sorted.get(index);
            //Assert
            Assert.assertTrue("Email did found" ,searchFor.getEmail().equalsIgnoreCase(searched.getEmail()));
        }else {
            Assert.assertTrue("Search didn't get anything", false);
        }

    }

    public static class UserInfo extends Response{
        private String email;
        private String username;

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    public static class UserInfoList extends ResponseList{
        public void setUsers(List<UserInfo> list){
            setCollections(list);
        }

        public List<UserInfo> getUsers(){
            return getCollections();
        }
    }

}
