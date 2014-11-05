package android.example.com.exampleprovider;

import android.content.Context;
import android.database.Cursor;
import android.example.com.exampleprovider.data.ExampleContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Lyla on 11/5/14.
 */
public class RatingAdapter extends CursorAdapter {

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final RatingBar ratingBar;
        public final TextView titleView;

        public ViewHolder(View view) {
            ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
            titleView = (TextView) view.findViewById(R.id.movie_name);

        }
    }


    public RatingAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.list_view_item_rating;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read title from cursor
        int titleIndex = cursor.getColumnIndex(ExampleContract.ExampleEntry.NAME);
        String movieTitle = cursor.getString(titleIndex);
        viewHolder.titleView.setText(movieTitle);

        int ratingIndex = cursor.getColumnIndex(ExampleContract.ExampleEntry.NUMBER_OF_FRIENDS);
        int rating = cursor.getInt(ratingIndex);

        float ratingDisplay = (float)(Math.min(rating,500)/100.0);

        viewHolder.ratingBar.setRating(ratingDisplay);

    }
}
