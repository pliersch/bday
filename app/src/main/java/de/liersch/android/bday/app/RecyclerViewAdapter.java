package de.liersch.android.bday.app;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.liersch.android.bday.R;
import de.liersch.android.bday.app.util.ViewModel;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

  private List<ViewModel> items;
  private OnItemClickListener onItemClickListener;

  public RecyclerViewAdapter(List<ViewModel> items) {
    this.items = items;
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
    v.setOnClickListener(this);
    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    ViewModel item = items.get(position);
    holder.text.setText(item.getText());
    holder.image.setImageBitmap(null);
    Picasso.with(holder.image.getContext()).load(item.getImage()).into(holder.image);
    holder.itemView.setTag(item);
  }

  @Override public int getItemCount() {
    return items.size();
  }

  @Override public void onClick(final View v) {
    // Give some time to the ripple to finish the effect
    if (onItemClickListener != null) {
      new Handler().postDelayed(new Runnable() {
        @Override public void run() {
          onItemClickListener.onItemClick(v, (ViewModel) v.getTag());
        }
      }, 200);
    }
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {
    public ImageView image;
    public TextView text;

    public ViewHolder(View itemView) {
      super(itemView);
      image = (ImageView) itemView.findViewById(R.id.image);
      text = (TextView) itemView.findViewById(R.id.text);
    }
  }

  public interface OnItemClickListener {

    void onItemClick(View view, ViewModel viewModel);

  }
}
