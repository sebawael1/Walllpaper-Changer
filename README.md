#### Every Unlock, A New Experience! 🌟

🚀 Unlock a new experience every time! Say goodbye to boring wallpapers—this app transforms your lock screen and home screen with a fresh image every time you unlock your phone! Choose your favorite pictures and let them cycle automatically, bringing excitement, beauty, and a personal touch to your device. Whether it’s breathtaking landscapes, stunning art, or cherished memories, every unlock is a surprise! Make your screen come alive! 🎉✨

![Illustrative image](https://github.com/user-attachments/assets/5c29b728-8484-4241-9961-cbdddaef8912)


# In This Project I Used:
- 4 Files written with java:
**MainActivity.java** responsible for 'Handles image selection and wallpaper setting', 'Requests necessary permissions', 'Starts WallpaperService and registers receivers for screen unlock and lock events' **UnlockReceiver.java**:'Listens for screen unlock events and changes the home screen wallpaper', 'Resets wallpaper index on device boot' **LockScreenReceiver.java**:'Listens for the screen turningIncludes buttons for selecting images and setting wallpapers. on while locked', 'Updates the lock screen wallpaper' **WallpaperService.java**'Runs a foreground service to keep wallpaper updates active', Uses a notification to prevent the service from being killed.
- XML File:
Defines the UI layout for MainActivity, Includes buttons for selecting images and setting wallpapers.
- AndroidManifest.xml:
Defines app permissions, activities, services, and broadcast receivers, Declares required permissions (e.g., wallpaper, storage, and battery optimizations), Registers MainActivity, WallpaperService, UnlockReceiver, and LockScreenReceiver.

## Features of Automatic Wallpaper Changer:
* Changes on Lock/Unlock – Updates wallpaper every time the screen is locked/unlocked
* Multiple Images – Select and cycle through multiple wallpapers
* Runs in Background – Uses a foreground service to keep updating.
* Boot Persistence – Continues working after device restart.
* Battery Optimization Handling – Ensures smooth operation without interruptions.

## Usage:
1. Grant Permissions
2. Select Images
3. click Set as wallpaper
4. The wallpaper will change every time you unlock the device.


### _Every time you lock/unlock your device, the wallpaper will change automatically._
