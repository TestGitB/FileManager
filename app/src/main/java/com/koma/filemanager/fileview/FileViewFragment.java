package com.koma.filemanager.fileview;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koma.filemanager.R;
import com.koma.filemanager.base.BaseFile;
import com.koma.filemanager.base.BaseFragment;
import com.koma.filemanager.util.Constants;
import com.koma.filemanager.util.FileCategoryUtils;
import com.koma.filemanager.util.LogUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by koma on 12/1/16.
 */

public class FileViewFragment extends BaseFragment implements FileViewContract.View {
    private static final String TAG = "FileViewFragment";
    @NonNull
    private FileViewContract.Presenter mPresenter;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private FileViewAdapter mAdapter;
    private ArrayList<BaseFile> mData;
    private String mPath;

    private final ContentObserver mFileObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            LogUtils.i(TAG, "File uri change so refresh");
            if (mPresenter != null) {
            }
        }
    };

    public static FileViewFragment newInstance(String path) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_FILE_DIRECTORY, path);
        FileViewFragment fileViewFragment = new FileViewFragment();
        fileViewFragment.setArguments(bundle);
        return fileViewFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtils.i(TAG, "onAttach");
        mPath = ((FileViewActivity) getActivity()).getPath();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(TAG, "onCreate");
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPath = bundle.getString(Constants.EXTRA_FILE_DIRECTORY);
        }
        mContext.getContentResolver().registerContentObserver(FileCategoryUtils.getFileUri(),
                true, mFileObserver);
        mData = new ArrayList<>();
        mAdapter = new FileViewAdapter(mContext, mData);
        if (mPresenter != null) {
            mPresenter.subscribe();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.i(TAG, "onViewCreated");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.i(TAG, "onResume");
        if (mPresenter != null) {
            LogUtils.i(TAG, "onResume mPath :" + mPath);
            mPresenter.getFiles(mPath);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.i(TAG, "onPause");
    }

    @Override
    public void refreshAdapter(ArrayList<BaseFile> files) {
        LogUtils.i(TAG, "refreshAdapter" + files.size());
        if (mData != null) {
            mData.clear();
        } else {
            mData = new ArrayList<>();
        }
        mData.addAll(files);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoadingView() {
        LogUtils.i(TAG, "showLoadingView");
    }

    @Override
    public void showEmptyView() {
        LogUtils.i(TAG, "showEmptyView");

    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public void setPresenter(@NonNull FileViewContract.Presenter presenter) {
        LogUtils.i(TAG, "setPresenter");
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "onDestroy");
        mContext.getContentResolver().unregisterContentObserver(mFileObserver);
        if (mPresenter != null) {
            mPresenter.unSubscribe();
        }
    }
}