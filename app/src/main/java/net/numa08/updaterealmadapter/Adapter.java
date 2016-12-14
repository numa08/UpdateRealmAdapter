package net.numa08.updaterealmadapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.numa08.updaterealmadapter.databinding.RowBinding;

import io.realm.OrderedRealmCollection;

class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

    private OrderedRealmCollection<User> data;

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    void updateData(OrderedRealmCollection<User> data) {
        this.data = data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final RowBinding binding;

        ViewHolder(RowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = data.get(position);
        holder.binding.setUser(user);
    }

}
