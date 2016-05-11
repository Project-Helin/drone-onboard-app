# Project Helin - Drone Onboard App

## Local Setup with Drone-Simulator

1. Setup SITL Simulator http://python.dronekit.io/develop/sitl_setup.html
3. Start SITL with `dronekit-sitl copter` 
4. Clone Github-Repo and open it in Android Studio
5. Setup Genimotion or another Android Emulator. Make sure you have Google Play Services installed. (http://forum.xda-developers.com/showthread.php?t=2528952)
6. Look up the Ip of the Virtual-Machine Ethernet-Interface (OSX = Vboxnet1)
7. Set the IP as IP at Main Activity

## Local Setup with Drone-Simulator and Groundstation

1. Setup SITL Simulator http://python.dronekit.io/develop/sitl_setup.html
3. Start SITL with `dronekit-sitl copter`
4. Clone Github-Repo and open it in Android Studio
5. Setup Genimotion or another Android Emulator. Make sure you have Google Play Services installed. (http://forum.xda-developers.com/showthread.php?t=2528952)
6. Look up the Ip of the Virtual-Machine Ethernet-Interface (OSX = Vboxnet1)
7. Set the IP as IP at Main Activity

## Local Setup with Pixhawk attached via USB

1. Setup MavProxy (https://erlerobotics.gitbooks.io/erle-robotics-mav-tools-free/content/en/installing_mavproxy.html)
2. Plug in Pixhawk and search for usb connection (/dev/tty0 or tty.usbmodem1)
2. Run MavProxy with `mavproxy.py --master=/dev/tty.usbmodem1` (master defines connection to the pixhawk)
3. Use MavProxy to Issue Commands like `arm throttle` 



