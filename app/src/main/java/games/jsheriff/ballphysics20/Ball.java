package games.jsheriff.ballphysics20;

import android.graphics.Color;
import android.util.Log;

import java.util.Random;

/**
 * Created by jaafe on 12/14/2015.
 */
public class Ball {

    GameView gameView;

    //Dimensions
    float x;
    float y;
    int radius;

    //appearance
    int color;

    //physics
    double dx = 0;
    double dy = 0;
    double energyLoss = 0.65;
    double dt = 0.2;

    private Random r = new Random();

    public Ball(float x, float y, GameView gameView)
    {
        this.x = x;
        this.y = y;
        this.gameView = gameView;
        this.radius = r.nextInt(200 - 50 + 1) + 50;
        color = Color.argb(255, r.nextInt(), r.nextInt(), r.nextInt());
    }

    public void update(float[] gravity)
    {
        applyGravity(gravity);
    }

    private void applyGravity(float[] gravity)
    {
        float xgrav = gravity[0]*5;
        float ygrav = gravity[1]*3;

        //check bounds
        if(y >= gameView.getHeight() - radius + 1)
        {
            y = gameView.getHeight() - radius;
            dy *= -energyLoss;
        }
        if(y <= 0 + radius - 1)
        {
            y = 0 + radius;
            dy *= -energyLoss;
        }
        if(x >= gameView.getWidth() - radius + 1)
        {
            x = gameView.getWidth() - radius;
            dx *= -energyLoss;
        }
        if(x <= 0 + radius - 1)
        {
            x = 0 + radius;
            dx *= -energyLoss;
        }

        //physics
        dy = dy + ygrav*dt;
        y += dy*dt + 0.5*ygrav*dt*dt;
        dx = dx - xgrav*dt;
        x += dx*dt - 0.5*xgrav*dt*dt;
    }


    public void paint() {
        gameView.paint.setColor(color);
        gameView.canvas.drawCircle(x, y, radius, gameView.paint);
        /*
        gameView.paint.setColor(Color.BLACK);
        gameView.paint.setTextSize(35);
        gameView.canvas.drawText("X: "+ String.format("%.2f", x-radius), x-8, y-15, gameView.paint);
        gameView.canvas.drawText("Y: "+ String.format("%.2f", y-radius), x-8, y+15, gameView.paint);
        */
    }
}
