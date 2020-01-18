package cz.garcejan;

import java.util.Map;

import static java.lang.System.exit;
import static java.lang.System.setOut;

public class Main {

    private static void loginTests () throws Exception {

        System.out.println("--------------LOGIN TESTS STARTED--------------");

        // successful login
        TMobileService test0 = new TMobileService();
        if ( ! test0.login("1105092013", "tmcz-97Ahp93k").equals( TMobileServerResponse.LOGIN_SUCCESSFUL)){
            System.out.println("test0 failed");
            throw new Exception();
        }

        // account is locked
        TMobileService test1 = new TMobileService();
        if ( ! test1.login("aaaa", "aaaa").equals( TMobileServerResponse.LOCKED_ACCOUNT)){
            System.out.println("test1 failed");
            throw new Exception();
        }

        // invalid username or password
        TMobileService test2 = new TMobileService();
        if ( ! test2.login("bbbb", "aaaa").equals( TMobileServerResponse.INVALID_USERNAME_OR_PASSWORD)) {
            System.out.println("test2 failed");
            throw new Exception();
        }

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
    private static void myTariffTests() throws Exception {
        System.out.println("--------------MY TARIFF TESTS STARTED--------------");

        // test0
        try{
            TMobileService test0 = new TMobileService();
            if ( ! test0.login("1105092013", "tmcz-97Ahp93k").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
                System.out.println("test0 failed");

            TMobileMyTariff tMobileMyTariff = test0.getMyTariff("");
            System.out.println("============================================================================================================");
            System.out.println(tMobileMyTariff.getMyTariff());

            for (Map.Entry entry : tMobileMyTariff.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");

            TMobileMyTariff tMobileMyTariff1 = test0.getMyTariff(" ");
            System.out.println("============================================================================================================");
            System.out.println(tMobileMyTariff1.getMyTariff());

            for (Map.Entry entry : tMobileMyTariff1.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");


        }
        catch (Exception e){
            e.printStackTrace();
            throw new Exception("MyTariff test0 failed");
        }

        // test1
        try{
            TMobileService test1 = new TMobileService();
            if ( ! test1.login("1105092013", "tmcz-97Ahp93k").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
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
            if ( ! test2.login("1105092013", "tmcz-97Ahp93k").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
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
            throw new Exception("MyTariff test2 failed");
        }

        System.out.println("--------------MY TARIFF TESTS FINISHED--------------");
    }

    @SuppressWarnings("Duplicates")
    private static void serviceSettingsTests() throws Exception {
        System.out.println("--------------SERVICE SETTINGS TESTS STARTED--------------");

        // test0
        try{
            TMobileService test0 = new TMobileService();
            if ( ! test0.login("1105092013", "tmcz-97Ahp93k").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
                System.out.println("test0 failed");

            TMobileServiceSettings tMobileServiceSettings = test0.getServiceSettings(" ");
            System.out.println("============================================================================================================");

            for (Map.Entry entry : tMobileServiceSettings.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");
        }
        catch (Exception e){
            e.printStackTrace();
            throw new Exception("ServiceSettings test0 failed");
        }

        // test1
        try{
            TMobileService test1 = new TMobileService();
            if ( ! test1.login("1105092013", "Strancice54").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
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
            if ( ! test2.login("1105092013", "tmcz-97Ahp93k").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
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
            throw new Exception("Service settings test0 failed");
        }

        System.out.println("--------------SERVICE SETTINGS TESTS FINISHED--------------");
    }

    @SuppressWarnings("Duplicates")
    private static void activationTests() {

        // test0
        try{
            TMobileService test0 = new TMobileService();
            if ( ! test0.login("1105092013", "tmcz-97Ahp93k").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
                System.out.println("test0 failed");

            if (test0.resetDataRoamingLimit("") != TMobileServerResponse.ACTIVATION_SUCCESSFUL){
                exit(5);
            }
            System.out.println("============================================================================================================");
            TMobileServerResponse response = test0.holidaySuspending("", "18.1.2020", "18.1.2020");
            if ( response != TMobileServerResponse.ACTIVATION_SUCCESSFUL){
                System.out.println(response);
                exit(1);
            }
            System.out.println("============================================================================================================");
            if (test0.resetDataRoamingLimit("") != TMobileServerResponse.ACTIVATION_SUCCESSFUL){
                exit(2);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Reset data roaming limit test0 failed");
        }

    }

    @SuppressWarnings("Duplicates")
    private static void tests() {
        // test0
        try{
            TMobileService test0 = new TMobileService();
            if ( ! test0.login("1105092013", "tmcz-97Ahp93k").equals( TMobileServerResponse.LOGIN_SUCCESSFUL))
                System.out.println("test0 failed");

            TMobileMyTariff tMobileMyTariff = test0.getMyTariff(" ");
            System.out.println("============================================================================================================");
            System.out.println(tMobileMyTariff.getMyTariff());

            for (Map.Entry entry : tMobileMyTariff.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");

            TMobileServiceSettings tMobileServiceSettings = test0.getServiceSettings(" ");
            System.out.println("============================================================================================================");

            for (Map.Entry entry : tMobileServiceSettings.getData().entrySet()) {
                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            System.out.println("============================================================================================================");

            TMobileMyTariff tMobileMyTariff1 = test0.getMyTariff(" ");
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
        try {
            //loginTests();
            //myTariffTests();
            serviceSettingsTests();
            //activationTests();
            //tests();

        } catch (Exception e) {
            System.out.println(e);
            exit(1);
        }
    }
}
