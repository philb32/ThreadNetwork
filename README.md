# ThreadNetwork
Use Google Thread Networking SDK to add Thread border router credentials to Google Play Services

Needed because without this app, Google does not work properly with my Thread border router made 
from Raspberry Pi + nRF52840 dongle.

Adding credentials using known dataset + border router id. When credentials are added to Play Services 
other apps like Google Home will recognise the border router (otherwise will just give an error message 
something like "This device needs a border router")

Note that controlling a device from Google Home App will need a google device (e.g. Google Home Mini) on the 
network as that will act as the Matter controller. Otherwise you can use the GHSAFM (Google Home Sample 
App for Matter) to act as a controller, in which case you won't need the Google Home Mini.
