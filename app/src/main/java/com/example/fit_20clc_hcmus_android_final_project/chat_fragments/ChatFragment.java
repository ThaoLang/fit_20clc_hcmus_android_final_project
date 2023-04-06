package com.example.fit_20clc_hcmus_android_final_project.chat_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fit_20clc_hcmus_android_final_project.ChatActivity;
import com.example.fit_20clc_hcmus_android_final_project.adapter.ChatAdapter;
import com.example.fit_20clc_hcmus_android_final_project.databinding.FragmentChatBinding;

public class ChatFragment extends Fragment implements ChatAdapter.Callbacks{
    private FragmentChatBinding binding;

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private ChatActivity chat_activity;
    private Context context;

    LinearLayoutManager mLinearLayoutManager;
    ChatAdapter adapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

        try {
            context = getActivity();
            chat_activity = (ChatActivity) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("ChatActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);

        adapter = new ChatAdapter(context);
        adapter.setListener(this);

        binding.listItem.setLayoutManager(mLinearLayoutManager);

        binding.listItem.setAdapter(adapter);
        binding.listItem.smoothScrollToPosition(0);

        return binding.getRoot();
    }

    public void swapToFriend(){ //TODO: pass over friend user
        chat_activity.switchScreenByScreenType(1);
    }
}