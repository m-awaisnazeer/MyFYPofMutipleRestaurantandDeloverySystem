package com.comunisolve.newmultiplerestaurantsapp.EventBus;

public class LoadingDialogClosingEvent {
    public boolean  dialogclose;

    public LoadingDialogClosingEvent(boolean dialogclose) {
        this.dialogclose = dialogclose;
    }

    public boolean isDialogclose() {
        return dialogclose;
    }

    public void setDialogclose(boolean dialogclose) {
        this.dialogclose = dialogclose;
    }
}
