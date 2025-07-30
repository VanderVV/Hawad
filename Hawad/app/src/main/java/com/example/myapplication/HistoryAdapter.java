package com.example.myapplication;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<OperationHistoryItem> historyItems;

    public HistoryAdapter(List<OperationHistoryItem> historyItems) {
        this.historyItems = historyItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OperationHistoryItem item = historyItems.get(position);
        holder.operationText.setText(item.getOperation());
        holder.statusText.setText(item.getStatusChange());
        holder.timestampText.setText(item.getTimestamp());
        holder.view.setBackgroundColor(item.getOldStatus() == 1 ? holder.view.getContext().getResources().getColor(R.color.teal_200) : holder.view.getContext().getResources().getColor(R.color.colorPrimary));

    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView operationText;
        public TextView statusText;
        public TextView timestampText;
        public View view;

        public ViewHolder(View view) {
            super(view);
            operationText = view.findViewById(R.id.operationText);
            statusText = view.findViewById(R.id.statusText);
            timestampText = view.findViewById(R.id.timestampText);
            this.view =  view.findViewById(R.id.status_indicator);
        }
    }
}