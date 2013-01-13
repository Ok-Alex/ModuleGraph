package me.akulakovsky.stanfy.graph;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import me.akulakovsky.stanfy.graph.views.VerticalProgressBar;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MyActivity extends SherlockActivity {
    private VerticalProgressBar barInternal;
    private VerticalProgressBar barExternal;
    private VerticalProgressBar barRAM;

    private TextView tvInternal;
    private TextView tvExternal;
    private TextView tvRAM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memory_gaph_activity);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillViews();
    }

    private void fillViews(){
        File system = Environment.getDataDirectory();
        int totalSys = (int) getSpace(system);
        int freeSys = (int) getFreeSpace(system);
        int currentSys = (100 * freeSys)/totalSys;
        tvInternal.setText(freeSys + " / " + totalSys);
        barInternal.setMax(100);
        barInternal.setProgress(100 - currentSys);

        File sd = Environment.getExternalStorageDirectory();
        int totalSd = (int) getSpace(sd);
        int freeSd = (int) getFreeSpace(sd);
        int currentSd = (100 * freeSd)/totalSd;
        tvExternal.setText(freeSd + " / " + totalSd);
        barExternal.setMax(100);
        barExternal.setProgress(100 - currentSd);

        int totalRam = (int) getTotalRAM();
        int freeRam = (int) getRAM();
        int currentRam = (100 * freeRam)/totalRam;
        tvRAM.setText(freeRam + " / " + totalRam);
        barRAM.setMax(100);
        barRAM.setProgress(100 - currentRam);
    }

    private void initViews(){
        barInternal = (VerticalProgressBar) findViewById(R.id.internal_bar);
        barExternal = (VerticalProgressBar) findViewById(R.id.external_bar);
        barRAM = (VerticalProgressBar) findViewById(R.id.ram_bar);

        tvInternal = (TextView) findViewById(R.id.internal_memory_total);
        tvExternal = (TextView) findViewById(R.id.external_memory_total);
        tvRAM = (TextView) findViewById(R.id.ram_memory_total);
    }

    private double getSpace(File file){
        StatFs stat = new StatFs(file.getPath());
        long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();
        long megAvailable = bytesAvailable / 1048576;

        return megAvailable;
    }

    private double getFreeSpace(File path){
        //File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        String internalSpace = Formatter.formatFileSize(this, availableBlocks * blockSize);
        internalSpace = internalSpace.replace(",", ".");
        internalSpace = internalSpace.replace(internalSpace.charAt(internalSpace.length()-1) + "", "");
        internalSpace = internalSpace.replace(internalSpace.charAt(internalSpace.length()-1) + "", "");

        return Double.parseDouble(internalSpace);
    }

    private long getRAM(){
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        long availableMegs = mi.availMem / 1048576L;
        return availableMegs;
    }

    public static long getTotalRAM() {
        RandomAccessFile reader = null;
        String load = null;
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Streams.close(reader);
        }
        load = load.replace(" kB", "");
        load = load.replace("MemTotal:", "");
        load = load.replace(" ", "");

        return Long.parseLong(load) / 1024;
    }
}
