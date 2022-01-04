# Java(FX) control software for the Waterkotte Resümat CD4

## About this project  
This program allows you to control and monitor a Waterkotte Resümat heating controller via your PC.  
It uses the RS232 serial port of the Resümat controller.

### Disclaimer
**This is not an official software.**  
Do not set any values if the corresponding readings don't match the displayed data on the control unit itself or if any other readings are not correct! I'm not responsible for any damage!

## Running it
Download the release file (wtkcontrol-1.0.jar) from https://github.com/mwllgr/java-wtkcontrol/releases/tag/v1.0.0 and run it:
```bash
java -jar wtkcontrol-1.0.jar
```

Start by selecting an address file and opening the serial port - you might have enter the device name manually (e.g. `COM1` or `ttyUSB0`).  
Java version >= 11 is required to run the file.

## CLI mode
You can run the program without a GUI by using the parameter `--no-gui` with an appended `--port <device>`.  
When an `--address-list <file>` is not given, the file `address-list.csv` is used.

Currently, only reading every address value and printing them is possible.

Example:
```shell
java -jar wtkcontrol-1.0.jar --no-gui --port /dev/ttyUSB0
```

Output example:
```
Messbeginn-Datum=20.04.19
Hz-Messergebnis=4880.2
Mess-Reset=0
KomprBeginn-Zeit=01:01:01
KomprBetrStunden=43935.9
Unterbrechungen=00000000
```



### Available parameters
The following parameters *do not control any GUI elements* and are only working when the program is started in `--no-gui` mode!

| **Parameter**           | **Description**                                                                                                           | **Required** | **Default**        |
|-------------------------|---------------------------------------------------------------------------------------------------------------------------|--------------|--------------------|
| --no-gui                | Prevents starting the application in GUI mode                                                                             | -            | -                  |
| --port \<device\>       | Serial port device, e.g. `COM1` or `/dev/ttyUSB0`                                                                         | Yes          | -                  |
| --address-list \<file\> | Specifies the comma-separated address list                                                                                | No           | `address-list.csv` |
| --logger                | *Only used in read mode:* Saves the received values into `wtk logger-dd-MM-yyyy_hh-mm-ss.cs` before the application exits | No           | -                  |

## Documentation
The documentation is available in PDF only (school requirement).  
You can check it out here: [WtkControl_mwllgr.pdf](WtkControl_mwllgr.pdf)

As the main business area for this specific model is Germany and Austria, the program UI is written in German.

## Screenshots

### Main window

![WtkControl main window](WtkControl_Main.png)
