package vandy.mooc.gcd.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vandy.mooc.gcd.R;
import vandy.mooc.gcd.utils.UiUtils;

/**
 * Main activity for an app that shows various Thread methods and
 * alternative ways of giving code to a Java thread.
 */
public class MainActivity 
       extends LifecycleLoggingActivity {
    /** 
     * A TextView used to display the output.
     */
    private TextView mTextViewLog;

    /** 
     * A ScrollView that contains the results of the TextView.
     */
    private ScrollView mScrollView;

    /**
     * Hook method called when the activity is first launched.
     */
    protected void onCreate(Bundle savedInstanceState) {
        // Call up to the super class to perform initializations.
        super.onCreate(savedInstanceState);

        // Sets the content view to the xml file.
        setContentView(R.layout.main_activity);

        // Store and initialize the TextView and ScrollView.
        mTextViewLog =
            (TextView) findViewById(R.id.text_output);
        mScrollView =
            (ScrollView) findViewById(R.id.scrollview_text_output);
        /*
        mScrollView.fullScroll(ScrollView.FOCUS_UP);
        mTextViewLog.setText(R.string.empty_string);
        */
    }

    /**
     * Method called back when the "run runnable" button is pressed.
     */
    public void runRunnable(View v) {
        // Create the GCD Runnable.
        GCDRunnable runnableCommand =
            new GCDRunnable(this);

        // Create a new Thread that's will execute the runnableCommand
        // concurrently.
        Thread thread = new Thread(runnableCommand);

        // Start the thread.
        thread.start();
    }

    /**
     * Method called back when the "run thread" button is pressed.
     */
    public void runThread(View v) {
        // Create the GCDThread.
        Thread thread = new GCDThread(this);

        // Start the thread.
        thread.start();
    }

    /**
     * Method called back when the "run thread and runnable" button is
     * pressed.
     */
    public void runThreadAndRunnable(View v) {
        // Create a list of thread.
        List<Thread> threadList = new ArrayList<>();

        // Create and add the GCDThread;
        threadList.add(new GCDThread(this));

        // Create and add a new thread that's will run the
        // GCDRunnable.
        threadList.add(new Thread(new GCDRunnable(this)));

        // Use Java 8 forEach() to start each thread.
        threadList.forEach(Thread::start);

        // Create and start a thread to wait for the other threads to
        // finish.
        new Thread(() -> { 
                // Use Java 8 forEach() to wait for all threads in the
                // list to finish.
                threadList.forEach(thread -> {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            Log.d(TAG, "thread interrupted");
                        }
                    });

                // Print a diagnostic message.
                println("All threads are joined in thread "
                        + Thread.currentThread());
        }).start();
    }

    /**
     * Append @a stringToPrint to the scrolling text view.
     */
    public void println(String stringToPrint) {
        // Create a command to print the results.
        Runnable command = () -> {
            // Append the stringToPrint and terminate it with a
            // newline.
            mTextViewLog.append(stringToPrint + "\n");
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        };

        // Optimize for the case where println() is called from the UI
        // thread.
        if (UiUtils.runningOnUiThread())
            command.run();
        else 
            // Run the command on the UI thread.
            runOnUiThread(command);
    }
}
