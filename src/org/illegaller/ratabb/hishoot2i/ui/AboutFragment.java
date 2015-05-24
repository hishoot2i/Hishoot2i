package org.illegaller.ratabb.hishoot2i.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.illegaller.ratabb.hishoot2i.R;

import com.afollestad.materialdialogs.MaterialDialog;

//import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class AboutFragment extends Fragment implements View.OnClickListener {

	private Context mContext;
	private static final String TAG = "Hishoot2i";

	// @InjectView(R.id.textAbout)
	private TextView textAbout;
	// @InjectView(R.id.tvVer)
	private TextView textVer;

	private Button btLicenses;

	public AboutFragment() {
	}

	public static AboutFragment newInstance() {
		return new AboutFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.about, container, false);
		// ButterKnife.inject(this, view);
		textAbout = (TextView) view.findViewById(R.id.textAbout);
		textVer = (TextView) view.findViewById(R.id.tvVer);
		btLicenses = (Button) view.findViewById(R.id.bLicenses);

		mContext = getActivity();
		String aboutTxt = String.format("%s\n%s", getString(R.string.about),
				getString(R.string.about2));

		textAbout.setText(aboutTxt);
		textVer.setText(getString(R.string.version, getString(R.string.app_ver)));

		btLicenses.setOnClickListener(this);
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// ButterKnife.reset(this);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// mContext = getActivity();
		// String aboutTxt = String.format("%s\n%s", getString(R.string.about),
		// getString(R.string.about2));
		//
		// textAbout.setText(aboutTxt);
		//
		// textVer.setText(getString(R.string.version,
		// getString(R.string.app_ver)));

	}

	// @OnClick(R.id.bLicenses)
	// void onClickLecenses() {
	// TextView tvLicense = new TextView(mContext);
	// tvLicense.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
	// LayoutParams.WRAP_CONTENT));
	//
	// tvLicense.setTextAppearance(mContext, R.style.HishootAboutBodyText);
	// tvLicense.setText(Html.fromHtml(getStringFromAssets("LICENSE")));
	//
	// getMaterialDialog(tvLicense);
	// }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.bLicenses) {
			TextView tvLicense = new TextView(mContext);
			tvLicense.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			tvLicense.setTextAppearance(mContext, R.style.HishootAboutBodyText);
			tvLicense.setText(Html.fromHtml(getStringFromAssets("LICENSE")));

			getMaterialDialog(tvLicense);
		}
	}

	private MaterialDialog getMaterialDialog(View view) {
		return new MaterialDialog.Builder(mContext)

		.customView(view, true)

		.autoDismiss(true)

		.title(R.string.licenses)

		.show();
	}

	private String getStringFromAssets(String asset) {
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = mContext.getAssets().open(asset,
					AssetManager.ACCESS_BUFFER);

			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			String str;

			while ((str = br.readLine()) != null) {
				sb.append(str);
				sb.append("<br>");// html?"<br>":"\n"
			}
			br.close();
			is.close();
		} catch (IOException ioe) {
			Log.e(TAG, asset + ioe.getMessage());
			sb.append("");
		} catch (NullPointerException npe) {
			Log.e(TAG, asset + npe.getMessage());
			sb.append("");
		}

		return sb.toString();
	}

}
