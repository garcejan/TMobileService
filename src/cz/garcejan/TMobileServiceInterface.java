package cz.garcejan;

import cz.garcejan.TMobileExceptions.InvalidTableException;
import cz.garcejan.TMobileExceptions.LogoutException;
import cz.garcejan.TMobileExceptions.UnknownNumberException;

import java.io.IOException;

public interface TMobileServiceInterface {

    /**
     * This method connects to your T-Mobile account.
     * @param username = your T-Mobile username
     * @param password = password of your account
     * @return server response - enum
     */
    public TMobileServerResponse login(String username, String password );

    /**
     * You have to use login(String username, String password ) first to sign up.
     * Doesn't work, if you used getMyTariff or getServiceSettings before with different phoneNumber. (T-Mobile server problem)
     * @param phoneNumber
     * @return TMobileMyTariff that includes Pair<header, tariff name> and Map<category name, List<TMobileData>> from Můj tarif page
     * @throws InvalidTableException when unknown table is found
     * @throws IOException
     * @throws LogoutException when you aren't signed in.
     * @throws UnknownNumberException when number wasn't found (you are not owner of this phone number)
     */
    public TMobileMyTariff getMyTariff(String phoneNumber) throws InvalidTableException, IOException, LogoutException, UnknownNumberException;

    /**
     * You have to use login(String username, String password ) first to sign up.
     * Doesn't work, if you used getMyTariff or getServiceSettings before with different phoneNumber. (T-Mobile server problem)
     * @param phoneNumber
     * @return TMobileServiceSettings that includes Map<category name, List<TMobileData>> from Nastaveni sluzeb page
     * @throws InvalidTableException when unknown table is found
     * @throws IOException
     * @throws LogoutException when you aren't signed in.
     * @throws UnknownNumberException when number wasn't found (you are not owner of this phone number)
     */
    public TMobileServiceSettings getServiceSettings(String phoneNumber) throws InvalidTableException, IOException, LogoutException, UnknownNumberException;

    /*
    public void resetDataRoamingLimit ( String phoneNumber );

    public void holidaySuspending ( String phoneNumber );
*/
}
