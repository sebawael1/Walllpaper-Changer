#### Every Unlock, A New Experience! 🌟

🚀 Unlock a new experience every time! Say goodbye to boring wallpapers—this app transforms your lock screen and home screen with a fresh image every time you unlock your phone! Choose your favorite pictures and let them cycle automatically, bringing excitement, beauty, and a personal touch to your device. Whether it’s breathtaking landscapes, stunning art, or cherished memories, every unlock is a surprise! Make your screen come alive! 🎉✨

# In This Project I Used:
- 4 Files written with java
****MainActivity.java


  responsible for 'Handles image selection and wallpaper setting', 'Requests necessary permissions', 'Starts WallpaperService and registers receivers for screen unlock and lock events'
## UnlockReceiver.java
:'Listens for screen unlock events and changes the home screen wallpaper', 'Resets wallpaper index on device boot'
## LockScreenReceiver.java
'Listens for the screen turningIncludes buttons for selecting images and setting wallpapers. on while locked', 'Updates the lock screen wallpaper'
## WallpaperService.java
: 'Runs a foreground service to keep wallpaper updates active', Uses a notification to prevent the service from being killed.
- XML File:
Defines the UI layout for MainActivity, Includes buttons for selecting images and setting wallpapers.
- AndroidManifest.xml
Defines app permissions, activities, services, and broadcast receivers, Declares required permissions (e.g., wallpaper, storage, and battery optimizations), Registers MainActivity, WallpaperService, UnlockReceiver, and LockScreenReceiver.

