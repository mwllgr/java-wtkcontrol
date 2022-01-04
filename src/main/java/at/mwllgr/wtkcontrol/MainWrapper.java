package at.mwllgr.wtkcontrol;

import at.mwllgr.wtkcontrol.helpers.WtkLogger;
import at.mwllgr.wtkcontrol.model.Repository;

import java.util.Arrays;

/**
 * Needed for JavaFX to allow a proper application start.
 * Run the program using this class!
 */
public class MainWrapper {
    public static void main(String[] args) {
        if(args.length != 0 && Arrays.asList(args).contains("--no-gui")) {
            // Continue as CLI application
            Cli.getInstance(args);
        } else {
            // Start GUI program
            Main.main(args);
        }
    }
}
