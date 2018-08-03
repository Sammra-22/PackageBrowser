package se.dohi.packagebrowser.adapaters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import se.dohi.packagebrowser.R;
import se.dohi.packagebrowser.listener.ImageListener;
import se.dohi.packagebrowser.model.Path;
import se.dohi.packagebrowser.utils.FileUtils;

/**
 * Created by Sam22 on 10/1/15.
 * Adapter holding the path list
 */
public class PathListAdapter extends ArrayAdapter<Path> implements ImageListener {

    private List<Path> items;

    public PathListAdapter(Context context, int resource, List<Path> items) {
        super(context, resource);
        this.items = items;
    }

    @Override
    public int getCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    @Override
    public Path getItem(int position) {
        if (items == null || position > items.size()) {
            return null;
        }
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.path_item, null);
            holder = new ViewHolder();
            holder.pathImageView = (ImageView) view.findViewById(R.id.path_image);
            holder.pathNameView = (TextView) view.findViewById(R.id.path_name);
            holder.pathInfoView = (TextView) view.findViewById(R.id.path_info);
            holder.pathLengthView = (TextView) view.findViewById(R.id.path_length);
            holder.pathTimeView = (TextView) view.findViewById(R.id.path_time);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        Path path = getItem(position);
        if (FileUtils.imageExists(getContext(), path.getName())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.pathImageView.setBackground(
                        Drawable.createFromPath(
                                FileUtils.getImageFile(getContext(), path.getName()).getAbsolutePath())
                );
            } else {
                holder.pathImageView.setBackgroundDrawable(
                        Drawable.createFromPath(
                                FileUtils.getImageFile(getContext(), path.getName()).getAbsolutePath())
                );
            }
        }
        holder.pathNameView.setText(path.getName());
        holder.pathInfoView.setText(path.getInfo());
        holder.pathLengthView.setText(String.format(Locale.getDefault(), "Length: %d Km", path.getLength()));
        holder.pathTimeView.setText(String.format(Locale.getDefault(), "Duration: %d H", path.getTime()));


        return view;
    }

    @Override
    public void onDownloadComplete() {
        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView pathImageView;
        TextView pathNameView, pathInfoView, pathLengthView, pathTimeView;
    }
}
