# cs190i_finalproject

### StationState
Maintains state of track you are listening to and / or tracks you are broadcasting
To be updated about these, SubscribeToListeningStationUpdates & SubscribeToBroadcastStationUpdates
To update the state of these, call UpdateListeningStation & UpdateBroadcastStation, passing in a StationState object
For example:
'''
StationState.UpdateBroadcastStation(new StationState(
        null,                 // User ID (string)
        "Peter Werner",       // User Name (string)
        null,                 // Track ID (string)
        "Piledriver Waltz",   // Track Name (string)
        "Alex Turner",        // Artist Name (string)
        true,                 // Is the track currently playing (from the broadcaster's perspective, in this case)? (boolean)
        30,                   // Length of the song in seconds (float)
        0,                    // Current position in the song in seconds (float)
        new HashSet<String>() // Set of User IDs listening to this broadcast
));
'''

### BroadcastIntroFragment
When the user clicks on this, spotify app is opened. See TODO comments for where to update the DB and and example of how to update the currently broadcasting track within the app (so the UI can reflect this).

### MainActivity
OnListeningSongChange is called when we are listening to a broadcaster and they change tracks.
OnListeningSongUpdate is called whenever the broadcaster updates the track (ie: scrubs through the track, really up to Shadee & how she does DB updates)

### MusicBarFragment
See TODO for the add / remove to / from spotify library button. Not critical feature but would be cool for saving tracks you like.

