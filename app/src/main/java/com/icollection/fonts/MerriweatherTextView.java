package com.icollection.fonts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.icollection.R;


public class MerriweatherTextView extends android.support.v7.widget.AppCompatTextView {

	public MerriweatherTextView(Context context) {
		super(context);
		if (isInEditMode()) return;
		parseAttributes(null);
	}

	public MerriweatherTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) return;
		parseAttributes(attrs);
	}

	public MerriweatherTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (isInEditMode()) return;
		parseAttributes(attrs);
	}
	
	private void parseAttributes(AttributeSet attrs) {
		int typeface;
		if (attrs == null) { //Not created from xml
			typeface = MerriWeather.MerriWeather_REGULAR;
		} else {
		    TypedArray values = getContext().obtainStyledAttributes(attrs, R.styleable.MerriWeatherTextView);
		    typeface = values.getInt(R.styleable.MerriWeatherTextView_typeface, MerriWeather.MerriWeather_REGULAR);
		    values.recycle();
		}
	    setTypeface(getRoboto(typeface));
	}
	
	public void setMerriWeatherTypeface(int typeface) {
	    setTypeface(getRoboto(typeface));
	}
	
	private Typeface getRoboto(int typeface) {
		return getRoboto(getContext(), typeface);
	}
	
	public static Typeface getRoboto(Context context, int typeface) {
		switch (typeface) {
		case MerriWeather.MerriWeather_BLACK:
			if (MerriWeather.sMerriWeatherBlack == null) {
				MerriWeather.sMerriWeatherBlack = Typeface.createFromAsset(context.getAssets(), "fonts/Merriweather-UltraBold.ttf");
			}
			return MerriWeather.sMerriWeatherBlack;
		case MerriWeather.MerriWeather_BOLD:
			if (MerriWeather.sMerriWeatherBold == null) {
				MerriWeather.sMerriWeatherBold = Typeface.createFromAsset(context.getAssets(), "fonts/Merriweather-Bold.ttf");
			}
			return MerriWeather.sMerriWeatherBold;
		case MerriWeather.MerriWeather_LIGHT:
			if (MerriWeather.sMerriWeatherLight == null) {
				MerriWeather.sMerriWeatherLight = Typeface.createFromAsset(context.getAssets(), "fonts/Merriweather-Light.ttf");
			}
			return MerriWeather.sMerriWeatherLight;
		case MerriWeather.MerriWeather_LIGHT_ITALIC:
			if (MerriWeather.sMerriWeatherLightItalic == null) {
				MerriWeather.sMerriWeatherLightItalic = Typeface.createFromAsset(context.getAssets(), "fonts/Merriweather-LightIt.ttf");
			}
			return MerriWeather.sMerriWeatherLightItalic;
			case MerriWeather.MerriWeather_ITALIC:
				if (MerriWeather.sMerriWeatherItalic == null) {
					MerriWeather.sMerriWeatherItalic = Typeface.createFromAsset(context.getAssets(), "fonts/Merriweather-Italic.ttf");
				}
				return MerriWeather.sMerriWeatherItalic;

			default:
			case MerriWeather.MerriWeather_REGULAR:
				if (MerriWeather.sMerriWeatherRegular == null) {
					MerriWeather.sMerriWeatherRegular = Typeface.createFromAsset(context.getAssets(), "fonts/Merriweather-Regular.ttf");
				}
				return MerriWeather.sMerriWeatherRegular;
		}
	}
	
	public static class MerriWeather {
		public static final int MerriWeather_BLACK = 0;
		public static final int MerriWeather_BOLD = 1;
		public static final int MerriWeather_LIGHT = 2;
		public static final int MerriWeather_LIGHT_ITALIC = 3;
		public static final int MerriWeather_REGULAR = 4;
		public static final int MerriWeather_ITALIC = 5;


		private static Typeface sMerriWeatherBlack;
		private static Typeface sMerriWeatherBold;
		private static Typeface sMerriWeatherLight;
		private static Typeface sMerriWeatherLightItalic;
		private static Typeface sMerriWeatherItalic;
		private static Typeface sMerriWeatherRegular;

	}
}
