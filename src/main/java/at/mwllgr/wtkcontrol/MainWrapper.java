package at.mwllgr.wtkcontrol;

import java.util.Arrays;
import java.util.List;

/**
 * Needed for JavaFX to allow a proper application start.
 * Run the program using this class!
 */
public class MainWrapper {
    public static void main(String[] args) {
        List<String> argList = Arrays.asList(args);
        if(args.length != 0 && (argList.contains("--no-gui") || argList.contains("--help") || argList.contains("-h"))) {
            // Continue as CLI application
            Cli.getInstance(args);
        } else {
            // Start GUI program
            Main.main(args);
        }
    }
}
