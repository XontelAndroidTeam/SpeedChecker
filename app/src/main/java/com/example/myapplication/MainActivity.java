package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jignesh13.speedometer.SpeedoMeterView;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class MainActivity extends AppCompatActivity {

    SpeedoMeterView speedoMeterView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        speedoMeterView = findViewById(R.id.speedometerview);

    }

    public void butonTiklandi(View view) {
        new SpeedTestTask().execute();
    }


    public class SpeedTestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(SpeedTestReport report) {
                    final int deger = (int) report.getTransferRateBit().intValue()/100000;
                    final int dd = (int) report.getTransferRateOctet().intValue()/100000;
                    // called when download/upload is finished
                    Log.v("TATZ", "[COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("TATZ", "[COMPLETED] rate in bit/s   : " + report.getTransferRateBit());
                    Log.v("TATZ", "[COMPLETED] dd   : " + dd);
                    Log.v("TATZ", "[COMPLETED] deger   : " + deger);
                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    Log.e("TATZ", "onError: "+errorMessage);
                    Log.e("TATZ", "onError: "+speedTestError);
                    // called when a download/upload error occur
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {

                    final int deger = (int) report.getTransferRateBit().intValue()/100000;
                    final int dd = (int) report.getTransferRateOctet().intValue()/100000;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            speedoMeterView.setSpeed(dd, true);

                            textView.setText(dd+ " Mbps");
                        }
                    });


                    // called to notify download/upload progress
                    Log.v("TATZ", "[PROGRESS] progress : " + percent + "%");
                    Log.v("TATZ", "[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("TATZ", "[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
                }
            });
            speedTestSocket.setDownloadSetupTime(600000);
            speedTestSocket.startDownload("https://raw.githubusercontent.com/yourkin/fileupload-fastapi/a85a697cab2f887780b3278059a0dd52847d80f3/tests/data/test-5mb.bin");

            return null;
        }
    }
}