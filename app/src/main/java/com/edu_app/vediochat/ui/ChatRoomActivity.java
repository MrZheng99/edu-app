package com.edu_app.vediochat.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.edu_app.R;
import com.edu_app.controller.student.course.CourserFragmentAdapter;
import com.edu_app.vediochat.IViewCallback;
import com.edu_app.vediochat.PeerConnectionHelper;
import com.edu_app.vediochat.ProxyVideoSink;
import com.edu_app.vediochat.WebRTCManager;
import com.edu_app.vediochat.bean.MemberBean;
import com.edu_app.vediochat.controller.RoomChatController;
import com.edu_app.vediochat.utils.PermissionUtil;
import com.google.android.material.tabs.TabLayout;

import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





/**
 * 群聊界面
 */
public class ChatRoomActivity extends AppCompatActivity implements IViewCallback {

    private FrameLayout video_view;

    private WebRTCManager manager;
    private Map<String, SurfaceViewRenderer> _videoViews = new HashMap<>();
    private Map<String, ProxyVideoSink> _sinks = new HashMap<>();
    private List<MemberBean> _infos = new ArrayList<>();
    private VideoTrack _localVideoTrack;

    private int mScreenWidth;

    private EglBase rootEglBase;
    private boolean fragmentVisible;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, ChatRoomActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_room);
        initView();
        initVar();
        ChatRoomFragment chatRoomFragment = new ChatRoomFragment();
        replaceFragment(chatRoomFragment);
        fragmentVisible = true;
        startCall();

    }


    private void initView() {
        video_view = findViewById(R.id.mult_video_view);

    }

    private void initVar() {
        // 设置宽高比例
       WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
      if (manager != null) {
           mScreenWidth = manager.getDefaultDisplay().getWidth();
       }
      video_view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenWidth));
      rootEglBase = EglBase.create();

    }

    private void startCall() {
        manager = WebRTCManager.getInstance();
        manager.setCallback(this);

        if (!PermissionUtil.isNeedRequestPermission(ChatRoomActivity.this)) {
            manager.joinRoom(getApplicationContext(), rootEglBase);
        }

    }
// 屏蔽本地视频
//    @Override
//    public void onSetLocalStream(MediaStream stream, String userId) {
//        List<VideoTrack> videoTracks = stream.videoTracks;
//        if (videoTracks.size() > 0) {
//            _localVideoTrack = videoTracks.get(0);
//        }
//        runOnUiThread(() -> {
//            addView(userId, stream);
//        });
//    }

    @Override
    public void onAddRemoteStream(MediaStream stream, String userId) {
        runOnUiThread(() -> {
            addView(userId, stream);
        });


    }

    @Override
    public void onCloseWithId(String userId) {
        runOnUiThread(() -> {
            removeView(userId);
        });


    }


    private void addView(String id, MediaStream stream) {
        SurfaceViewRenderer renderer = new SurfaceViewRenderer(ChatRoomActivity.this);
        renderer.init(rootEglBase.getEglBaseContext(), null);
        renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        renderer.setMirror(true);
        // set render
        ProxyVideoSink sink = new ProxyVideoSink();
        sink.setTarget(renderer);
        if (stream.videoTracks.size() > 0) {
            stream.videoTracks.get(0).addSink(sink);
        }
        _videoViews.put(id, renderer);
        _sinks.put(id, sink);
        _infos.add(new MemberBean(id));
        video_view.addView(renderer);

        int size = _infos.size();
        for (int i = 0; i < size; i++) {
            MemberBean memberBean = _infos.get(i);
            SurfaceViewRenderer renderer1 = _videoViews.get(memberBean.getId());
            if (renderer1 != null) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.height = getWidth(size);
                layoutParams.width = getWidth(size);
                layoutParams.leftMargin = getX(size, i);
                layoutParams.topMargin = getY(size, i);
                renderer1.setLayoutParams(layoutParams);
            }

        }


    }


    private void removeView(String userId) {
        ProxyVideoSink sink = _sinks.get(userId);
        SurfaceViewRenderer renderer = _videoViews.get(userId);
        if (sink != null) {
            sink.setTarget(null);
        }
        if (renderer != null) {
            renderer.release();
        }
        _sinks.remove(userId);
        _videoViews.remove(userId);
        _infos.remove(new MemberBean(userId));
        video_view.removeView(renderer);


        int size = _infos.size();
        for (int i = 0; i < _infos.size(); i++) {
            MemberBean memberBean = _infos.get(i);
            SurfaceViewRenderer renderer1 = _videoViews.get(memberBean.getId());
            if (renderer1 != null) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.height = getWidth(size);
                layoutParams.width = getWidth(size);
                layoutParams.leftMargin = getX(size, i);
                layoutParams.topMargin = getY(size, i);
                renderer1.setLayoutParams(layoutParams);
            }

        }

    }

    private int getWidth(int size) {
        if (size <= 4) {
            return mScreenWidth / 2;
        } else if (size <= 9) {
            return mScreenWidth / 3;
        }
        return mScreenWidth / 3;
    }

    private int getX(int size, int index) {
        if (size <= 4) {
            if (size == 3 && index == 2) {
                return mScreenWidth / 4;
            }
            return (index % 2) * mScreenWidth / 2;
        } else if (size <= 9) {
            if (size == 5) {
                if (index == 3) {
                    return mScreenWidth / 6;
                }
                if (index == 4) {
                    return mScreenWidth / 2;
                }
            }

            if (size == 7 && index == 6) {
                return mScreenWidth / 3;
            }

            if (size == 8) {
                if (index == 6) {
                    return mScreenWidth / 6;
                }
                if (index == 7) {
                    return mScreenWidth / 2;
                }
            }
            return (index % 3) * mScreenWidth / 3;
        }
        return 0;
    }

    private int getY(int size, int index) {
        if (size < 3) {
            return mScreenWidth / 4;
        } else if (size < 5) {
            if (index < 2) {
                return 0;
            } else {
                return mScreenWidth / 2;
            }
        } else if (size < 7) {
            if (index < 3) {
                return mScreenWidth / 2 - (mScreenWidth / 3);
            } else {
                return mScreenWidth / 2;
            }
        } else if (size <= 9) {
            if (index < 3) {
                return 0;
            } else if (index < 6) {
                return mScreenWidth / 3;
            } else {
                return mScreenWidth / 3 * 2;
            }

        }
        return 0;
    }


    @Override  // 屏蔽返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        exit();
        super.onDestroy();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.mult_container, fragment)
                .commit();

    }

    // 切换摄像头
    public void switchCamera() {
        manager.switchCamera();
    }

    // 挂断
    public void hangUp() {
        exit();
        this.finish();
    }

    // 静音
    public void toggleMic(boolean enable) {
        manager.toggleMute(enable);
    }

    // 免提
    public void toggleSpeaker(boolean enable) {
        manager.toggleSpeaker(enable);
    }

    // 打开关闭摄像头
    public void toggleCamera(boolean enableCamera) {
        if (_localVideoTrack != null) {
            _localVideoTrack.setEnabled(enableCamera);
        }

    }

    private void exit() {
        manager.exitRoom();
        for (SurfaceViewRenderer renderer : _videoViews.values()) {
            renderer.release();
        }
        for (ProxyVideoSink sink : _sinks.values()) {
            sink.setTarget(null);
        }
        _videoViews.clear();
        _sinks.clear();
        _infos.clear();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            Log.i(PeerConnectionHelper.TAG, "[Permission] " + permissions[i] + " is " + (grantResults[i] == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                finish();
                break;
            }
        }
        manager.joinRoom(getApplicationContext(), rootEglBase);


    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(fragmentVisible){
                    findViewById(R.id.mult_switch_mute).setVisibility(View.GONE);
                    findViewById(R.id.mult_hand_free).setVisibility(View.GONE);
                    findViewById(R.id.mult_open_camera).setVisibility(View.GONE);
                    findViewById(R.id.mult_switch_camera).setVisibility(View.GONE);
                    findViewById(R.id.mult_switch_hang_up).setVisibility(View.GONE);

                    fragmentVisible=false;
                    return true;

                }else {
                    findViewById(R.id.mult_switch_mute).setVisibility(View.VISIBLE);
                    findViewById(R.id.mult_hand_free).setVisibility(View.VISIBLE);
                    findViewById(R.id.mult_open_camera).setVisibility(View.VISIBLE);
                    findViewById(R.id.mult_switch_camera).setVisibility(View.VISIBLE);
                    findViewById(R.id.mult_switch_hang_up).setVisibility(View.VISIBLE);
                    fragmentVisible = true;
                    return true;

                }

        }
        return false;
    }
}
