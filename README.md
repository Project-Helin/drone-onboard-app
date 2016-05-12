# Project Helin - Drone Onboard App

## Local Setup with Drone-Simulator and Genimotion Android Emulator

1. Setup SITL Simulator http://python.dronekit.io/develop/sitl_setup.html
2. Start SITL with `dronekit-sitl copter`
3. Clone Github-Repo and open it in Android Studio
4. Setup Genimotion or another Android Emulator. Make sure you have Google Play Services installed. (http://forum.xda-developers.com/showthread.php?t=2528952)
5. Look up the Ip of the Virtual-Machine Ethernet-Interface (OSX = Vboxnet1)
6. Set the IP as IP at Main Activity

## Local Setup with Drone-Simulator, Genimotion Android Emulator and Groundstation

1. Setup SITL Simulator http://python.dronekit.io/develop/sitl_setup.html
2. Start SITL with `dronekit-sitl copter --home=47.2233,8.8819,584,353` (--home sets the drone location to a coordinate, attention that you don't have spaces after commas)
3. Setup MavProxy (https://erlerobotics.gitbooks.io/erle-robotics-mav-tools-free/content/en/installing_mavproxy.html)
4. Run MavProxy with ` mavproxy.py --master tcp:127.0.0.1:5760 --out 127.0.0.1:14550  --out 127.0.0.1:14551` (master defines connection to the Drone-Simulator(SITL), out defines udp outputs for groundstation and mobile)
5. Setup Genimotion. Make sure you have Google Play Services installed. (http://forum.xda-developers.com/showthread.php?t=2528952)
6. Open Virtualbox network preferences for Genimotion Emulator VM and add a port forwarding on NAT-Adapter:` host-port: 14551, host-ip: (leave empty),  guest-port: 14551, guest-ip: (ip of the phone in virtual network). ` The network address of the phone in the virtual network can be found with an app like Fing.
7. Clone Github-Repo and open it in Android Studio
8. Look up the Ip of the Virtual-Machine Ethernet-Interface with ipconfig or ifconfig(unix) (OSX = Vboxnet1, Vboxnet2)
9. Set the IP as LOCAL_IP at DroneConnectionService
10. Run Groundstation like Missionplanner or QGroundcontrol and connect to Port 14550 on localhost
11. Use MavProxy to issue Commands like `arm throttle` or `mode guided`

## Local Setup with Pixhawk attached by USB

1. Setup MavProxy (https://erlerobotics.gitbooks.io/erle-robotics-mav-tools-free/content/en/installing_mavproxy.html)
2. Plug in Pixhawk and search for usb connection (/dev/tty0 or tty.usbmodem1)
2. Run MavProxy with `mavproxy.py --master=/dev/tty.usbmodem1` (master defines connection to the pixhawk)
3. Use MavProxy to issue Commands like `arm throttle` 



