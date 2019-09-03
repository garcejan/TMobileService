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

    private boolean isFirst = true; //check if get... methods have already been used or not

    /**
     * This method connects to your T-Mobile account.
     * @param username = your T-Mobile username
     * @param password = password of your account
     * @return server response - enum
     */
    public TMobileServerResponse login(String username, String password ) {

        try {
            // web client to default page
            final WebClient webClient = new WebClient();
            final HtmlPage defaultPage = webClient.getPage(url);

            //https://stackoverflow.com/questions/11249317/error-while-using-htmlunit
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            // this form hasn't got id, neither name, so it has to be chosen by brute force
            // it's 3rd on this page
            final HtmlForm loginForm = defaultPage.getForms().get(2);

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

        }
        catch ( MalformedURLException me ){
            System.out.println("INVALID_SERVER_URL");
            System.out.println("Exception: " + me);
            return TMobileServerResponse.INVALID_SERVER_URL;
        }
        catch ( IOException ioe ) {
            System.out.println("SERVER_CONNECTION_FAILED");
            System.out.println("Exception: " + ioe);
            return TMobileServerResponse.SERVER_CONNECTION_FAILED;
        }
        catch ( Exception e ){
            System.out.println("Ooops, something went wrong");
            System.out.println("Exception: " + e );
            return TMobileServerResponse.INVALID_SERVER_URL;
        }

        return getResponse();
    }

    /**
     * You have to use login(String username, String password ) first to sign up.
     * Doesn't work, if you used getMyTariff or getServiceSettings before with different phoneNumber. (T-Mobile server problem)
     * @param phoneNumber
     * @return TMobileMyTariff that includes pair<header, tariff name> and map<category name, list<TMobileData>> from Můj tarif page
     * @throws InvalidTableException when unknown table is found
     * @throws IOException
     * @throws LogoutException when you aren't signed in.
     * @throws UnknownNumberException when number wasn't found (you are not owner of this phone number)
     */
    public TMobileMyTariff getMyTariff(String phoneNumber) throws InvalidTableException, IOException, LogoutException, UnknownNumberException {

        System.out.println("My tariff");
        TMobileMyTariff tMobileMyTariff = new TMobileMyTariff();

        HtmlPage myTariffPage = travelToPage ( phoneNumber,"Můj tarif");

        List<HtmlTable> tables = myTariffPage.getByXPath("/html/body//table");
        tMobileMyTariff.saveDataFromTables(tables);

        return tMobileMyTariff;
    }

    /**
     * You have to use login(String username, String password ) first to sign up.
     * Doesn't work, if you used getMyTariff or getServiceSettings before with different phoneNumber. (T-Mobile server problem)
     * @param phoneNumber
     * @return TMobileServiceSettings that includes map<category name, list<TMobileData>> from Nastaveni sluzeb page
     * @throws InvalidTableException when unknown table is found
     * @throws IOException
     * @throws LogoutException when you aren't signed in.
     * @throws UnknownNumberException when number wasn't found (you are not owner of this phone number)
     */
    public TMobileServiceSettings getServiceSettings(String phoneNumber) throws InvalidTableException, IOException, LogoutException, UnknownNumberException {

        System.out.println("Service settings");
        TMobileServiceSettings serviceSettings = new TMobileServiceSettings();

        HtmlPage serviceSettingsPage = travelToPage ( phoneNumber,"Nastavení služeb");

        List<HtmlTable> tables = serviceSettingsPage.getByXPath("/html/body//table");
        serviceSettings.saveDataFromTables(tables);

        return serviceSettings;
    }


    /**
     * Easy submethod of method login.
     * @return server response if connection succeeded or not.
     */
    private TMobileServerResponse getResponse() {
        if ( mainPage.asText().contains("Kód chyby: G 706") ){
            System.out.println("INVALID_USERNAME_OR_PASSWORD");
            return TMobileServerResponse.INVALID_USERNAME_OR_PASSWORD;
        }

        if ( mainPage.asText().contains("Kód chyby: G 701") ){
            System.out.println("LOCKED_ACCOUNT");
            return TMobileServerResponse.LOCKED_ACCOUNT;
        }

        if ( mainPage.asText().contains("Vítejte v Můj T-Mobile")){
            System.out.println("LOGIN_SUCCESSFUL");
            return TMobileServerResponse.LOGIN_SUCCESSFUL;
        }

        System.out.println("SERVER_CONNECTION_FAILED - Unknown failure");
        return TMobileServerResponse.SERVER_CONNECTION_FAILED;
    }

    /**
     * This method transfers your method from Muj T-Mobile main page to Bleskovy prehled of @param phoneNumber
     * @return returns HtmlPage of Bleskovy prehled phoneNumber
     * @throws IOException
     */
    private HtmlPage overviewPage(String phoneNumber) throws IOException, LogoutException, UnknownNumberException {

        if ( ! mainPage.asText().contains("Vítejte v Můj T-Mobile"))
            throw new LogoutException("You have to log in first");

/*
        final HtmlForm form = mainPage.getForms().get(3);
        HtmlTable mainTable =  (HtmlTable) form.getByXPath("//table").get(1);
        HtmlPage tmpPage = mainPage;

        DomNodeList<HtmlElement> anchors =  mainTable.getElementsByTagName("a");

        for (int i = 0; i < anchors.size(); ++i){
            // 1st = +
            // 2nd = Zobrazit služby
            // 3rd = Spravovat tuto skupinu
            if ( i % 3 == 0 ){
                tmpPage = anchors.get(i).click();
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    System.out.println("fml");
                }

                System.out.println("============================================================================================================");
                System.out.println(anchors.get(i) + " , " + anchors.get(i).asText());
                System.out.println( i + ", tmpPage: " + tmpPage.asText());
                System.out.println("============================================================================================================");


            }
        }

        List<HtmlTable> tables = tmpPage.getByXPath("/html/body//table");

        for ( HtmlTable table : tables){
            System.out.println("============================================================================================================");
            System.out.println(table.asText());
            System.out.println("============================================================================================================");
            for (HtmlTableRow tableRow : table.getRows() ){
                if ( tableRow.getCell(0).asText().equals(phoneNumber)){
                    tmpPage = tableRow.getElementsByTagName("a").get(0).click();
                    try {
                        Thread.sleep(2000);
                    }
                    catch (InterruptedException e) {
                    }

                    //open side-nav bar, where Nastavení služeb & Můj tarif are
                    DomNodeList<HtmlElement> tmpElements = tmpPage.getElementById("side-nav").getElementsByTagName("a");
                    for ( HtmlElement element : tmpElements ){
                        if ( element.asText().equals("Služby")){
                            element.click();
                            System.out.println("SIDE-NAV SUCCESS");
                            break;
                        }
                    }
                    return tmpPage;
                }
            }
        }

        System.out.println("Unknown number");
        throw new UnknownNumberException("UNKNOWN NUMBER");
*/

        //finding text inputs with this xpath in whole site (2x) and take 2nd
        final HtmlTextInput textInput = (HtmlTextInput) mainPage.getByXPath("/html/body//form//input[@class='input-clear' and @type='text']").get(1);
        final HtmlSubmitInput findButton = mainPage.getFirstByXPath("/html/body//form//input[@type='submit' and @value='Hledat']"); //phoneNumberForm.getInputByValue("Hledat");

        textInput.type(phoneNumber);
        HtmlPage tmpPage = findButton.click();
        try {
            Thread.sleep(6000);
        }
        catch (InterruptedException e) {
        }

        System.out.println("tmpPage: " + tmpPage.asText());

        return findPhoneNumberPage( tmpPage, phoneNumber);
    }

    /**
     * This method takes you to the final common page of overview page.
     * @param page
     * @param phoneNumber
     * @return overview page common for both method (getMyTariff & getServiceSettings)
     * @throws IOException
     * @throws UnknownNumberException when phone number isn't found in list of your numbers
     */
    private HtmlPage findPhoneNumberPage(HtmlPage page, String phoneNumber) throws IOException, UnknownNumberException {
        List<HtmlTable> tables = page.getByXPath("/html/body//table");
        for ( HtmlTable table : tables){
            System.out.println(table.asText());
            for (HtmlTableRow tableRow : table.getRows() ){
                if ( tableRow.getCell(0).asText().equals(phoneNumber)){
                    page = tableRow.getElementsByTagName("a").get(0).click();
                    try {
                        Thread.sleep(2000);
                    }
                    catch (InterruptedException e) {
                    }
                    return page;
                }
            }
        }
        System.out.println("Unknown number");
        throw new UnknownNumberException("UNKNOWN NUMBER");

    }

    /**
     * Open side-nav bar, where Nastavení služeb & Můj tarif are
     * @param page
     * @return
     * @throws IOException
     */
    private HtmlPage openSideNav (HtmlPage page) throws IOException {
        DomNodeList<HtmlElement> tmpElements = page.getElementById("side-nav").getElementsByTagName("a");
        for ( HtmlElement element : tmpElements ){
            if ( element.asText().equals("Služby")){
                element.click();
                System.out.println("SIDE-NAV SUCCESS");
                break;
            }
        }
        return page;
    }

    /**
     * This method transfers your method from Bleskovy prehled (overviewPage) to final page of your choice
     * @param overviewPage = Bleskovy prehled page
     * @param name = name of destination
     * @return final @param name's page
     * @throws IOException
     */
    private HtmlPage nextPage(HtmlPage overviewPage, String name) throws IOException {
        List<HtmlAnchor> anchors = overviewPage.getAnchors();

        HtmlAnchor link = null;
        for (HtmlAnchor anchor : anchors) {
            if (anchor.asText().equals(name)){
                link = anchor;
                System.out.println("Anchor found! " + anchor.asText());
                break;
            }
        }
        return link.click();
    }

    /**
     * Thanks this method methods getMyTariff & getServiceSettings get right page
     * @param phoneNumber
     * @param name = name of the page
     * @return page depending on name
     * @throws IOException
     * @throws LogoutException
     * @throws UnknownNumberException
     */
    private HtmlPage travelToPage(String phoneNumber, String name ) throws IOException, LogoutException, UnknownNumberException {

        if ( isFirst ){
            overviewPage = overviewPage (phoneNumber);
            isFirst = false;
        }

        overviewPage = openSideNav(overviewPage);
        System.out.println("sidenav: " + overviewPage.asText());

        HtmlPage page = nextPage(overviewPage, name);
        try {
            Thread.sleep(1000);
        }
        catch (Exception e){
        }
        System.out.println("Final page: " + page.asText());

        return page;
    }
}
