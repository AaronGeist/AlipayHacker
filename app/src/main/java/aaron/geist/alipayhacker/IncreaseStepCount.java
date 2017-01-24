package aaron.geist.alipayhacker;

import java.util.Random;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * This class is to upload fake step count to server.
 * <p>
 * Created by Aaron on 2016/12/29.
 */

public class IncreaseStepCount implements IXposedHookLoadPackage {

    private static final String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";

    private void log(String msg) {
        XposedBridge.log(msg);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (lpparam.packageName.equals(ALIPAY_PACKAGE_NAME)) {

            // this function is to increase step counting significantly
            findAndHookMethod("com.alipay.mobile.healthcommon.stepcounter.APMainStepManager", lpparam.classLoader, "a", String.class, int.class, int.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    // steps already in server side
                    int serverSteps = (int) param.args[2];

                    if (serverSteps < 10000) {
                        serverSteps = 50000;
                    }

                    // steps to increase
                    int newSteps = (int) param.args[3];

                    // add random to avoid zero in low bit
                    Random r = new Random();
                    int rndSteps = r.nextInt(1000);

                    log("s=" + serverSteps + ", n=" + newSteps + ", r=" + rndSteps);

                    // args[1] is the total steps to be returned to server
                    param.args[1] = serverSteps + newSteps * 100 + rndSteps;
                }
            });
        }
    }
}
