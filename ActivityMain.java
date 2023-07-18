package com.example.sampleapp_20230717_003;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GnssAntennaInfo;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int REQUEST_PERMISSION = 1;
    private static final long MIN_TIME = 500; // ミリ秒
    private static final float MIN_DISTANCE = 0; // メートル
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private LocationManager locationManager;
    private TextView mainTextView;
    private boolean runningStatus;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTextView = findViewById(R.id.mainTextView);

        // 許可されていない権限のリストを取得する
        String[] deniedPermissions = getDeniedPermissions(this);
        // 許可されていない権限があればリクエストを行う
        if (deniedPermissions.length > 0) {
            ActivityCompat.requestPermissions(this, deniedPermissions, PERMISSION_REQUEST_CODE);
        }

        // 許可されていない権限のリストをもう一度取得する
        String[] reDeniedPermissions = getDeniedPermissions(this);
        // 許可されていない権限があれば処理を抜ける
        if (deniedPermissions.length > 0) {
            return;
        } else {
            startLocationUpdates();
        }

        // とりあえず権限がなくても、起動はさせる


        runningStatus = false;
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        // 位置情報を管理しているクラスをインスタンス化する
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        if (locationManager != null) {
            locationManager.registerGnssStatusCallback(gnssStatusCallback);
        }
    }

    /**
     * 必要な権限のうち、許可されていない権限のリスト（配列）を返す。
     * @return
     */
    private String[] getDeniedPermissions(Context context) {

        List<String> deniedPermissionList = new ArrayList<String>();

        // 精度の高い位置情報を取得する権限を持っているか確認する
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            // 持っていなければリクエストのリストに追加する
            deniedPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // おおまかな位置情報を取得する権限を持っているか確認する
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            // 持っていなければリクエストのリストに追加する
            deniedPermissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        return deniedPermissionList.toArray(new String[deniedPermissionList.size()]);

    }

    /**
     * 位置情報が変更されたときに呼び出されるコールバックメソッド
     * @param location
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    private GnssStatus.Callback gnssStatusCallback = new GnssStatus.Callback() {

        @Override
        public void onStarted() {
            // GNSS受信機の状態の取得が開始された時の処理
            super.onStarted();
        }

        @Override
        public void onStopped() {
            // GNSS受信機の状態の取得が停止された時の処理
            super.onStopped();
        }

        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            int satelliteCount = status.getSatelliteCount();
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();

            //sb.append("衛星数：").append(String.valueOf(satelliteCount)).append(System.getProperty("line.separate"));
            sb1.append("衛星数：").append(String.valueOf(satelliteCount)).append(System.lineSeparator());

            for (int i = 0; i < satelliteCount; i++) {
                // sb.append(String.format("%2d", i)).append(":");

                // 最新の位置情報取得に使用したか？
                if (status.usedInFix(i)) {
                    sb1.append("* ");

                    // 衛星の種類
                    //sb1.append(status.getConstellationType(i)).append(" ");
                    switch (status.getConstellationType(i)) {
                        case 1: sb1.append("GPS  "); break;
                        case 2: sb1.append("SBA  "); break;
                        case 3: sb1.append("GLO  "); break;
                        case 4: sb1.append("QZS  "); break;
                        case 5: sb1.append("BEI  "); break;
                        case 6: sb1.append("GAL  "); break;
                        case 7: sb1.append("IRN  "); break;
                        default: sb1.append("UNK  "); break;
                    }
                    // 衛星の識別番号？
                    sb1.append(String.format("%3d", status.getSvid(i))).append(" ");
                    // 衛星が発する信号の搬送波周波数
                    sb1.append(String.format("% 7.2f MHz", status.getCarrierFrequencyHz(i) / 1000 / 1000)).append(" ");
                    // 衛星のアンテナにおける搬送波対雑音密度（dB-Hz 単位）
                    sb1.append(String.format("% 5.1f dB-Hz", status.getCn0DbHz(i))).append(" ");
                    // 衛星に天体暦データがあるか？
                    if (status.hasEphemerisData(i)) {
                        sb1.append("*  ");
                    } else {
                        sb1.append("   ");
                    }
                    sb1.append(System.lineSeparator());

                } else {
                    sb2.append("  ");

                    // 衛星の種類
                    //sb2.append(status.getConstellationType(i)).append(" ");
                    switch (status.getConstellationType(i)) {
                        case 1: sb2.append("GPS  "); break;
                        case 2: sb2.append("SBA  "); break;
                        case 3: sb2.append("GLO  "); break;
                        case 4: sb2.append("QZS  "); break;
                        case 5: sb2.append("BEI  "); break;
                        case 6: sb2.append("GAL  "); break;
                        case 7: sb2.append("IRN  "); break;
                        default: sb2.append("UNK  "); break;
                    }
                    // 衛星の識別番号？
                    sb2.append(String.format("%3d", status.getSvid(i))).append(" ");
                    // 衛星が発する信号の搬送波周波数
                    sb2.append(String.format("% 7.2f MHz", status.getCarrierFrequencyHz(i) / 1000 / 1000)).append(" ");
                    // 衛星のアンテナにおける搬送波対雑音密度（dB-Hz 単位）
                    sb2.append(String.format("% 5.1f dB-Hz", status.getCn0DbHz(i))).append(" ");
                    // 衛星に天体暦データがあるか？
                    if (status.hasEphemerisData(i)) {
                        sb2.append("*  ");
                    } else {
                        sb2.append("   ");
                    }
                    sb2.append(System.lineSeparator());
                }
            }
            // 画面に表示
            mainTextView.setText(sb1.toString() + sb2.toString());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

}