package soumyajit.org.nestawaydemo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    ImageView nestawaySplashImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setSplash();
    }

    private void setSplash() {
        nestawaySplashImage=(ImageView)findViewById(R.id.nestaway_image);
        nestawaySplashImage.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.slide_in_right));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nestawaySplashImage.animate().rotation(360).setDuration(3000).start();
            }
        },2000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nestawaySplashImage.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.slide_out_left));
            }
        },6000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getBaseContext(),TenantListScreen.class);
                startActivity(i);
            }
        },6500);
    }
}
