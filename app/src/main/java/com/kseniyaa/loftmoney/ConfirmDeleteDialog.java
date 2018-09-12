package com.kseniyaa.loftmoney;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class ConfirmDeleteDialog extends DialogFragment {

    private Listener listener;

    public void setListener(Listener listener){
        this.listener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage(R.string.item_del_dialog_msg)
                .setPositiveButton(R.string.item_del_dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onDeleteConfirmed();
                        }
                    }
                })
                .setNegativeButton(R.string.item_del_dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
    }

    interface Listener {
        void onDeleteConfirmed();
    }
}

