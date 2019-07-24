package app.adie.reservation.view.widget;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

public interface AdapterDelegate<T> {
    int getItemViewType();

    boolean isForViewType(@NonNull T t, int i);

    void onBindViewHolder(@NonNull T t, int i, @NonNull ViewHolder viewHolder);

    @NonNull
    ViewHolder onCreateViewHolder(ViewGroup viewGroup);
}
