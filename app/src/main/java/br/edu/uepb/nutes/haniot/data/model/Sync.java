package br.edu.uepb.nutes.haniot.data.model;

public class Sync {

    private boolean was_sync;

    public Sync() {
        this.was_sync = false; // inicializando não sincronizado
    }

    public boolean isSync() {
        return was_sync;
    }

    public void setSync(boolean was_sync) {
        this.was_sync = was_sync;
    }
}
