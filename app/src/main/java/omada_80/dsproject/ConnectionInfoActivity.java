package omada_80.dsproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * The second activity following launch activity,
 * responsible for getting map/reduce-worker servers connection information.
 * Sets the layout,adding listeners to proceed to next activity.
 */
public class ConnectionInfoActivity extends Activity {

    private String mapAddresses[][];     // Map-workers server connection info (IP-Port)
    private String reduceAddresses[][];  // Reduce-workers server connection info (IP-Port)
    private int    mwNum;                // Number of map-workers servers to connect

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);  // Setting activity_connections as layout

        /* Getting data passed from previous activity (the number of MWServers to connect) */
        final int N = this.getIntent().getExtras().getInt("numberMW");
        mwNum = 1;  // Initializing the number of the current MWServer info

        ((TextView) findViewById(R.id.mwNumber)).setText(String.format(
                getResources().getString(R.string.mwNumber), mwNum ));  // Setting the tag number

        mapAddresses = new String[N][2];
        reduceAddresses = new String[1][2];

        /* Getting reference to editable texts and button on the screen to be used later */
        final EditText etAddress = (EditText) findViewById(R.id.ipEditText);
        final EditText etPort = (EditText) findViewById(R.id.portEditText);
        final Button button = (Button) findViewById(R.id.nextBtn);

        button.setEnabled(false);

        /* Adding/Implementing listener to be added to both edit-texts (TextWatcher) for not letting
           the user actually press 'next' button if the input is blank, by disabling it */
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String address = etAddress.getText().toString();
                String port = etPort.getText().toString();

                if (address.equals("") || port.equals("")) {
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }

            }
        };

        /* Adding textWatcher implemented before to both edit texts */
        etAddress.addTextChangedListener(textWatcher);
        etPort.addTextChangedListener(textWatcher);

        /* Adding listener to 'next' button */
        button.setOnClickListener(new View.OnClickListener() {

            /**
             * Implements onClickListener.
             * Changes the number of the tagged mw-server processed at the time down-left.
             * If the number of mw-servers to connect is reached, changes to the title to RW.
             * Finally, when all connection info is set, passes the connection arrays to next
             * activity (Coordinates activity) in a bundle and starts it.
             * @param view The view to be clicked, here 'Next' button
             */
            public void onClick(View view) {
                if (mwNum < N) {
                    /* Storing mw-server connection info */
                    mapAddresses[mwNum - 1][0] = etAddress.getText().toString();
                    mapAddresses[mwNum - 1][1] = etPort.getText().toString();

                    mwNum++;  // Incrementing the number of mw-server at the time

                    ((TextView) findViewById(R.id.mwNumber)).setText(String.format(
                            getResources().getString(R.string.mwNumber), mwNum ));

                    etAddress.setText("");
                    etPort.setText("");

                } else if (mwNum == N) {
                    /* Storing mw-server connection info */
                    mapAddresses[N - 1][0] = etAddress.getText().toString();
                    mapAddresses[N - 1][1] = etPort.getText().toString();

                    mwNum++; // Incrementing the number of mw-server at the time

                    findViewById(R.id.mwNumber).setVisibility(View.INVISIBLE);

                    etAddress.setText("");
                    etPort.setText("");

                    ((TextView) findViewById(R.id.serverTitle)).setText(
                            getResources().getString(R.string.rw_server));

                } else {
                    /* Storing rw-server connection info */
                    reduceAddresses[0][0] = etAddress.getText().toString();
                    reduceAddresses[0][1] = etPort.getText().toString();

                    /* Starting/Passing to Coordinates Activity data such as connection arrays */
                    Intent it = new Intent(ConnectionInfoActivity.this, CoordinatesActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("mw_num",N);
                    bundle.putSerializable("map_addresses", mapAddresses);
                    bundle.putSerializable("reduce_addresses", reduceAddresses);
                    it.putExtras(bundle);
                    startActivity(it);
                }

            }

        });  // End of implementation of clickListener on 'next' button

    }

}

