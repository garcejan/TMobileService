package cz.garcejan;

import java.util.Map;

public class Main {

    private static void loginTests (){

        System.out.println("--------------LOGIN TESTS STARTED--------------");

        // successful login
        TMobileService test0 = new TMobileService();
        if ( ! test0.login("igar007", "Strancice54").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
            System.out.println("test0 failed");

        // account is locked
        TMobileService test1 = new TMobileService();
        if ( ! test1.login("aaaa", "aaaa").equals( TMobileServerResponse.LOCKED_ACCOUNT))
            System.out.println("test1 failed");

        // invalid username or password
        TMobileService test2 = new TMobileService();
        if ( ! test2.login("bbbb", "aaaa").equals( TMobileServerResponse.INVALID_USERNAME_OR_PASSWORD))
            System.out.println("test2 failed");

/*
        // without internet connection
        TMobileService test3 = new TMobileService();
        if ( ! test3.login("aaa", "aaaa").equals( TMobileServerResponse.SERVER_CONNECTION_FAILED))
            System.out.println("test3 failed");
*/
/*
        // url is invalid
        TMobileService test4 = new TMobileService();
        if ( ! test4.login("ccc", "aaaa").equals( TMobileServerResponse.INVALID_SERVER_URL) )
            System.out.println("test4 failed");
*/
        System.out.println("--------------LOGIN TESTS FINISHED--------------");

    }

    @SuppressWarnings("Duplicates")
    private static void myTariffTests() {
        System.out.println("--------------MY TARIFF TESTS STARTED--------------");

        // test0
        try{
            TMobileService test0 = new TMobileService();
            if ( ! test0.login("igar007", "Strancice54").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
                System.out.println("test0 failed");

            TMobileMyTariff tMobileMyTariff = test0.getMyTariff("720205492");
            System.out.println("============================================================================================================");
            System.out.println(tMobileMyTariff.getMyTariff());

            for (Map.Entry entry : tMobileMyTariff.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");

            TMobileMyTariff tMobileMyTariff1 = test0.getMyTariff("720205492");
            System.out.println("============================================================================================================");
            System.out.println(tMobileMyTariff1.getMyTariff());

            for (Map.Entry entry : tMobileMyTariff1.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");


        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("MyTariff test0 failed");
        }

        // test1
        try{
            TMobileService test1 = new TMobileService();
            if ( ! test1.login("igar007", "Strancice54").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
                System.out.println("test1 failed");

            TMobileMyTariff tMobileMyTariff = test1.getMyTariff("twehagh");
            System.out.println("============================================================================================================");
            System.out.println(tMobileMyTariff.getMyTariff());

            for (Map.Entry entry : tMobileMyTariff.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");


        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("MyTariff test1 success");
        }

        // test2
        try{
            TMobileService test2 = new TMobileService();
            if ( ! test2.login("igar007", "Strancice54").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
                System.out.println("test2 failed");

            TMobileMyTariff  tMobileMyTariff = test2.getMyTariff("739316504");
            System.out.println("============================================================================================================");
            System.out.println(tMobileMyTariff.getMyTariff());
            System.out.println("============================================================================================================");

            for (Map.Entry entry : tMobileMyTariff.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");

        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("MyTariff test2 failed");
        }

        System.out.println("--------------MY TARIFF TESTS FINISHED--------------");
    }

    @SuppressWarnings("Duplicates")
    private static void serviceSettingsTests() {
        System.out.println("--------------SERVICE SETTINGS TESTS STARTED--------------");

        // test0
        try{
            TMobileService test0 = new TMobileService();
            if ( ! test0.login("igar007", "Strancice54").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
                System.out.println("test0 failed");

            TMobileServiceSettings tMobileServiceSettings = test0.getServiceSettings("720205492");
            System.out.println("============================================================================================================");

            for (Map.Entry entry : tMobileServiceSettings.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");
/*


            TMobileServiceSettings tMobileServiceSettings1 = test0.getServiceSettings("720205492");
            System.out.println("============================================================================================================");
            for (Map.Entry entry : tMobileServiceSettings1.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");
*/

        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Service settings test0 failed");
        }

/*

        // test1
        try{
            TMobileService test1 = new TMobileService();
            if ( ! test1.login("igar007", "Strancice54").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
                System.out.println("test1 failed");

            TMobileServiceSettings tMobileServiceSettings = test1.getServiceSettings("twehagh");
            System.out.println("============================================================================================================");
            for (Map.Entry entry : tMobileServiceSettings.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");


        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Service settings test1 success");
        }

        // test2
        try{
            TMobileService test2 = new TMobileService();
            if ( ! test2.login("igar007", "Strancice54").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
                System.out.println("test2 failed");

            TMobileServiceSettings tMobileServiceSettings = test2.getServiceSettings("739316504");
            System.out.println("============================================================================================================");

            for (Map.Entry entry : tMobileServiceSettings.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");

        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Service settings test2 failed");
        }
*/




        System.out.println("--------------SERVICE SETTINGS TESTS FINISHED--------------");
    }

    @SuppressWarnings("Duplicates")
    private static void tests() {
        // test0
        try{
            TMobileService test0 = new TMobileService();
            if ( ! test0.login("igar007", "Strancice54").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
                System.out.println("test0 failed");

            TMobileMyTariff tMobileMyTariff = test0.getMyTariff("720205492");
            System.out.println("============================================================================================================");
            System.out.println(tMobileMyTariff.getMyTariff());

            for (Map.Entry entry : tMobileMyTariff.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");

            TMobileServiceSettings tMobileServiceSettings = test0.getServiceSettings("720205492");
            System.out.println("============================================================================================================");

            for (Map.Entry entry : tMobileServiceSettings.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");

            TMobileMyTariff tMobileMyTariff1 = test0.getMyTariff("720205492");
            System.out.println("============================================================================================================");
            System.out.println(tMobileMyTariff.getMyTariff());

            for (Map.Entry entry : tMobileMyTariff1.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");

        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("MyTariff test0 failed");
        }


    }

    public static void main(String[] args) {
            //loginTests();
            //myTariffTests();
            //serviceSettingsTests();
            tests();
    }
}
