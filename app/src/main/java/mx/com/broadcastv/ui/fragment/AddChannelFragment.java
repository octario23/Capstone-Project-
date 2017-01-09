package mx.com.broadcastv.ui.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

import mx.com.broadcastv.R;
import mx.com.broadcastv.model.AddChannel;
import mx.com.broadcastv.model.AddChannelList;
import mx.com.broadcastv.model.Request;
import mx.com.broadcastv.model.Token;
import mx.com.broadcastv.ui.MainListActivity;
import mx.com.broadcastv.util.AsyncTaskHelper;
import mx.com.broadcastv.util.Utils;
import mx.com.broadcastv.util._Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddChannelFragment extends Fragment implements View.OnClickListener {

    public static final String FRAGMENT_TAG = AddChannelFragment.class.getSimpleName();
    private static final String TOKEN = "token";
    private View rootView;
    private Toolbar toolbar;
    private FrameLayout rootLayout;
    private ActionBar actionBar;
    private FloatingActionButton mFab;
    private EditText channelName;
    private EditText channelUrl;
    private Button addButton;
    private ProgressDialog progress;
    private String token;

    public AddChannelFragment() {
        // Required empty public constructor
    }

    public static AddChannelFragment newInstance(Bundle args) {
        AddChannelFragment fragment = new AddChannelFragment();
        if (args!=null){
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            token = getArguments().getString(TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView            = inflater.inflate(R.layout.fragment_add_channel, container, false);
        toolbar             = (Toolbar)         getActivity().findViewById(R.id.toolbar);
        rootLayout          = (FrameLayout)     getActivity().findViewById(R.id.rootLayout);
        mFab                = (FloatingActionButton)     getActivity().findViewById(R.id.mFab);
        channelName         = (EditText)        rootView.findViewById(R.id.channelName);
        channelUrl          = (EditText)        rootView.findViewById(R.id.channelUrl);
        addButton           = (Button)        rootView.findViewById(R.id.addChannelBtn);
        actionBar           = ((AppCompatActivity)getActivity()).getSupportActionBar();
        toolbar.setTitleTextColor(Color.WHITE);
        addButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootLayout.setPadding(0, 0, 0, 0);
        ColorDrawable background = (ColorDrawable) toolbar.getBackground();
        background.setAlpha(0);
        background.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        restablishActionBar();
        mFab.setVisibility(View.VISIBLE);
    }

    public void restablishActionBar() {
        rootLayout.setPadding(0, toolbar.getHeight(), 0, 0);
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_white_1000));
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.getBackground().setAlpha(255);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if(v instanceof Button) {
            AddChannelList channelListObj = new AddChannelList();
            AddChannel channel = new AddChannel();
            List<AddChannel> channelList = new ArrayList<AddChannel>();
            final Context ctx = getActivity();
            channel.setChannelName(channelName.getText().toString());
            channel.setChannelURL(channelUrl.getText().toString());
            channelList.add(channel);
            channelListObj.setToken(token);
            channelListObj.setChannelList(channelList);
            final Request<AddChannelList> request = new Request<AddChannelList>();
            request.setRqt(channelListObj);
            progress = Utils.broadcastvLoading(getActivity());
            progress.show();
            new AsyncTaskHelper((Activity) ctx, request,(new _Callback(){
                @Override
                public void execute(JsonNode response, Context context) {
                    if(response.get("Success")!= null && response.get("Success").asBoolean() == true) {
                        progress.hide();
                        Toast.makeText(ctx,
                                context.getResources().getString(R.string.channel_added),Toast.LENGTH_SHORT).show();
                    }else{
                        progress.hide();
                        Toast.makeText(ctx,
                                context.getResources().getString(R.string.error_network),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void Failure(JsonNode response, Context context) {
                    Log.e(FRAGMENT_TAG, String.valueOf(response));
                    progress.hide();
                }

                @Override
                public HttpEntity<?> setHeader(Request request, HttpHeaders headers) {
                    HttpEntity<?> requestEntity = new HttpEntity<Request>(request,headers);
                    return requestEntity;
                }
            }),"/RegistrarCanal").execute();
        }
    }
}
