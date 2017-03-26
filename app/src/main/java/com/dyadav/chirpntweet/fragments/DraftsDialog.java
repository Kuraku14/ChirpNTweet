package com.dyadav.chirpntweet.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.adapter.DraftsArrayAdapter;
import com.dyadav.chirpntweet.databinding.DraftsFragmentBinding;
import com.dyadav.chirpntweet.modal.Drafts;
import com.dyadav.chirpntweet.modal.Drafts_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class DraftsDialog extends DialogFragment {

    private DraftsFragmentBinding binding;
    DraftsArrayAdapter mAadapter;

    public interface DraftsListener {
        void onFinishDialog(String draft);
    }

    private DraftsListener mListener;

    public void setFinishDialogListener(DraftsListener listener) {
        mListener = listener;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.drafts_fragment, container, false);

        //Fetch drafts from DB and display
        final List<Drafts> drafts = SQLite.select(Drafts_Table.draft).from(Drafts.class).orderBy(Drafts_Table.id, false).queryList();
        mAadapter = new DraftsArrayAdapter(getContext(), drafts);
        binding.listDrafts.setAdapter(mAadapter);

        //OnClick use draft
        binding.listDrafts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Send text back to Compose view
                mListener.onFinishDialog(drafts.get(i).getDraft());
                //Delete the draft from DB
                SQLite.delete(Drafts.class).where(Drafts_Table.id.eq(drafts.get(i).getId())).async()
                        .execute();
                drafts.remove(i);
                mAadapter.notifyDataSetChanged();
                dismiss();
            }
        });

        //Long press delete draft
        binding.listDrafts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                //Delete the draft from DB and update the listview
                SQLite.delete(Drafts.class).where(Drafts_Table.id.eq(drafts.get(pos).getId())).async()
                        .execute();
                drafts.remove(pos);
                mAadapter.notifyDataSetChanged();
                return true;
            }
        });

        return binding.getRoot();
    }
}
