package com.example.fit_20clc_hcmus_android_final_project.data_struct;

import java.util.List;

public class AlarmNotification {

    private List<String> changes;

    public AlarmNotification()
    {
        changes = null;
    }

    public AlarmNotification(List<String> changes) {
        this.changes = changes;
    }


    public List<String> getChanges() {
        return changes;
    }

    public void setChanges(List<String> changes) {
        this.changes = changes;
    }
}
