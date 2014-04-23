package com.aeclarke.typewriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aeclarke.typewriter.R;
import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.core.CMap;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.sfntly.table.core.CMapTable.CMapId;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		FontFactory factory = FontFactory.getInstance();
		Font font = null;
		try {
			font = loadFont(new File("assets/Roboto-Regular.ttf"))[0];
			CMapTable cmapTable = font.getTable(Tag.cmap);
			CMap cmap = cmapTable.cmap(CMapId.MAC_ROMAN);
			TextView contentView = (TextView) findViewById(R.id.main_content_text);
			contentView.setText(cmapTable.toString());
			
			
//			GlyphTable glyfTable = font.getTable(Tag.glyf);
//			ReadableFontData fontData = glyfTable.readFontData();
//			Font.Builder fontBuilder = factory.newFontBuilder();
//			Builder<? extends Table> newTableBuilder = fontBuilder.newTableBuilder(Tag.glyf);
//
//			WritableFontData writableFontData = WritableFontData.createWritableFontData(0);//newTableBuilder.data();
//			fontData.copyTo(writableFontData);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	  public static Font[] loadFont(File file) throws IOException {
		    FontFactory fontFactory = FontFactory.getInstance();
		    fontFactory.fingerprintFont(true);
		    FileInputStream is = null;
		    try {
		      is = new FileInputStream(file);
		      return fontFactory.loadFonts(is);
		    } finally {
		      if (is != null) {
		        is.close();
		      }
		    }
		  }

}
