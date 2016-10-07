# DeviceController 
This application is an background server and SMS receiver that can used Android devices runs API 14 and higher versions. You can also activate in Device administrators in settings to give extra features permissions 

## How it works?
* It runs CoolSocket server at 4632 port. 
* It communicates with JSON
* It's password secured so no one can access expect the one who set the access password.
* It starts itself when it detects network status changes
* It also reads the messages that starts with '{' character

## How to send commands
* First of all you need send commands using CoolJsonCommunication.Messenger class
* You can use our Android app to send commands http://github.com/genonbeta/CoolSocket-Client/releases (read below)

## How to use CoolSocket (Client) app
* You need to enter the target device IP address
* To find devices on the network you can use "Pair Finder"
* To get available commands send "request commands"
* Commands are available in this file https://github.com/velitasali/DeviceController/blob/master/app/src/main/res/raw/commands.txt

