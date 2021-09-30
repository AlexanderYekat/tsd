package ru.ttmf.mark.consumption_positions;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.ttmf.mark.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConsumptionPositionsAdapter extends RecyclerView.Adapter<ConsumptionPositionsAdapter.PositionViewHolder> {

    private List<String> positionList = new ArrayList<>();

    public ConsumptionPositionsAdapter(List<String> positions) {
        this.positionList.addAll(positions);
    }


    @NonNull
    @Override
    public PositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PositionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.position_viewholder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PositionViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.number.setText(positionList.get(position));
    }

    public void removeItem(String code) {
        int index = -1;

        if (positionList.contains(code))
            index = positionList.indexOf(code);

        if (index >= 0) {
            positionList.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void addItem(String code) {
        positionList.add(code);
        notifyItemInserted(positionList.size() - 1);
    }


    public List<String> getItems() {
        return positionList;
    }

    @Override
    public int getItemCount() {
        return positionList.size();
    }

    public class PositionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.number)
        TextView number;

        public PositionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //todo remove
//            itemView.setOnClickListener(view -> {
//                if (onPositionClickListener!=null){
//                    onPositionClickListener.onPositionClick(positionList.get((Integer) itemView.getTag()));
//                }
//            });
        }
    }
}

