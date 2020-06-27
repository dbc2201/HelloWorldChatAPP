package dbc2201.app.helloworldchatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

import screen.unified.CometChatUnified;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public static final String userName = "DBC";
    private static final String appID = "REDACTED";
    private static final String region = "us";
    private static final String authKey = "REDACTED";
    public static int userCount = 61;
    public static final String userID = "user" + userCount;
    AppSettings appSettings = new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers()
            .setRegion(region).build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        CometChat.init(this, appID, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {

                Timber.d("onSuccess: Init Completed Successfully");
                User user = new User();
                user.setUid(userID);
                user.setName(userName);
                userCount++;

                CometChat.createUser(user, authKey, new CometChat.CallbackListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        Timber.d("onSuccess: User Created Successfully");
                        if (CometChat.getLoggedInUser() == null) {
                            CometChat.login(userID, authKey, new CometChat.CallbackListener<User>() {
                                @Override
                                public void onSuccess(User user) {
                                    Timber.d("onSuccess: %s successfully logged in!", userName);
                                    Toast.makeText(MainActivity.this, "Welcome, " + userName, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, CometChatUnified.class));
                                }

                                @Override
                                public void onError(CometChatException e) {
                                    Timber.e("onError: Error while logging in %s", userName);
                                }
                            });
                        } else {
                            // user is already logged in
                            // TODO show a welcome message
                            Timber.d("onSuccess: %s was already logged in!", userName);
                            Toast.makeText(MainActivity.this, "Hey,  " + userName + " welcome back!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, CometChatUnified.class));
                        }
                    }

                    @Override
                    public void onError(CometChatException e) {
                        Timber.e("onError: User Creation Failed:  %s \n %s", e.getMessage(), e.getDetails());
                        Toast.makeText(MainActivity.this, "user failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(CometChatException e) {
                Timber.e("onError: Init Failed with Exception: %s", e.getMessage());
            }
        });


    }

}