package info.scelus.dotsandboxes;

import android.app.Application;
import android.graphics.Typeface;

import info.scelus.dotsandboxes.utils.Globals;

/**
 * Created by SceLus on 15/10/2014.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Globals.kgTrueColors = Typeface.createFromAsset(getAssets(), "fonts/KGTrueColors.ttf");
    }
}
