package com.gradeprediction.server;

public interface ServerObserver {
    // send a notification to application to update progress bar from server
    void updateProgressBar(double progress, String school);
}
