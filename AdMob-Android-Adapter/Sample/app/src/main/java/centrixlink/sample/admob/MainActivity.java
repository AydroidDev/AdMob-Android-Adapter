package centrixlink.sample.admob;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.centrixlink.SDK.AdConfig;
import com.centrixlink.mediationAdapter.CentrixlinkAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RewardedVideoAd mAd;
//    private static final String mAdUnitIdRewardBased = "ca-app-pub-2798310708890891/5463711107";
    private static final String mAdUnitIdRewardBased = "ca-app-pub-2798310708890891/9632678871";
    private Button showRewardedVideoAdButton;
    private ScrollView scrollView;
    private TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showRewardedVideoAdButton = ((Button) this.findViewById(R.id.show));
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        logTextView = (TextView) findViewById(R.id.logContent);

        // Check Google Play Services availability
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);
        apiAvailability.showErrorDialogFragment(this, errorCode, 0);

        //RewardedVideoAd init
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(mRewardedVideoAdListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAd != null){
            mAd.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAd != null){
            mAd.pause();
        }
    }

    private void log(CharSequence text) {
        Log.d(TAG, text.toString());

        if (logTextView.length() > 0)
            logTextView.append("\n");
        logTextView.append(text);

        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 500);
    }

    private String getVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getVersion failed", e);
            return "";
        }
    }

    private String getGooglePlayServicesVersion() {
        try {
            return getPackageManager()
                    .getPackageInfo(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0)
                    .versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getGooglePlayServicesVersion failed", e);
            return "";
        }
    }

    private Bundle getNetworkExtras() {
        Bundle extras = null;

        //you can compose your customize AdConfig here and put
        AdConfig config = new AdConfig();
        config.setOptionKeyUser(this.getPackageName());

        extras = new Bundle();
        extras.putSerializable(CentrixlinkAdapter.KEY_CUSTOM_AD_CONFIG, config);

        return extras;
    }

    public void load(View view) {
        log("Loading reward-based ad…");
        Bundle extras = getNetworkExtras();
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(CentrixlinkAdapter.class, extras)
                .build();
        showRewardedVideoAdButton.setText(R.string.ad_loading);
        showRewardedVideoAdButton.setEnabled(false);
        mAd.loadAd(mAdUnitIdRewardBased, adRequest);
    }

    public void show(View view) {
        log("Showing reward-based ad…");
        mAd.show();
    }

    public void loadInterstitial(View view) {
//        if (iAd.isLoading()) {
//            log("An interstitial is already loading.");
//            return;
//        }
//        if (iAd.isLoaded()) {
//            log("An interstitial is already loaded.");
//            return;
//        }
//        log("Loading interstitial ad…");
//        Bundle extras = getNetworkExtras();
//        AdRequest adRequest = new AdRequest.Builder()
//                .addNetworkExtrasBundle(VungleAdapter.class, extras)
//                .build();
//        showInterstitialAdButton.setText(R.string.ad_loading);
//        showInterstitialAdButton.setEnabled(false);
//        iAd.loadAd(adRequest);

        new myAsyncTask().execute();
    }


    private class myAsyncTask extends AsyncTask<Object,Integer,String>{

        @Override
        protected String doInBackground(Object... objects) {

            try {
                AdvertisingIdClient.Info info = AdvertisingIdClient.getAdvertisingIdInfo(MainActivity.this);
                if(info != null){
                   return info.getId();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            log(s);
        }
    }

    private RewardedVideoAdListener mRewardedVideoAdListener = new RewardedVideoAdListener() {
        @Override
        public void onRewardedVideoAdLeftApplication() {
            log("onRewardedVideoAdLeftApplication");

            log("ad clicked");

        }

        @Override
        public void onRewardedVideoAdClosed() {
            log("onRewardedVideoAdClosed");
//            showRewardedVideoAdButton.setText(R.string.ad_shown);
//            showRewardedVideoAdButton.setEnabled(false);
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int errorCode) {
            log("onRewardedVideoAdFailedToLoad: error " + errorCode);
//            showRewardedVideoAdButton.setText(R.string.ad_failed);
//            showRewardedVideoAdButton.setEnabled(false);

            showRewardedVideoAdButton.setText(R.string.ad_show);
            showRewardedVideoAdButton.setEnabled(true);
        }

        @Override
        public void onRewardedVideoAdLoaded() {
            log("onRewardedVideoAdLoaded");
            showRewardedVideoAdButton.setText(R.string.ad_show);
            showRewardedVideoAdButton.setEnabled(true);

        }

        @Override
        public void onRewardedVideoAdOpened() {
            log("onRewardedVideoAdOpened");
        }

        @Override
        public void onRewarded(RewardItem reward) {
            log("onRewarded! currency: " + reward.getType() + ", amount: " + reward.getAmount());
        }

        @Override
        public void onRewardedVideoStarted() {
            log("onRewardedVideoStarted");
        }
    };

}
