package games.jsheriff.ballphysics20;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;

public class Stage extends Activity{

    private GameView myGame;
    AdView adView;
    public final String MYID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        myGame = new GameView(this);
        this.setContentView(myGame);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        myGame.pause();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        myGame.resume();
        if (adView != null) {
            adView.resume();
        }
    }


}
