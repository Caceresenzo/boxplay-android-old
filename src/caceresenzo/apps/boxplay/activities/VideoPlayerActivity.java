package caceresenzo.apps.boxplay.activities;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.support.v7.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity implements OnPreparedListener {
	
	// private static VideoPlayerActivity INSTANCE;
	//
	// private VideoView videoView;
	//
	// public VideoPlayerActivity() {
	// ;
	// }
	//
	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.activity_videoplayer);
	// INSTANCE = this;
	//
	// initializeViews();
	// }
	//
	// private void initializeViews() {
	// // videoView = (VideoView) findViewById(R.id.activity_videoplayer_videoview_player); // com.devbrackets.android.exomedia.ui.widget.VideoView
	// // View view = findViewById(R.id.activity_videoplayer_videoview_player); // com.devbrackets.android.exomedia.ui.widget.VideoView
	// // ToastUtils.makeLong(INSTANCE, view.getClass().toString());
	//
	// // videoView.setOnPreparedListener(this);
	// //
	// // videoView.setVideoURI(Uri.parse("https://archive.org/download/Popeye_forPresident/Popeye_forPresident_512kb.mp4"));
	// }
	//
	// public static VideoPlayerActivity getVideoPlayerActivity() {
	// return INSTANCE;
	// }
	
	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}
	
}