package cz.garcejan;

import cz.garcejan.TMobileExceptions.InvalidTableException;
import cz.garcejan.TMobileExceptions.LogoutException;
import cz.garcejan.TMobileExceptions.ServiceAlreadyActivatedException;
import cz.garcejan.TMobileExceptions.UnknownNumberException;

import java.io.IOException;

public interface TMobileServiceInterface {

    /**
     * This method connects to your T-Mobile account.
     *
     * @param username: your T-Mobile username
     * @param password: password of your account
     * @return server response - enum
     */
    TMobileServerResponse login(String username, String password);

    /**
     * You have to use login(String username, String password ) first to sign up.
     * Doesn't work, if you used any other methods with different phoneNumber before. (T-Mobile server problem)
     *
     * @param phoneNumber
     * @return TMobileMyTariff that includes Pair<header, tariff name> and Map<category name, List<TMobileData>> from Můj tarif page
     * @throws InvalidTableException  when unknown table is found
     * @throws IOException
     * @throws LogoutException        when you aren't signed in.
     * @throws UnknownNumberException when number wasn't found (you are not owner of this phone number)
     */
    TMobileMyTariff getMyTariff(String phoneNumber) throws InvalidTableException, IOException, LogoutException, UnknownNumberException, NullPointerException;

    /**
     * You have to use login(String username, String password ) first to sign up.
     * Doesn't work, if you used any other methods with different phoneNumber before. (T-Mobile server problem)
     *
     * @param phoneNumber
     * @return TMobileServiceSettings that includes Map<category name, List<TMobileData>> from Nastaveni sluzeb page
     * @throws InvalidTableException  when unknown table is found
     * @throws IOException
     * @throws LogoutException        when you aren't signed in.
     * @throws UnknownNumberException when number wasn't found (you are not owner of this phone number)
     */
    TMobileServiceSettings getServiceSettings(String phoneNumber) throws InvalidTableException, IOException, LogoutException, UnknownNumberException, NullPointerException;

    /**
     * This method resets your phoneNumber data roaming limit.
     * But first you have to use method TMobileServiceInterFace.login to sign up.
     * Doesn't work, if you used any other methods with different phoneNumber before. (T-Mobile server problem)
     *
     * @param phoneNumber
     * @return TMobileServerResponse
     * @throws UnknownNumberException when number wasn't found (you are not owner of this phone number)
     * @throws IOException
     * @throws LogoutException        when you aren't signed in.
     */
    TMobileServerResponse resetDataRoamingLimit(String phoneNumber) throws UnknownNumberException, IOException, LogoutException, NullPointerException;


    /**
     * This method sets your holiday suspending for phoneNumber.
     * But first you have to use method TMobileServiceInterFace.login to sign up.
     * Doesn't work, if you used any other methods with different phoneNumber before. (T-Mobile server problem)
     *
     * @param phoneNumber
     * @param from:       MUST be in format dd.MM.yyyy and >= today's date
     * @param to:         MUST be in format dd.MM.yyyy and >= @param from
     * @return TMobileServerResponse
     * @throws UnknownNumberException when number wasn't found (you are not owner of this phone number)
     * @throws IOException
     * @throws LogoutException        when you aren't signed in.
     */
    TMobileServerResponse holidaySuspending(String phoneNumber, String from, String to) throws UnknownNumberException, IOException, LogoutException, NullPointerException;

    /**
     * Set holiday suspending from today to @param to
     */
    TMobileServerResponse holidaySuspending(String phoneNumber, String to) throws UnknownNumberException, IOException, LogoutException, NullPointerException;

    /**
     * Set holiday suspending for a week from today
     */
    TMobileServerResponse holidaySuspending(String phoneNumber) throws UnknownNumberException, IOException, LogoutException, NullPointerException;

    /**
     * This method deactivates phoneNumber holiday suspending.
     * But first you have to use method TMobileServiceInterFace.login to sign up.
     * Doesn't work, if you used any other methods with different phoneNumber before. (T-Mobile server problem)
     *
     * @param phoneNumber
     * @return TMobileServerResponse
     * @throws UnknownNumberException
     * @throws IOException
     * @throws LogoutException
     * @throws NullPointerException
     */
    TMobileServerResponse deactivateHolidaySuspending(String phoneNumber) throws UnknownNumberException, IOException, LogoutException, NullPointerException;

}
