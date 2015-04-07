package edu.msu.wegschei.flocking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends ActionBarActivity {
    private EditText playerName;
    private EditText playerPassword;

    private final static String PLAYER_NAME = "LoginActivity.playerName";
    private final static String PLAYER_PASSWORD = "LoginActivity.playerPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.playerName = (EditText)findViewById(R.id.playerName);
        this.playerPassword = (EditText)findViewById(R.id.playerPassword);

        if(savedInstanceState != null) {
            // We have saved state
            loadInstanceState(savedInstanceState);
        }
    }

    public void loadInstanceState(Bundle bundle) {
        String nameOne = bundle.getString(PLAYER_NAME);
        String nameTwo = bundle.getString(PLAYER_PASSWORD);

        playerName.setText(nameOne, TextView.BufferType.EDITABLE);
        playerPassword.setText(nameTwo, TextView.BufferType.EDITABLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        String nameOne = playerName.getText().toString();
        String nameTwo = playerPassword.getText().toString();

        bundle.putString(PLAYER_NAME, nameOne);
        bundle.putString(PLAYER_PASSWORD, nameTwo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_rules:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // Parameterize the builder
                builder.setTitle(R.string.rules);
                builder.setMessage(R.string.rules_text);
                builder.setPositiveButton(android.R.string.ok, null);

                // Create the dialog box and show it
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onStartGame(View view){
        //Here is where we need to query (or call a function to query) the server to see if this user
        //exists.  If they do, proceed with the rest of the function (just loads up the main game atm)


        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra(GameActivity.PLAYER_ONE, playerName.getText().toString());
        //intent.putExtra(GameActivity.PLAYER_TWO, playerPassword.getText().toString());

        startActivity(intent);
    }

    public void onCreateAccount(View view){
        Intent intent = new Intent(this, CreateAccount.class);
        startActivity(intent);
    }
}
