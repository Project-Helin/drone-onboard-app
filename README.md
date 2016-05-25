# Project Helin - Drone Onboard App

This works as a bridge between the Server and the FlightController on the drone. It also provides a User-Interface 
which gives information about the state of the drone and missions it has to fulfil.
It uses [AMQP](https://en.wikipedia.org/wiki/Advanced_Message_Queuing_Protocol)
to communicate with the server and [DroneKit-Android](https://github.com/dronekit/dronekit-android) as an 
API for communicating with the drone over [MAVLink](https://en.wikipedia.org/wiki/MAVLink).

# Getting Started

## Requirements
* Android Studio (Tested with Version 2.1)
* Android SDKs for the Android Version you want to develop for(Tested with Android 4.4.4)
* VirtualBox (Tested with Version 5.0.20)
* [Server Project](https://github.com/Project-Helin/server) installed 

## Local Setup with Drone-Simulator and Android Emulator
This setup is useful for testing with a simulated drone

1. Clone https://github.com/Project-Helin/drone-onboard-app and open it in Android Studio
2. Setup SITL Simulator http://python.dronekit.io/develop/sitl_setup.html
3. Start SITL with `dronekit-sitl copter`
4. Setup Genimotion or another Android Emulator. Make sure you have Google Play Services installed. (http://forum.xda-developers.com/showthread.php?t=2528952)
5. Look up the Ip of the Virtual-Machine Ethernet-Interface (OSX = Vboxnet1)
6. Set the IP as IP at Main Activity

## Local Setup with Drone-Simulator, Android Emulator and Groundstation
Use this setup if you want to see what your drone does, after you issued commands.

1. Clone https://github.com/Project-Helin/drone-onboard-app and open it in Android Studio
2. Setup SITL Simulator http://python.dronekit.io/develop/sitl_setup.html
3. Start SITL with `dronekit-sitl copter --home=47.2233,8.8819,584,353` (--home sets the drone location to a coordinate, attention that you don't have spaces after commas)
4. Setup MavProxy (https://erlerobotics.gitbooks.io/erle-robotics-mav-tools-free/content/en/installing_mavproxy.html)
5. Run MavProxy with `mavproxy.py --master tcp:127.0.0.1:5760 --out 127.0.0.1:14550  --out 127.0.0.1:14551` (master defines connection to the Drone-Simulator(SITL), out defines udp outputs for groundstation and mobile)
6. Setup Genimotion. Make sure you have Google Play Services installed. (http://forum.xda-developers.com/showthread.php?t=2528952)
7. Open Virtualbox network preferences for Genimotion Emulator VM and add a port forwarding on NAT-Adapter: ` host-port: 14551, host-ip: (leave empty),  guest-port: 14551, guest-ip: (ip of the phone in virtual network). ` The network address of the phone in the virtual network can be found with an app like Fing.
8. Look up the Ip of the Virtual-Machine Ethernet-Interface with ipconfig or ifconfig(unix) (OSX = Vboxnet1, Vboxnet2)
9. Set the IP as LOCAL_IP at DroneConnectionService
10. Run Groundstation like Missionplanner or QGroundcontrol and connect to Port 14550 on localhost
11. Use MavProxy to issue Commands like `arm throttle` or `mode guided`

## Local Setup with Pixhawk attached by USB
This setup is useful if you want to test if some MAVLink-Commands work with your real FlightController.

1. Setup MavProxy (https://erlerobotics.gitbooks.io/erle-robotics-mav-tools-free/content/en/installing_mavproxy.html)
2. Plug in Pixhawk and search for usb connection (/dev/tty0 or tty.usbmodem1)
3. Run MavProxy with `mavproxy.py --master=/dev/tty.usbmodem1` (master defines connection to the pixhawk)
4. Use MavProxy to issue Commands like `arm throttle` 



