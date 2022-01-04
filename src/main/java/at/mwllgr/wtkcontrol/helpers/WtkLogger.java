package at.mwllgr.wtkcontrol.helpers;

import at.mwllgr.wtkcontrol.model.Repository;

public class WtkLogger {

    private static WtkLogger instance;
    private WtkLogger () {}
    Repository repo = Repository.getInstance();

    public static WtkLogger getInstance () {
        if (WtkLogger.instance == null) {
            WtkLogger.instance = new WtkLogger();
        }
        return WtkLogger.instance;
    }

    public void log(String message) {
        System.out.println(message);
    }

    public void error(String message) {
        System.err.println(message);
    }

    public void logGui(String message) {
        if(!repo.isNoGuiMode()) System.out.println(message);
    }

    public void errorGui(String message) {
        if(!repo.isNoGuiMode()) System.err.println(message);
    }
}
