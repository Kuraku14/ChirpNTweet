package com.dyadav.chirpntweet.fragments;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyadav.chirpntweet.R;

public class ComposeDialog extends DialogFragment {

    public ComposeDialog() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_corner_dialog);

        return inflater.inflate(R.layout.compose_fragment, container);

    }

}
