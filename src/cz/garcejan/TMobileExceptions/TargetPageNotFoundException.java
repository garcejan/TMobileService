package cz.garcejan.TMobileExceptions;

public class TargetPageNotFoundException extends Exception{
    public TargetPageNotFoundException (String message) {
        super(message);
    }

    public TargetPageNotFoundException () {}

}
