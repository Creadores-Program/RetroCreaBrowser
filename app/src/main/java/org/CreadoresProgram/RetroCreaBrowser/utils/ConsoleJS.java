package org.CreadoresProgram.RetroCreaBrowser.utils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

import org.CreadoresProgram.RetroCreaBrowser.R;
import org.CreadoresProgram.WebViewCREA.WebViewCreaClient;

public class ConsoleJS {

    private Queue<String> logQueue;
    private final StringBuilder logBuilder = new StringBuilder();
    private TextView consoleTextView;

    public ConsoleJS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            queueG();
        } else {
            logQueue = new LinkedList<String>();
        }
    }
    private void queueG(){
        logQueue = new ArrayDeque<String>(30);
    }

    public synchronized void appendLog(String message) {
        if (logQueue.size() >= 30) {
            if (logQueue instanceof LinkedList) {
                ((LinkedList<String>) logQueue).removeFirst();
            } else {
                appendLogArrayD(message);
            }
        }
        logQueue.add(message);

        if (consoleTextView != null) {
            updateTextView();
        }
    }
    private synchronized void appendLogArrayD(String message){
        ((ArrayDeque<String>) logQueue).pollFirst();
    }

    private void updateTextView() {
        logBuilder.setLength(0);
        for (String line : logQueue) {
            logBuilder.append(line).append("\n");
        }
        
        final String fullLog = logBuilder.toString();

        if (consoleTextView != null) {
            consoleTextView.post(new Runnable() {
                @Override
                public void run() {
                    if (consoleTextView != null) {
                        consoleTextView.setText(fullLog);
                    }
                }
            });
        }
    }

    @SuppressWarnings("deprecation")
    public void showConsoleDialog(Context context, final WebViewCreaClient viewClient, final WebView view) {
        LinearLayout rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(15, 15, 15, 15);
        ScrollView scrollView = new ScrollView(context);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.FILL_PARENT, 0, 1.0f);
        scrollView.setLayoutParams(scrollParams);
        
        consoleTextView = new TextView(context);
        consoleTextView.setTypeface(Typeface.MONOSPACE);
        consoleTextView.setTextSize(12f);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            consoleTextView.setTextIsSelectable(true);
        }

        scrollView.addView(consoleTextView);
        rootLayout.addView(scrollView);
        LinearLayout inputContainer = new LinearLayout(context);
        inputContainer.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams inputContainerParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inputContainer.setLayoutParams(inputContainerParams);

        final EditText inputJs = new EditText(context);
        inputJs.setHint(R.string.evaljs);
        inputJs.setTextSize(13f);
        inputJs.setSingleLine(true);
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        inputJs.setLayoutParams(editParams);
    
        Button sendButton = new Button(context);
        sendButton.setText(R.string.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = inputJs.getText().toString().trim();
                if (!TextUtils.isEmpty(code) && viewClient != null) {
                    appendLog("> " + code);
                    
                    viewClient.evaluateJavascript(view, code);
                    
                    inputJs.setText("");
                }
            }
        });
        inputContainer.addView(inputJs);
        inputContainer.addView(sendButton);
        rootLayout.addView(inputContainer);
        updateTextView();

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
            .setIcon(android.R.drawable.ic_menu_preferences)
            .setTitle(R.string.console)
            .setView(scrollView)
            .setPositiveButton(R.string.close, null)
            .setCancelable(false);

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                consoleTextView = null;
            }
        });

        builder.show();
    }
}
