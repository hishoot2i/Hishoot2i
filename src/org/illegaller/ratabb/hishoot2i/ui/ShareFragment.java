package org.illegaller.ratabb.hishoot2i.ui;

import java.io.File;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import org.illegaller.ratabb.hishoot2i.Constants;
import org.illegaller.ratabb.hishoot2i.R;





//import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
//import android.widget.ListView;
import android.widget.Toast;

public class ShareFragment extends Fragment implements OnClickListener {
	private Context mContext;
	private String mPathImage;
	// private static final String TAG = "ShareFragment";

	// @InjectView(R.id.iv_preview)
	private ImageView ivPreview;
	// @InjectView(R.id.gruplist)
//	private ListView grupListView;

	// @InjectView(R.id.fab_menu)
	private FloatingActionsMenu fabMenu;
	// @InjectViews({ R.id.fab_share_app, R.id.fab_share_wall,
	// R.id.fab_share_grup })
	// FloatingActionButton[] fabs_share;

	// @InjectView(R.id.fab_share_app)
	private FloatingActionButton fabs_share_app;
	// @InjectView(R.id.fab_open_with)
	private FloatingActionButton fab_open_with;

	public ShareFragment() {
	}

	public static ShareFragment newInstance(String string) {
		final ShareFragment sf = new ShareFragment();
		final Bundle arg = new Bundle();
		arg.putString(Constants.EXTRA_FILE_SAVE, string);
		sf.setArguments(arg);

		return sf;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		mPathImage = getArguments() != null ? getArguments().getString(
				Constants.EXTRA_FILE_SAVE) : null;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.share, container, false);
//		ButterKnife.inject(this, view);
		
		ivPreview =(ImageView)view.findViewById(R.id.iv_preview);
		
		fabMenu =(FloatingActionsMenu)view.findViewById(R.id.fab_menu);
		fabs_share_app=(FloatingActionButton)view.findViewById(R.id.fab_share_app);
		fab_open_with=(FloatingActionButton)view.findViewById(R.id.fab_open_with);
		
		ivPreview.setImageBitmap(BitmapFactory.decodeFile(mPathImage));

		fabs_share_app.setOnClickListener(this);
		fab_open_with.setOnClickListener(this);
		return view;
	}

//	@Override
//	public void onViewCreated(View view, Bundle savedInstanceState) {
//		super.onViewCreated(view, savedInstanceState);

//		ivPreview.setImageBitmap(BitmapFactory.decodeFile(mPathImage));
//
//		// for (FloatingActionButton fab : fabs_share) {
//		// fab.setOnClickListener(this);
//		// }
//		fabs_share_app.setOnClickListener(this);
//		fab_open_with.setOnClickListener(this);
//
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fab_share_app:
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("image/*");
			i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mPathImage)));
			startActivity(Intent.createChooser(i, "Share with: "));
			break;
		case R.id.fab_open_with:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(mPathImage)), "image/*");
			startActivity(Intent.createChooser(intent, "Open with: "));
			break;

		// break;
		default:
			break;
		}
		fabMenu.collapse();
	}

	// XXX
	@SuppressWarnings("unused")
	private void comingSoon() {
		Toast.makeText(mContext, "coming soon *7o7*", Toast.LENGTH_SHORT)
				.show();
	}

}
