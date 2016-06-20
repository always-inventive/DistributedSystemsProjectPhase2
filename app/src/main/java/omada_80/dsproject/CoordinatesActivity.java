package omada_80.dsproject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * The third activity after getting connection info with the servers.
 * This activity asks the user to give coordinates(limits-range) in order to
 * instantiate Master(responsible mostly for the whole MapReduce process)
 * and pass them to its instance in order to begin the process.
 */
public class CoordinatesActivity extends Activity {

    private String mapAddresses[][];     // Map-workers server connection info (IP-Port)
    private String reduceAddresses[][];  // Reduce-workers server connection info (IP-Port)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates); // Setting activity_coordinates.xml as layout

        Object[] objectArray;

        /* Deserializing map addresses */
        objectArray = (Object[]) getIntent().getExtras().getSerializable("map_addresses");
        if (objectArray != null) {
            mapAddresses = new String[objectArray.length][];
            for (int i = 0; i < objectArray.length; i++) {
                mapAddresses[i] = (String[]) objectArray[i];
            }
        }

        /* Deserializing reduce addresses */
        objectArray = (Object[]) getIntent().getExtras().getSerializable("reduce_addresses");
        if (objectArray != null) {
            reduceAddresses = new String[objectArray.length][];
            for(int i = 0; i < objectArray.length; i++){
                reduceAddresses[i]= (String[]) objectArray[i];
            }
        }


        /* Getting reference to editable texts and button on the screen to be used later */
        final EditText etMinLat = (EditText) findViewById(R.id.minLatitudeET);
        final EditText etMaxLat = (EditText) findViewById(R.id.maxLatitudeET);
        final EditText etMinLong = (EditText) findViewById(R.id.minLongitudeET);
        final EditText etMaxLong = (EditText) findViewById(R.id.maxLongitudeET);
        final EditText etMinDT = (EditText) findViewById(R.id.minDTET);
        final EditText etMaxDT = (EditText) findViewById(R.id.maxDTET);
        final Button button = (Button) findViewById(R.id.startBtn);

        button.setEnabled(false);

        /* Adding/Implementing listener to be added to all edit-texts (TextWatcher) for not letting
           the user actually press 'start' button if the input is blank, by disabling it */
        TextWatcher mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String minLat = etMinLat.getText().toString();
                String maxLat = etMaxLat.getText().toString();
                String minLong = etMinLong.getText().toString();
                String maxLong = etMaxLong.getText().toString();


                if (minLat.equals("") || maxLat.equals("") ||
                        minLong.equals("") || maxLong.equals("")) {

                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }

            }
        };

        /* Adding textWatcher implemented before to edit texts */
        etMinLat.addTextChangedListener(mTextWatcher);
        etMaxLat.addTextChangedListener(mTextWatcher);
        etMinLong.addTextChangedListener(mTextWatcher);
        etMaxLong.addTextChangedListener(mTextWatcher);

        /* Adding listener to 'start' button */
        button.setOnClickListener(new View.OnClickListener() {

            /**
             * Implements onClickListener.
             * Gets all the data gathered (mw-servers number,coordinates,connection info) and
             * actually starts the whole Map-Reduce process by using Master.
             * @param view The view to be clicked, here 'Start' button
             */
            public void onClick(View view) {
                final Coordinates coordinates = new Coordinates();
                coordinates.setMinLatitude(Double.parseDouble(etMinLat.getText().toString()));
                coordinates.setMaxLatitude(Double.parseDouble(etMaxLat.getText().toString()));
                coordinates.setMinLongitude(Double.parseDouble(etMinLong.getText().toString()));
                coordinates.setMaxLongitude(Double.parseDouble(etMaxLong.getText().toString()));

                String minDT = etMinDT.getText().toString();
                coordinates.setMinDT( minDT.equals("") ? "2012-04-03 18:00:00" : minDT);

                String maxDT = etMaxDT.getText().toString();
                coordinates.setMaxDT( maxDT.equals("") ? "2012-08-16 03:00:00" : maxDT);

                AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Master master = new Master(mapAddresses, reduceAddresses,
                                    coordinates, CoordinatesActivity.this);
                            master.startProcess();
                        }
                    });


            }

        });  // End of implementation of clickListener on 'start' button

    }

}

