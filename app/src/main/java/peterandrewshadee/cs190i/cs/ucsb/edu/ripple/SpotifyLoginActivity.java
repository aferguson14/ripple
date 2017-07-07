package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ferg on 6/5/17.
 */

public class SpotifyLoginActivity extends Activity{

    //Client ID for individual user (must fetch from spotify)
    private static final String CLIENT_ID = "5aa6208c6cd54357ad6b55bd67197d51";
    //Redirect URI after Spotify grants/denies permission (specified on Spotify registration)
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final int REQUEST_CODE = 1337;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = CredentialsHandler.getToken(this);

        if (token == null) {
            setContentView(R.layout.login_page);
        } else {
            startMainActivity(token);
        }
    }

    public void spotifyLoginClicked(View V){

        if(NotificationManagerCompat.getEnabledListenerPackages(getApplicationContext()).contains(getApplicationContext().getPackageName())) {
            final AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

            builder.setScopes(new String[]{
                    "user-read-private",
                    "user-read-currently-playing",
                    "streaming",
                    "user-read-playback-state",
                    "user-library-modify",
                    "user-library-read"
            });
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        }
        else{
            Toast.makeText(getApplicationContext(), "Must Allow Notification Access for Ripple", Toast.LENGTH_LONG).show();
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Log.d("MainActivity", "onActivityResult");
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            Log.d("MainActivity", "authResponse:" + response.toString());


            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Log.d("LoginActivity", "token obtained");
                    CredentialsHandler.setToken(this, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
                    startMainActivity(response.getAccessToken());
                    break;
                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    private void startMainActivity(String accessToken){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.ACCESS_TOKEN, accessToken);
        startActivity(intent);
        finish();
    }
}
