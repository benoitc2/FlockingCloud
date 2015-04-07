package edu.msu.wegschei.flocking;


import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class CreateAccount extends ActionBarActivity {

    private EditText userName;
    private EditText userPassword;
    private EditText userPasswordConfirm;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_user_create);

        this.userName = (EditText)findViewById(R.id.userName);
        this.userPassword = (EditText)findViewById(R.id.userPassword);
        this.userPasswordConfirm = (EditText)findViewById(R.id.userPasswordConfirm);
    }

    public void onCreateUser(View view) {
        onReturnToLogin();  //temp call for testing, does not keep only 1 copy of activity on stack atm

        if(userPassword.getText() == userPasswordConfirm.getText() && userName.length() > 0) {
            //Requirements met, query server to create this user

            //Check if username is already being used in server, if it is, throw an error and do not add user

            //Otherwise, add the user and return to log in activity
            //onReturnToLogin();
        }
        else {
            if(userPassword.getText() != userPasswordConfirm.getText()) {
                //Password and confirm password does not match error message
            }
            else if(userName.length() == 0) {
                //Username must be at least 1 character error message
            }
        }
    }

    public void onReturnToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);
        this.finish();
    }

}
