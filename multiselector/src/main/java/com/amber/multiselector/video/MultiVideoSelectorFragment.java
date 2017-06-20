package com.amber.multiselector.video;


import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.amber.multiselector.R;
import com.amber.multiselector.utlis.ScreenUtils;
import com.amber.multiselector.video.adapter.FolderAdapter;
import com.amber.multiselector.video.adapter.VideoGridAdapter;
import com.amber.multiselector.video.bean.VideoFolder;
import com.amber.multiselector.video.bean.VideoInfo;
import com.bumptech.glide.Glide;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by luosiyi on 2017/6/20.
 */

public class MultiVideoSelectorFragment extends Fragment {
    public static final String TAG = "MultiVideoSelectorFragment";

    private ArrayList<VideoFolder> mResultFolder = new ArrayList();
    private ArrayList<String> resultList = new ArrayList();
    private MultiVideoSelectorFragment.Callback mCallback;

    private boolean hasFolderGened = false;
    private VideoGridAdapter mVideoAdapter;
    private FolderAdapter mFolderAdapter;
    private ListPopupWindow mFolderPopupWindow;
    private View mPopupAnchorView;
    private TextView mCategoryText;
    private GridView mGridView;
    private File mTmpFile;

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        private final String[] VIDEO_PROJECTION = new String[]{MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.MIME_TYPE};
        private final String[] TNUMB_COLUMNS = {
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = null;
            if (id == 0) {
                cursorLoader = new CursorLoader(getActivity(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION, null, null, VIDEO_PROJECTION[2] + " DESC");
            } else if (id == 1) {
                cursorLoader = new CursorLoader(getActivity(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION, null, null, VIDEO_PROJECTION[2] + " DESC");
            }

            return cursorLoader;
        }

        private boolean fileExist(String path) {
            return !TextUtils.isEmpty(path) ? (new File(path)).exists() : false;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null && data.getCount() > 0) {
                ArrayList<VideoInfo> videos = new ArrayList<>();
                data.moveToFirst();
                do {
                    VideoInfo video = new VideoInfo();
                    long id = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[0]));
                    video.setId(id);
                    String name = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[1]));
                    video.setDisplayName(name);
                    long dateTime = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[2]));
                    video.setDateAdded(dateTime);
                    String path = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[3]));
                    video.setPath(path);
                    int width = data.getInt(data.getColumnIndexOrThrow(VIDEO_PROJECTION[4]));
                    video.setWidth(width);
                    int height = data.getInt(data.getColumnIndexOrThrow(VIDEO_PROJECTION[5]));
                    video.setHeight(height);
                    long duration = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[6]));
                    video.setDuration(duration);
                    long size = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[7]));
                    video.setSize(size);
                    String mimeType = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[8]));
                    video.setMimeType(mimeType);
//                    Cursor thumbCursor = getActivity().getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, TNUMB_COLUMNS, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id, null, null);
//                    if (thumbCursor.moveToFirst()) {
//                        String thumbPath = thumbCursor.getString(thumbCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
//                        video.setThumbPath(thumbPath);
//                        thumbCursor.close();
//                    }
                    getThumbPath(video,TNUMB_COLUMNS,id);
                    if (fileExist(path)) {
                        if (!TextUtils.isEmpty(name)) {
                            videos.add(video);
                        }

                        if (!hasFolderGened) {
                            File folderFile = (new File(path)).getParentFile();
                            if (folderFile != null && folderFile.exists()) {
                                String fp = folderFile.getAbsolutePath();
                                VideoFolder f = getFolderByPath(fp);
                                if (f == null) {
                                    VideoFolder folder = new VideoFolder();
                                    folder.name = folderFile.getName();
                                    folder.path = fp;
                                    folder.video = video;
                                    List<VideoInfo> videoList = new ArrayList();
                                    videoList.add(video);
                                    folder.videos = videoList;
                                    mResultFolder.add(folder);
                                } else {
                                    f.videos.add(video);
                                }
                            }
                        }
                    }
                } while (data.moveToNext());
                mVideoAdapter.setData(videos);
                if (resultList != null && resultList.size() > 0) {
                   mVideoAdapter.setDefaultSelected(resultList);
                }

                if (!hasFolderGened) {
                    mFolderAdapter.setData(mResultFolder);
                    hasFolderGened = true;
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };
    public void getThumbPath(final VideoInfo video,final String [] TNUMB_COLUMNS,final long id ){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor thumbCursor = getActivity().getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, TNUMB_COLUMNS, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id, null, null);
                if (thumbCursor.moveToFirst()) {
                    String thumbPath = thumbCursor.getString(thumbCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                    video.setThumbPath(thumbPath);
                    thumbCursor.close();
                    handler.sendEmptyMessage(1);
                }
            }
        }).start();
    }
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 1:
                    mVideoAdapter.notifyDataSetChanged();
                    mFolderAdapter.notifyDataSetChanged();
                    break;
            }
            return false;
        }
    });

    private VideoFolder getFolderByPath(String path) {
        if (mResultFolder != null) {
            Iterator var2 = mResultFolder.iterator();

            while (var2.hasNext()) {
                VideoFolder folder = (VideoFolder) var2.next();
                if (TextUtils.equals(folder.path, path)) {
                    return folder;
                }
            }
        }

        return null;
    }

    public MultiVideoSelectorFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (MultiVideoSelectorFragment.Callback) getActivity();
        } catch (ClassCastException var3) {
            throw new ClassCastException("The Activity must implement MultiImageSelectorFragment.Callback interface...");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mis_fragment_multi_video, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final int mode = selectMode();
        if (mode == 1) {
            ArrayList tmp = getArguments().getStringArrayList("default_list");
            if (tmp != null && tmp.size() > 0) {
                resultList = tmp;
            }
        }

        mVideoAdapter = new VideoGridAdapter(getActivity(), 3);
        mVideoAdapter.showSelectIndicator(mode == 1);
        mPopupAnchorView = view.findViewById(R.id.footer);
        mCategoryText = (TextView) view.findViewById(R.id.category_btn);
        mCategoryText.setText(R.string.multi_video_all);
        mCategoryText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mFolderPopupWindow == null) {
                    createPopupFolderList();
                }

                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.show();
                    int index = mFolderAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    mFolderPopupWindow.getListView().setSelection(index);
                }

            }
        });
        mGridView = (GridView) view.findViewById(R.id.grid);
        mGridView.setAdapter(mVideoAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VideoInfo video = (VideoInfo) adapterView.getAdapter().getItem(i);
               selectVideoFromGrid(video, mode);


            }
        });
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_FLING) {
                    Glide.with(view.getContext()).pauseRequests();
                } else {
                    Glide.with(view.getContext()).resumeRequests();
                }

            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        mFolderAdapter = new FolderAdapter(getActivity());
    }

    private void createPopupFolderList() {
        Point point = ScreenUtils.getScreenSize(getActivity());
        int width = point.x;
        int height = (int) (point.y * (4.5f/8.0f));
        mFolderPopupWindow = new ListPopupWindow(getActivity());
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(-1));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);
        mFolderPopupWindow.setHeight(height);
        mFolderPopupWindow.setAnchorView(mPopupAnchorView);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
               mFolderAdapter.setSelectIndex(i);
                (new Handler()).postDelayed(new Runnable() {
                    public void run() {
                        mFolderPopupWindow.dismiss();
                        if (i == 0) {
                           getActivity().getSupportLoaderManager().restartLoader(0, (Bundle) null, mLoaderCallback);
                            mCategoryText.setText(R.string.multi_video_all);

                        } else {
                            VideoFolder folder = (VideoFolder) adapterView.getAdapter().getItem(i);
                            if (null != folder) {
                               mVideoAdapter.setData(folder.videos);
                                mCategoryText.setText(folder.name);
                                if (resultList != null && resultList.size() > 0) {
                                   mVideoAdapter.setDefaultSelected(resultList);
                                }
                            }

                        }

                        mGridView.smoothScrollToPosition(0);
                    }
                }, 100);
            }
        });
    }

    private void selectVideoFromGrid(VideoInfo video, int mode) {
        if (video != null) {
            if (mode == 1) {
                if (resultList.contains(video.getPath())) {
                    resultList.remove(video.getPath());
                    if (mCallback != null) {
                        mCallback.onVideoUnselected(video.getPath());
                    }
                } else {
                    if (selectVideoCount() == resultList.size()) {
                        Toast.makeText(getActivity(), R.string.multi_msg_amount_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    resultList.add(video.getPath());
                    if (mCallback != null) {
                        mCallback.onVideoSelected(video.getPath());
                    }
                }

                mVideoAdapter.select(video);
            } else if (mode == 0 && mCallback != null) {
                mCallback.onSingleVideoSelected(video.getPath());
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("key_temp_file", mTmpFile);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mTmpFile = (File) savedInstanceState.getSerializable("key_temp_file");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(0, (Bundle) null, mLoaderCallback);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mFolderPopupWindow != null && mFolderPopupWindow.isShowing()) {
            mFolderPopupWindow.dismiss();
        }
        super.onConfigurationChanged(newConfig);
    }

    private int selectMode() {
        return getArguments() == null ? 1 : getArguments().getInt("select_count_mode");
    }

    private int selectVideoCount() {
        return getArguments() == null ? 3 : getArguments().getInt("max_select_count");
    }

    public interface Callback {
        void onSingleVideoSelected(String var1);

        void onVideoSelected(String var1);

        void onVideoUnselected(String var1);

    }
}

