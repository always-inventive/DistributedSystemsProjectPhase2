package omada_80.dsproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * The first activity started by clicking app launcher icon.
 * Actually, the first "screen" shown by the application.
 * Sets the layout,adding listeners to proceed to next activity.
 */
public class LauncherActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);  // Setting activity_launch.xml as layout

        /* Getting reference to 'next' button and setting a listener */
        Button enter = (Button) findViewById(R.id.enterBtn);
        /* Adding listener to 'enter' button */
        enter.setOnClickListener(new View.OnClickListener() {

            /**
             * Implements onClickListener.
             * Builds an alert dialog that asks for a number of MW-Servers in order to proceed.
             * Using TextWatcher, it cannot proceed until input isn't blank.
             * @param view The view to be clicked, here 'Enter' button
             */
            public void onClick(View view) {
                final EditText input = new EditText(LauncherActivity.this);

                /* Building alert dialog for the user */
                AlertDialog.Builder alert = new AlertDialog.Builder(LauncherActivity.this);
                alert.setTitle("Number of MW-Servers");
                alert.setView(input);

                /* Setting positive button 'proceed' plus setting a listener in order to pass the
                   the number of MW-Servers/start new activity if the button is clicked */
                alert.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int n = Integer.parseInt(input.getText().toString().trim());  // Input

                        /* Start new activity with the data passed in the intent */
                        Intent it = new Intent(LauncherActivity.this, ConnectionInfoActivity.class);
                        it.putExtra("numberMW",n);  // Passing the number to new activity
                        startActivity(it);
                    }
                });

                /* Setting negative button 'cancel' plus setting a listener for canceling the alert
                   dialog and return if clicked */
                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                /* Creating/Showing dialog from the alert builder */
                final AlertDialog dialog = alert.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);  // Disable button

                /* Adding/Implementing listener to input (TextWatcher) for not letting the user
                   actually enter next activity if the input is blank, by disabling 'proceed' */
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence,int i,int i2,int i3) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence,int i,int i2,int i3) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String s1 = input.getText().toString();
                        Button dButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                        if (s1.equals("")) {
                            dButton.setEnabled(false);
                        } else {
                            dButton.setEnabled(true);
                        }
                    }

                });  // End of implementation of TextWatcher Listener on 'input'

            }

        });  // End of implementation of clickListener on 'enter' button

    }

}


