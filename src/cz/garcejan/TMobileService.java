package cz.garcejan;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import cz.garcejan.TMobileExceptions.InvalidTableException;
import cz.garcejan.TMobileExceptions.LogoutException;
import cz.garcejan.TMobileExceptions.ServiceAlreadyActivatedException;
import cz.garcejan.TMobileExceptions.UnknownNumberException;

import java.io.*;
import java.net.*;
import java.util.List;

public class TMobileService implements TMobileServiceInterface {

    private final static String url = "https://www.t-mobile.cz/podnikatele-firmy/muj-t-mobile";

    private HtmlPage mainPage;
    private HtmlPage overviewPage; //overview page of the phone number you choose

    // uncomment this, if account owns more phoneNumbers
    // private boolean isFirst = true; //check if public methods have already been used or not, only for overviewPage

    /*
      |=================================================================================================================
      | LOGIN
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

        TMobileServerResponse response = getServerResponse();
        return response;
    }

    /**
     * Easy submethod of method login.
     *
     * @return server response if connection succeeded or not.
     */
    private TMobileServerResponse getServerResponse() {
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
      | LOGIN END
      |=================================================================================================================
     */

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
    public TMobileMyTariff getMyTariff(String phoneNumber) throws InvalidTableException, IOException, LogoutException, UnknownNumberException, NullPointerException {

        System.out.println("My tariff");
        TMobileMyTariff tMobileMyTariff = new TMobileMyTariff();

        HtmlPage myTariffPage = getPageByName(phoneNumber, "Můj tarif", 3);

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
    public TMobileServiceSettings getServiceSettings(String phoneNumber) throws InvalidTableException, IOException, LogoutException, UnknownNumberException, NullPointerException {

        System.out.println("Service settings");
        TMobileServiceSettings serviceSettings = new TMobileServiceSettings();

        HtmlPage serviceSettingsPage = getPageByName(phoneNumber, "Nastavení služeb", 5);

        List<HtmlTable> tables = serviceSettingsPage.getByXPath("/html/body//table");
        serviceSettings.saveDataFromTables(tables);

        return serviceSettings;
    }


    /*
      |=================================================================================================================
      | ACTIVATION METHODS
      |=================================================================================================================
     */

    /**
     * Info in TMobileServiceInterface
     */
    public TMobileServerResponse resetDataRoamingLimit(String phoneNumber) throws UnknownNumberException, IOException, LogoutException, NullPointerException{
        System.out.println("Reset data roaming limit");

        HtmlPage foreignPage = getPageByName(phoneNumber, "Zahraničí", 10);
        List<HtmlTable> tables = foreignPage.getByXPath("/html/body//table");

        for (HtmlTable table : tables) {

            if (table.getCaptionText().equals("Služby v roamingu")) {
                System.out.println("Table " + table.getCaptionText() + " found!");


                HtmlPage page = null;
                try {
                    page = serviceActivation(table, "Data Roaming Limit - Reset");
                } catch (ServiceAlreadyActivatedException e) {
                    return TMobileServerResponse.SERVICE_ALREADY_ACTIVATED;
                }

                if ( page != null ){
                    System.out.println("Restart data roaming limit successful");
                    return TMobileServerResponse.TASK_SUCCESSFUL;
                }
                else throw new NullPointerException();
            }
        }

        return TMobileServerResponse.ACTIVATION_FAILED;
    }

    /**
     * Info in TMobileServiceInterface
     */
    public TMobileServerResponse holidaySuspending(String phoneNumber, String from, String to) throws UnknownNumberException, IOException, LogoutException, NullPointerException{
        System.out.println("Holiday suspending");

        HtmlPage serviceSettingsPage = getPageByName(phoneNumber, "Nastavení služeb", 10);
        List<HtmlTable> tables = serviceSettingsPage.getByXPath("/html/body//table");

        System.out.println(serviceSettingsPage.asText());
        for (HtmlTable table : tables) {

            if (table.getCaptionText().equals("Pozastavení služeb")) {
                System.out.println("Table " + table.getCaptionText() + " found!");

                HtmlPage confirmationPage = null;

                try {
                    confirmationPage = serviceActivation(table, "Suspendace - Prázdniny");
                } catch (ServiceAlreadyActivatedException e){
                    return TMobileServerResponse.SERVICE_ALREADY_ACTIVATED;
                }

                if ( confirmationPage != null ){
                    TMobileServerResponse confirmResponse = confirmHolidaySuspending(confirmationPage, from, to);
                    return confirmResponse;
                }
                else throw new NullPointerException();

            }
        }

        return TMobileServerResponse.ACTIVATION_FAILED;
    }

    public TMobileServerResponse holidaySuspending(String phoneNumber, String to) throws UnknownNumberException, IOException, LogoutException, NullPointerException {
        return holidaySuspending(phoneNumber, "", to);
    }

    public TMobileServerResponse holidaySuspending(String phoneNumber) throws UnknownNumberException, IOException, LogoutException, NullPointerException {
        return holidaySuspending(phoneNumber, "", "");
    }


    /**
     * Info in TMobileServiceInterface
     */
    public TMobileServerResponse deactivateHolidaySuspending(String phoneNumber) throws UnknownNumberException, IOException, LogoutException, NullPointerException {
        System.out.println("Deactivate holiday suspending");

        HtmlPage serviceSettingsPage = getPageByName(phoneNumber, "Nastavení služeb", 10);
        List<HtmlTable> tables = serviceSettingsPage.getByXPath("/html/body//table");

        for (HtmlTable table : tables) {
            if (table.getCaptionText().equals("Pozastavení služeb")) {
                System.out.println("Table " + table.getCaptionText() + " found!");

                HtmlPage confirmationPage = null;
                try {
                    confirmationPage = serviceDeactivation(table, "Suspendace - Prázdniny");
                } catch (ServiceAlreadyActivatedException e) {
                    return TMobileServerResponse.SERVICE_ALREADY_DEACTIVATED;
                }

                if ( confirmationPage != null ){
                    TMobileServerResponse confirmResponse = confirmOrder( confirmationPage );
                    return confirmResponse;
                }
                else throw new NullPointerException();

            }
        }

        return TMobileServerResponse.DEACTIVATION_FAILED;
    }

    /**
     * Submethod for holidaySuspending. Fill in dates from/to for suspending.
     * @param confirmationPage
     * @param from: MUST be in format dd.MM.yyyy and >= today's date
     * @param to: MUST be in format dd.MM.yyyy and >= @param from
     * @return TMobileServerResponse
     * @throws IOException
     */
    private TMobileServerResponse confirmHolidaySuspending(HtmlPage confirmationPage, String from, String to) throws IOException {

        List<HtmlForm> forms = confirmationPage.getForms();
        final HtmlForm confirmationForm = forms.get(2); // 3rd on the pag
        final HtmlTextInput fromTextBox = confirmationForm.getInputByName("_corevcc_WAR_vcc_1");
        final HtmlTextInput toTextBox = confirmationForm.getInputByName("_corevcc_WAR_vcc_2");
        final HtmlSubmitInput continueButton = confirmationForm.getInputByName("_corevcc_WAR_vcc_3.9.1");

        if ( from.matches("[0-3]?[0-9]\\.[0-1]?[0-9]\\.20[2-9][0-9]") )
            fromTextBox.setDefaultValue(from);

        if ( to.matches("[0-3]?[0-9]\\.[0-1]?[0-9]\\.20[2-9][0-9]"))
            toTextBox.setDefaultValue(to);

        HtmlPage orderOverviewPage = continueButton.click();

        sleep(2);

        TMobileServerResponse response = confirmOrder( orderOverviewPage );
        return response;
    }



    /**
     * Method for confirmation order form.
     * @param orderOverviewPage
     * @return TMobileServerResponse
     * @throws IOException
     */
    private TMobileServerResponse confirmOrder(HtmlPage orderOverviewPage) throws IOException {

        if ( ! orderOverviewPage.asText().contains("Přehled objednávky"))
            return TMobileServerResponse.TASK_FAILED_WRONG_SUBMIT_PAGE;

        List<HtmlForm> orderOverviewForms = orderOverviewPage.getForms();
        final HtmlForm orderOverviewForm = orderOverviewForms.get(2); // 3rd on the page
        final HtmlSubmitInput submitButton = orderOverviewForm .getInputByName("_corevcc_WAR_vcc_submit_form");

        HtmlPage lastPage = submitButton.click();

        sleep(2);

        if ( ! lastPage.asText().contains("Děkujeme. Úspěšně jsme přijali vaši objednávku."))
            return TMobileServerResponse.TASK_CONFIRMATION_FAILED;

        return TMobileServerResponse.TASK_SUCCESSFUL;
    }


    /**
     * Submethod to methods, whose task is activation of a service
     *
     * @param table       = table of service location
     * @param serviceName = name of the service
     * @throws IOException
     */
    private HtmlPage changeServiceState(HtmlTable table, String serviceName, boolean activation) throws IOException, ServiceAlreadyActivatedException {

        for (HtmlTableRow tableRow : table.getRows()) {
            //System.out.println("tableRow: " + tableRow.asText());
            if (tableRow.getCells().size() != 5)
                continue;

            if (tableRow.getCell(0).asText().equals(serviceName)) {
                System.out.println(tableRow.getCell(0).asText() + " - " + tableRow.getElementsByTagName("a").get(0).asText());

                HtmlElement button = tableRow.getElementsByTagName("a").get(0);
                if ((! button.asText().contains("Aktivovat") && activation )
                        || (! button.asText().contains("Zrušit") && ! activation ))
                    throw new ServiceAlreadyActivatedException();

                HtmlPage confirmationPage = button.click();

                sleep(5);
                return confirmationPage;
            }
        }
        return null;
    }

    private HtmlPage serviceActivation(HtmlTable table, String serviceName) throws IOException, ServiceAlreadyActivatedException {
        return changeServiceState(table, serviceName, true);
    }
    private HtmlPage serviceDeactivation(HtmlTable table, String serviceName) throws IOException, ServiceAlreadyActivatedException {
        return changeServiceState(table, serviceName, false);
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
    private HtmlPage getPageByName(String phoneNumber, String name, long seconds) throws IOException, LogoutException, UnknownNumberException, NullPointerException {
        overviewPage = mainPage;
        /*
        // uncomment this, if account owns more phoneNumbers
        if ( isFirst ){
            getOverviewPage = getOverviewPage (phoneNumber);
            isFirst = false;
        }
        */

        overviewPage = openSideNav(overviewPage);
        //System.out.println("sidenav: " + overviewPage.asText());

        HtmlPage finalPage = nextPage(overviewPage, name);

        sleep(seconds);

        System.out.println("Final page: \n" + finalPage.asText());

        return finalPage;
    }

    /**
     * Open side-nav bar
     *
     * @param page
     * @return page with open side bar
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
     * This method transfers your method from Bleskovy prehled (getOverviewPage) to final page of your choice
     *
     * @param overviewPage = Bleskovy prehled page
     * @param name         = name of destination
     * @return final @param name's page
     * @throws IOException
     */
    private HtmlPage nextPage(HtmlPage overviewPage, String name) throws IOException {
        List<HtmlAnchor> anchors = overviewPage.getAnchors();

        HtmlAnchor link = anchors.get(0);
        for (HtmlAnchor anchor : anchors) {
            if (anchor.asText().equals(name)) {
                link = anchor;
                System.out.println("Anchor found! " + anchor.asText());
                return link.click();
            }
        }
        return null;
    }

    /**
     * Because of loading pages.
     * @param seconds
     */
    private void sleep(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
        }
    }


    /*
      |=================================================================================================================
      | UNUSED FUNCTIONS
      |=================================================================================================================
     */
    /**
     * This method transfers your method from Muj T-Mobile main page to Bleskovy prehled of @param phoneNumber
     *
     * @return returns HtmlPage of Bleskovy prehled phoneNumber
     * @throws IOException
     */
    private HtmlPage getOverviewPage(String phoneNumber) throws IOException, LogoutException, UnknownNumberException {

        if (!mainPage.asText().contains("Vítejte v Můj T-Mobile"))
            throw new LogoutException("You have to log in first");

        //finding text inputs with this xpath in whole site (2x) and take 2nd
        final HtmlTextInput textInput = (HtmlTextInput) mainPage.getByXPath("/html/body//form//input[@class='input-clear' and @type='text']").get(1);
        final HtmlSubmitInput findButton = mainPage.getFirstByXPath("/html/body//form//input[@type='submit' and @value='Hledat']"); //phoneNumberForm.getInputByValue("Hledat");

        textInput.type(phoneNumber);
        HtmlPage tmpPage = findButton.click();

        sleep(6);

        HtmlPage phoneNumberPage = findPhoneNumberPage(tmpPage, phoneNumber);

        return phoneNumberPage;
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
            //System.out.println(table.asText());

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

}
