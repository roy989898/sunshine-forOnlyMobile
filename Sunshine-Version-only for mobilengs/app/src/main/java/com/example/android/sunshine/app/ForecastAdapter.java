package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    Context mcontext;
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mcontext=context;
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(mcontext,high, isMetric) + "/" + Utility.formatTemperature(mcontext,low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */


    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        // Use placeholder image for now
        ViewHolder vh=(ViewHolder)view.getTag();
        ImageView iconView = vh.iconView;
        if(getItemViewType(cursor.getPosition())==VIEW_TYPE_TODAY){
            iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
        }else{
            iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));
        }



        //Read date from cursor
        Long date=cursor.getLong(ForecastFragment.COL_WEATHER_DATE);

        TextView dateview= vh.dateView;

        dateview.setText(Utility.getFriendlyDayString(context,date));
        // Read weather forecast from cursor
        String w_forecast=cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        TextView forecastView=vh.descriptionView;
        forecastView.setText(w_forecast);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        TextView highView = vh.highTempView;
        highView.setText(Utility.formatTemperature(mcontext,high, isMetric));

        //  Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        TextView lowView =vh.lowTempView;
        lowView.setText(Utility.formatTemperature(mcontext,low, isMetric));




    }

    //multi layput
    //how many view type
     static final private int VIEW_TYPE_TODAY=0;
    static final private int VIEW_TYPE_FUTUREDAY=1;
    @Override
    public int getViewTypeCount() {
        return 2;
    }
    //use the position od the row in the cursor to identify the viewtype(layout)
    @Override
    public int getItemViewType(int position) {
        return (position==0)?VIEW_TYPE_TODAY:VIEW_TYPE_FUTUREDAY;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        // TODO: Determine layoutId from viewType
        if(viewType==VIEW_TYPE_FUTUREDAY){
            layoutId=R.layout.list_item_forecast;
        }else{
            layoutId=R.layout.list_item_forecast_today;
        }
        View view=LayoutInflater.from(context).inflate(layoutId, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}