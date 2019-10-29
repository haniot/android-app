package br.edu.uepb.nutes.haniot.data.objectbox;

import io.objectbox.annotation.BaseEntity;

@BaseEntity
public class SyncOB {

    private boolean sync;

    public SyncOB() {
        this.sync = false; // inicializando não sincronizado
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean was_sync) {
        this.sync = was_sync;
    }
}