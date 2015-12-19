package games.jsheriff.ballphysics20;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.ads.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;

class GameView extends SurfaceView implements Runnable, SensorEventListener
{
    //Game stuff
    Thread gameThread = null;
    volatile boolean isRunning;

    //draw stuff
    SurfaceHolder ourHolder;
    Canvas canvas;
    Paint paint;

    //Fps stuff
    long fps;
    private long timeThisFrame;

    //user stuff
    float Px = 0;
    float Py = 0;

    //ball stuff
    ArrayList<Ball> balls = new ArrayList<Ball>();

    //sensor stuff
    SensorManager sm;
    Sensor accelerometer;
    //0 = south = -y
    //1 = west = -x
    //2 = east = +x
    //3 = north = +y
    float[] gravity = {-1, -1, -1};

    Context cx;

    public GameView(Context context)
    {
        super(context);
        //paint stuff
        cx = context;
        ourHolder = getHolder();
        paint = new Paint();

        //sensor stuff
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //TYPE_GRAVITY?
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //Game engine
    @Override
    public void run()
    {
        while(isRunning)
        {
            long startTime = System.currentTimeMillis();

            update();
            draw();

            //fps`
            timeThisFrame = System.currentTimeMillis() - startTime;
            if(timeThisFrame > 0) fps = 1000/timeThisFrame;
        }
    }


    //Update virtual world
    public void update()
    {
        for(int i = 0; i < balls.size(); i++) balls.get(i).update(gravity);
    }

    //Draw virtual world
    public void draw()
    {
        //make sure our surface is valid
        if(!ourHolder.getSurface().isValid()) return;
        //Lock canvas to draw, make drawing surface our canvas
        canvas = ourHolder.lockCanvas();

        //Draw background
        canvas.drawColor(Color.argb(255, 26, 128, 182));

        //Draw balls
        for(int i = 0; i < balls.size(); i++) balls.get(i).paint();

        //Text
        paint.setColor(Color.argb(255, 249, 129, 0));
        paint.setTextSize(45);

        if(balls.size() == 0) canvas.drawText("Press to begin", 400, 500, paint);
        else canvas.drawText("# Balls: " + balls.size(), 400, 50, paint);

        canvas.drawText("FPS: " + fps, 20, 50, paint);

        canvas.drawText("RESET", 900, 50, paint);

        paint.setTextSize(35);
        for(int j = 0; j < 2; j++) drawTilt(j);

        //ad block
        paint.setColor(Color.BLACK);
        //canvas.drawRect(0, this.getHeight() - 200, this.getWidth(), this.getHeight(), paint);

        //draw everything to the screen
        ourHolder.unlockCanvasAndPost(canvas);
    }

    private void drawTilt(int index)
    {
        int pos = 0;
        while (pos <= Math.abs(gravity[index]*10)) pos++;

        if(index == 0)
        {
            if(gravity[index] > 0)
            {
                pos = -pos;
                paint.setColor(Color.WHITE);
                canvas.drawRect(450+pos, 140, 450, 160, paint);
            }
            else
            {
                paint.setColor(Color.BLACK);
                canvas.drawRect(450, 140, 450+pos, 160, paint);
            }
            canvas.drawText("X: " + String.format("%.2f", -gravity[index]), 450 + pos, 135, paint);
        }
        if(index == 1)
        {
            if(gravity[index] < 0)
            {
                pos = -pos;
                paint.setColor(Color.WHITE);
                canvas.drawRect(440, 150+pos, 460, 150, paint);
            }
            else
            {
                paint.setColor(Color.BLACK);
                canvas.drawRect(440, 150, 460, 150+pos, paint);
            }
            canvas.drawText("Y: " + String.format("%.2f", gravity[index]), 400, 180+pos, paint);
        }
    }


    //if game engine is paused/stopped
    public void pause()
    {
        isRunning = false;
        sm.unregisterListener(this);
        try
        {
            gameThread.join();
        }
        catch(InterruptedException e) { Log.e("Error: ", "joining thread"); }
    }
    public void resume()
    {
        isRunning = true;
        gameThread = new Thread(this);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        gameThread.start();
    }

    //TouchEvents overriden from surfaceview
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK)
        {
            //user touched the screen
            case MotionEvent.ACTION_DOWN:
                Px = motionEvent.getX();
                Py = motionEvent.getY();
                if(875 < Px && Px < 1200 && Py > 0 && Py < 140) balls.clear();
                else balls.add(new Ball(Px, Py, this));
                break;

            //user released the screen
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        this.gravity = sensorEvent.values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}