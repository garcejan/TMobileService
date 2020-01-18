package cz.garcejan.TMobileExceptions;

public class ServiceAlreadyActivatedException extends Exception  {
    public ServiceAlreadyActivatedException(String message) {
        super(message);
    }

    public ServiceAlreadyActivatedException() {}
}
