package com.dyadav.chirpntweet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.modal.Drafts;

import java.util.List;

public class DraftsArrayAdapter extends ArrayAdapter<Drafts> {
    Context mContext;
    List<Drafts> mDrafts;

    public DraftsArrayAdapter(Context context, List<Drafts> drafts) {
        super(context, 0, drafts);
        mDrafts = drafts;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Drafts draft = mDrafts.get(position);

        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.items_draft, parent, false);
        }

        TextView tvDraft = (TextView) convertView.findViewById(R.id.itemDraft);
        tvDraft.setText(draft.getDraft());

        return convertView;
    }
}
