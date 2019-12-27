package cz.garcejan;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import cz.garcejan.TMobileExceptions.InvalidTableException;
import cz.garcejan.TMobileExceptions.LogoutException;
import cz.garcejan.TMobileExceptions.UnknownNumberException;

import java.io.*;
import java.net.*;
import java.util.List;

public class TMobileService implements TMobileServiceInterface {

    private final static String url = "https://www.t-mobile.cz/podnikatele-firmy/muj-t-mobile";

    private HtmlPage mainPage;
    private HtmlPage overviewPage; //overview page of the phone number you choose

    private boolean isFirst = true; //check if public methods have already been used or not

    /*
      |=================================================================================================================
      | RESET DATA ROAMING LIMIT METHODS
      |=================================================================================================================
     */

    /**
     * This method connects to your T-Mobile account.
     *
     * @param username = your T-Mobile username
     * @param password = password of your account
     * @return server response - enum
     */
    public TMobileServerResponse login(String username, String password) {

        try {
            // web client to default page

            final WebClient webClient = new WebClient();
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            final HtmlPage defaultPage = webClient.getPage(url);
            //https://stackoverflow.com/questions/11249317/error-while-using-htmlunit

            // this form hasn't got id, neither name, so it has to be chosen by brute force
            // it's 3rd on this page
            List<HtmlForm> forms = defaultPage.getForms();
            final HtmlForm loginForm = forms.get(2);

            // getting inputs from form
            final HtmlTextInput textFieldUsername = loginForm.getInputByName("username");
            final HtmlPasswordInput textFieldPassword = loginForm.getInputByName("password");
            final HtmlCheckBoxInput checkBox = loginForm.getInputByName("remember");
            final HtmlSubmitInput submitButton = loginForm.getInputByName("submit");


            // filling the sign up form and log in
            checkBox.setAttribute("checked", "checked");
            textFieldPassword.type(password);
            textFieldUsername.type(username);
            mainPage = submitButton.click();

        } catch (MalformedURLException me) {
            System.out.println("INVALID_SERVER_URL");
            System.out.println("Exception: " + me);
            return TMobileServerResponse.INVALID_SERVER_URL;
        } catch (IOException ioe) {
            System.out.println("SERVER_CONNECTION_FAILED");
            System.out.println("Exception: " + ioe);
            return TMobileServerResponse.SERVER_CONNECTION_FAILED;
        } catch (Exception e) {
            System.out.println("Ooops, something went wrong");
            System.out.println("Exception: " + e);
            return TMobileServerResponse.INVALID_SERVER_URL;
        }

        TMobileServerResponse response = getResponse();
        return response;
    }


    //TODO logout

    /**
     * You have to use login(String username, String password ) first to sign up.
     * Doesn't work, if you used getMyTariff or getServiceSettings before with different phoneNumber. (T-Mobile server problem)
     *
     * @param phoneNumber
     * @return TMobileMyTariff that includes pair<header, tariff name> and map<category name, list<TMobileData>> from Můj tarif page
     * @throws InvalidTableException  when unknown table is found
     * @throws IOException
     * @throws LogoutException        when you aren't signed in.
     * @throws UnknownNumberException when number wasn't found (you are not owner of this phone number)
     */
    public TMobileMyTariff getMyTariff(String phoneNumber) throws InvalidTableException, IOException, LogoutException, UnknownNumberException {

        System.out.println("My tariff");
        TMobileMyTariff tMobileMyTariff = new TMobileMyTariff();

        HtmlPage myTariffPage = travelToPage(phoneNumber, "Můj tarif", 3);

        List<HtmlTable> tables = myTariffPage.getByXPath("/html/body//table");
        tMobileMyTariff.saveDataFromTables(tables);

        return tMobileMyTariff;
    }

    /**
     * You have to use login(String username, String password ) first to sign up.
     * Doesn't work, if you used getMyTariff or getServiceSettings before with different phoneNumber. (T-Mobile server problem)
     *
     * @param phoneNumber
     * @return TMobileServiceSettings that includes map<category name, list<TMobileData>> from Nastaveni sluzeb page
     * @throws InvalidTableException  when unknown table is found
     * @throws IOException
     * @throws LogoutException        when you aren't signed in.
     * @throws UnknownNumberException when number wasn't found (you are not owner of this phone number)
     */
    public TMobileServiceSettings getServiceSettings(String phoneNumber) throws InvalidTableException, IOException, LogoutException, UnknownNumberException {

        System.out.println("Service settings");
        TMobileServiceSettings serviceSettings = new TMobileServiceSettings();

        HtmlPage serviceSettingsPage = travelToPage(phoneNumber, "Nastavení služeb", 5);

        List<HtmlTable> tables = serviceSettingsPage.getByXPath("/html/body//table");
        serviceSettings.saveDataFromTables(tables);

        return serviceSettings;
    }

    //TODO test
    @Override
    public boolean resetDataRoamingLimit(String phoneNumber) throws UnknownNumberException, IOException, LogoutException {
        System.out.println("Reset data roaming limit");

        HtmlPage foreignPage = travelToPage(phoneNumber, "Zahraničí", 10);
        List<HtmlTable> tables = foreignPage.getByXPath("/html/body//table");

        for (HtmlTable table : tables) {
            if (table.getCaptionText().equals("Služby v roamingu")) {
                System.out.println("Table " + table.getCaptionText() + " found!");
                boolean flag = serviceActivation(table, "Data Roaming Limit - Reset");
                return flag;
            }
        }
        return false;
    }

    //TODO test + predelat navratovej typ
    @Override
    public boolean holidaySuspending(String phoneNumber) throws UnknownNumberException, IOException, LogoutException {
        System.out.println("Holiday suspending");

        HtmlPage serviceSettingsPage = travelToPage(phoneNumber, "Nastavení služeb", 10);

        //TODO

        List<HtmlTable> tables = serviceSettingsPage.getByXPath("/html/body//table");

        for (HtmlTable table : tables) {
            if (table.getCaptionText().equals("Pozastavení služeb")) {
                System.out.println("Table " + table.getCaptionText() + " found!");
                boolean flag = serviceActivation(table, "Suspendace - Prázdniny");
                return flag;
            }
        }
        return false;
    }

    /*
      |=================================================================================================================
      | ACTIVATION METHODS
      |=================================================================================================================
     */

    /**
     * Submethod to methods, whose task is activation of a service
     *
     * @param table       = table of service location
     * @param serviceName = name of the service
     * @throws IOException
     */
    private boolean serviceActivation(HtmlTable table, String serviceName) throws IOException {

        for (HtmlTableRow tableRow : table.getRows()) {
            System.out.println("tableRow: " + tableRow.asText());
            if (tableRow.getCells().size() != 5)
                continue;

            if (tableRow.getCell(0).asText().equals(serviceName)) {
                System.out.println(tableRow.getCell(0).asText() + " - " + tableRow.getElementsByTagName("a").get(0).asText());
                tableRow.getElementsByTagName("a").get(0).click();
                System.out.println("Activation successful");
                return true;
            }
        }
        return false;
    }

    /**
     * Submethod to resetDataRoamingLimit
     *
     * @param htmlTable
     * @throws IOException
     */
    private void resetDataRoamingLimitActivation(HtmlTable htmlTable) throws IOException {

        for (HtmlTableRow tableRow : htmlTable.getRows()) {
            System.out.println("tableRow: " + tableRow.asText());
            if (tableRow.getCells().size() != 5)
                continue;

            if (tableRow.getCell(0).asText().equals("Data Roaming Limit - Reset")) {
                System.out.println("Data Roaming Limit - Reset " + tableRow.getElementsByTagName("a").get(0).asText());
                tableRow.getElementsByTagName("a").get(0).click();
                System.out.println("Reset data roaming limit success");
                return;
            }
        }
    }

     /*
      |=================================================================================================================
      | LOGIN METHODS
      |=================================================================================================================
     */

    /**
     * Easy submethod of method login.
     *
     * @return server response if connection succeeded or not.
     */
    private TMobileServerResponse getResponse() {
        if (mainPage.asText().contains("Kód chyby: G 706")) {
            System.out.println("INVALID_USERNAME_OR_PASSWORD");
            return TMobileServerResponse.INVALID_USERNAME_OR_PASSWORD;
        }

        if (mainPage.asText().contains("Kód chyby: G 701")) {
            System.out.println("LOCKED_ACCOUNT");
            return TMobileServerResponse.LOCKED_ACCOUNT;
        }

        if (mainPage.asText().contains("Vítejte v Můj T-Mobile") || mainPage.asText().contains("Bleskový přehled")) {
            System.out.println("LOGIN_SUCCESSFUL");
            return TMobileServerResponse.LOGIN_SUCCESSFUL;
        }

        System.out.println("SERVER_CONNECTION_FAILED - Unknown failure");
        return TMobileServerResponse.SERVER_CONNECTION_FAILED;
    }

     /*
      |=================================================================================================================
      | NAVIGATION TO REQUIRED PAGE
      |=================================================================================================================
     */

    /**
     * This method gets you to @param name page of number @param phoneNumber
     *
     * @param phoneNumber
     * @param name        = name of the page we want to travel
     * @param seconds     = sleep time for loading
     * @return page depending on name
     * @throws IOException
     * @throws LogoutException
     * @throws UnknownNumberException
     */
    private HtmlPage travelToPage(String phoneNumber, String name, long seconds) throws IOException, LogoutException, UnknownNumberException {
        overviewPage = mainPage;
        /*
        if ( isFirst ){
            overviewPage = overviewPage (phoneNumber);
            isFirst = false;
        }
        */

        overviewPage = openSideNav(overviewPage);
        System.out.println("sidenav: " + overviewPage.asText());

        HtmlPage page = nextPage(overviewPage, name);

        sleep(seconds);

        System.out.println("Final page: " + page.asText());

        return page;
    }

    /**
     * This method transfers your method from Muj T-Mobile main page to Bleskovy prehled of @param phoneNumber
     *
     * @return returns HtmlPage of Bleskovy prehled phoneNumber
     * @throws IOException
     */
    private HtmlPage overviewPage(String phoneNumber) throws IOException, LogoutException, UnknownNumberException {

        if (!mainPage.asText().contains("Vítejte v Můj T-Mobile"))
            throw new LogoutException("You have to log in first");

        //finding text inputs with this xpath in whole site (2x) and take 2nd
        final HtmlTextInput textInput = (HtmlTextInput) mainPage.getByXPath("/html/body//form//input[@class='input-clear' and @type='text']").get(1);
        final HtmlSubmitInput findButton = mainPage.getFirstByXPath("/html/body//form//input[@type='submit' and @value='Hledat']"); //phoneNumberForm.getInputByValue("Hledat");

        textInput.type(phoneNumber);
        HtmlPage tmpPage = findButton.click();
        sleep(6);


        System.out.println("tmpPage: " + tmpPage.asText());

        return findPhoneNumberPage(tmpPage, phoneNumber);
    }

    /**
     * This method takes you to the final common page of overview page.
     *
     * @param page
     * @param phoneNumber
     * @return overview page common for all public methods
     * @throws IOException
     * @throws UnknownNumberException when phone number isn't found in list of your numbers
     */
    private HtmlPage findPhoneNumberPage(HtmlPage page, String phoneNumber) throws IOException, UnknownNumberException {
        List<HtmlTable> tables = page.getByXPath("/html/body//table");
        for (HtmlTable table : tables) {
            System.out.println(table.asText());

            for (HtmlTableRow tableRow : table.getRows()) {
                if (tableRow.getCell(0).asText().equals(phoneNumber)) {
                    page = tableRow.getElementsByTagName("a").get(0).click();
                    sleep(2);
                    return page;
                }
            }
        }
        System.out.println("Unknown number");
        throw new UnknownNumberException("UNKNOWN NUMBER");

    }

    /**
     * Open side-nav bar, where Nastavení služeb & Můj tarif are
     *
     * @param page
     * @return
     * @throws IOException
     */
    private HtmlPage openSideNav(HtmlPage page) throws IOException {
        DomNodeList<HtmlElement> tmpElements = page.getElementById("side-nav").getElementsByTagName("a");
        for (HtmlElement element : tmpElements) {
            if (element.asText().equals("Služby")) {
                element.click();
                System.out.println("SIDE-NAV SUCCESS");
                break;
            }
        }
        return page;
    }

    /**
     * This method transfers your method from Bleskovy prehled (overviewPage) to final page of your choice
     *
     * @param overviewPage = Bleskovy prehled page
     * @param name         = name of destination
     * @return final @param name's page
     * @throws IOException
     */
    private HtmlPage nextPage(HtmlPage overviewPage, String name) throws IOException {
        List<HtmlAnchor> anchors = overviewPage.getAnchors();

        HtmlAnchor link = null;
        for (HtmlAnchor anchor : anchors) {
            if (anchor.asText().equals(name)) {
                link = anchor;
                System.out.println("Anchor found! " + anchor.asText());
                break;
            }
        }
        return link.click();
    }

    /*
      |=================================================================================================================
      | SLEEP
      |=================================================================================================================
     */
    private void sleep(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
        }
    }
}
