package com.dyadav.chirpntweet.fragments;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.databinding.VideoFragmentBinding;

public class VideoDialog extends DialogFragment {
    private VideoFragmentBinding binding;
    String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.video_fragment, container, false);

        //Fetch video URL
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString("url");
        }

        if (url != null) {
            MediaController videoControl = new MediaController(getContext());
            videoControl.setAnchorView(binding.videoView);
            videoControl.setMediaPlayer(binding.videoView);
            binding.videoView.setMediaController(videoControl);
            Uri videoUri = Uri.parse(url);
            binding.videoView.setVideoURI(videoUri);
            binding.videoView.start();
        } else {
            dismiss();
        }
        return binding.getRoot();
    }
}
