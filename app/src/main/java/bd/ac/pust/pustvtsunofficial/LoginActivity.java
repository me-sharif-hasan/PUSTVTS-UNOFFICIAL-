package bd.ac.pust.pustvtsunofficial;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import bd.ac.pust.pustvtsunofficial.Helper.LocationPermissionChecker;


public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText userName;
    EditText password;
    TextView error;
    ImageView logo;
    ProgressBar progressBar;
    LocationPermissionChecker permissionChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        loginButton=findViewById(R.id.button);
        userName=findViewById(R.id.editTextTextPersonName);
        password=findViewById(R.id.editTextTextPersonName2);
        error=findViewById(R.id.message);
        logo=findViewById(R.id.imageView2);
        progressBar=findViewById(R.id.login_load);
        permissionChecker = new LocationPermissionChecker(LoginActivity.this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(permissionChecker.checkPermission()){
                        error.setText("Permission Granted.");
                    }else{
                        error.setText("Permission Not Granted.");
                    }
//                String usr=userName.getText().toString().trim();
//                String pass=password.getText().toString().trim();
//                if(usr==null||usr.equals("")){
//                    error.setTextColor(Color.RED);
//                    error.setText("You must use a username!");
//                    return;
//                }
//                if(pass==null||pass.equals("")){
//                    error.setTextColor(Color.RED);
//                    error.setText("You must use your password!");
//                    return;
//                }
//                // if both entered, try login.
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    error.setTextColor(Color.BLACK);
//                                    error.setText("Please wait");
//                                    progressBar.setVisibility(View.VISIBLE);
//                                }
//                            });
//                            BusFactory.checkUsernamePassword(usr,pass);
//                            TrackerConfig.updateUserAndPass(usr,pass);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    error.setTextColor(Color.GREEN);
//                                    error.setText("Login successful");
//                                    Intent i=new Intent(LoginActivity.this,BusFinderActivity.class);
//                                    startActivity(i);
//                                    finish();
//                                }
//                            });
//                        } catch (Exception e) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        CookieManger.getInstance().clearCookies();
//                                    } catch (Exception ex) {
//                                        ex.printStackTrace();
//                                    }
//                                    error.setTextColor(Color.RED);
//                                    error.setText(e.getLocalizedMessage());
//                                    progressBar.setVisibility(View.GONE);
//                                }
//                            });
//                        }
//                    }
//                }).start();
            }
        });
    }

}