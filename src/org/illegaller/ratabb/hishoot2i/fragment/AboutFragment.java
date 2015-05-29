package org.illegaller.ratabb.hishoot2i.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.util.SystemProp;

import com.afollestad.materialdialogs.MaterialDialog;

//import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AboutFragment extends Fragment implements View.OnClickListener {

	private Context mContext;
	private static final String TAG = "Hishoot2i";
	private TextView textAbout;
	private TextView textVer;
	private Button btLicenses;
	private long[] mHits = new long[5];

	public AboutFragment() {
	}

	public static AboutFragment newInstance() {
		return new AboutFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.about, container, false);
		textAbout = (TextView) view.findViewById(R.id.textAbout);
		textVer = (TextView) view.findViewById(R.id.tvVer);
		btLicenses = (Button) view.findViewById(R.id.bLicenses);

		mContext = getActivity();
		String aboutTxt = String.format("%s\n%s", getString(R.string.about),
				getString(R.string.about2));

		textAbout.setText(aboutTxt);
		textVer.setText(getString(R.string.version, getString(R.string.app_ver)));

		btLicenses.setOnClickListener(this);
		textVer.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bLicenses:
			TextView tvLicense = new TextView(mContext);
			tvLicense.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			tvLicense.setTextAppearance(mContext, R.style.HishootAboutBodyText);
			tvLicense.setText(Html.fromHtml(getStringFromAssets("LICENSE")));

			getMaterialDialog(tvLicense);
			break;
		case R.id.tvVer:
			System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
			mHits[mHits.length - 1] = SystemClock.uptimeMillis();
			if (mHits[0] >= (SystemClock.uptimeMillis() - 1000)) {
				int t = getResetTrial(mContext);
				Toast.makeText(mContext, "Ok trial was reset: " + t,
						Toast.LENGTH_SHORT).show();
			}

			break;
		default:
			break;
		}

	}

	private int getResetTrial(Context context) {
		SystemProp.resTrial(context.getContentResolver());
		return SystemProp.getTrial(context);
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
