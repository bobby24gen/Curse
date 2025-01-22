package com.example.curse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.curse.JobsClickListener;
import com.example.curse.R;
import com.example.curse.models.Jobs;

import java.util.List;

public class JobsListAdapter extends RecyclerView.Adapter<JobsViewHolder> {

    Context context;
    List<Jobs> list;

    JobsClickListener listener;

    public JobsListAdapter(Context context, List<Jobs> list, JobsClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new JobsViewHolder(LayoutInflater.from(context).inflate(R.layout.jobs_list_mini, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull JobsViewHolder holder, int position) {
        holder.textView_title.setText(list.get(position).getTitle());
        holder.textView_title.setSelected(true);

        holder.textView_description.setText(list.get(position).getDescription());

        holder.textView_cost.setText(list.get(position).getCost());
        holder.textView_cost.setSelected(true);

        holder.textView_date.setText(list.get(position).getDate());
        holder.textView_date.setSelected(true);

        if (list.get(position).getPinned()) {
            holder.imageView_pin.setImageResource(R.drawable.baseline_favorite_red_24);
        } else {
            holder.imageView_pin.setImageResource(0);
        }

        holder.jobs_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnClick(list.get(holder.getAdapterPosition()));
            }
        });

        holder.jobs_container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClick(list.get(holder.getAdapterPosition()), holder.jobs_container);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList (List<Jobs> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }
}

class JobsViewHolder extends RecyclerView.ViewHolder {

    CardView jobs_container;
    TextView textView_title, textView_description, textView_date, textView_cost;
    ImageView imageView_pin;

    public JobsViewHolder(@NonNull View itemView) {
        super(itemView);

        jobs_container = itemView.findViewById(R.id.jobs_container);

        textView_title = itemView.findViewById(R.id.textView_title);
        textView_description = itemView.findViewById(R.id.textView_description);
        textView_cost = itemView.findViewById(R.id.textView_cost);
        textView_date = itemView.findViewById(R.id.textView_date);

        imageView_pin = itemView.findViewById(R.id.imageView_pin);
    }
}
