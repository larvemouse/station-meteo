package com.example.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class Communication extends AppCompatActivity {

    Button btn1, btn2, btn3, btn4, btn5, btnDis;
    String address = null;
    TextView lumn,reception;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ReadSignal rsignal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS);

        rsignal = new ReadSignal();

        setContentView(R.layout.meteo);

        btn1 = (Button) findViewById(R.id.button2);
        btn2 = (Button) findViewById(R.id.button3);
        btn3 = (Button) findViewById(R.id.button5);
        btn4 = (Button) findViewById(R.id.button6);
        btn5 = (Button) findViewById(R.id.button7);
        btnDis = (Button) findViewById(R.id.button4);
        lumn = (TextView) findViewById(R.id.textView2);
        reception=(TextView) findViewById(R.id.reception);

        new ConnectBT().execute();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("1");
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("a");
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("3");
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("4");
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("5");
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });
    }

    private void sendSignal(String number) {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(number.getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }


    private void Disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                msg("Error");
            }
        }

        finish();
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Communication.this, "Connexion en cours", "Patientez s'il vous plait...");
        }


        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("La connexion a échoué");
                finish();
            } else {
                msg("Connecté !");
                isBtConnected = true;
                rsignal.execute();
            }

            progress.dismiss();
        }
    }

    private class ReadSignal extends AsyncTask<Void, Integer, Void> {

        String reconstitution;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            reconstitution = new String();
        }

        @Override
        protected Void doInBackground(Void... devices) {

            int test;

            try {


                    while(btSocket!=null) {
                        try {
                            test = btSocket.getInputStream().read();
                            publishProgress(test);

                        } catch (IOException e) {
                            msg("Error");
                        }
                    }

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... valeur) {
            String a = Character.toString((char) (int) valeur[0]);
            reconstitution = reconstitution.concat(a);

            //System.out.print(reconstitution);
            if(reconstitution.endsWith("\n")){
                System.out.println("ok:" + reconstitution);
                reception.setText(reconstitution);
                //System.out.print("OKOK");
                reconstitution = ""; //Reset de reconst.
                if(reconstitution.startsWith("temp"))
            }



        }
    }
}

