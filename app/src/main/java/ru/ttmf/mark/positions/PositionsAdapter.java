package ru.ttmf.mark.positions;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.ttmf.mark.R;
import ru.ttmf.mark.network.model.Position;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PositionsAdapter extends RecyclerView.Adapter<PositionsAdapter.PositionViewHolder> {

    private List<Position> positionList = new ArrayList<>();
    private OnPositionClickListener onPositionClickListener;

    public PositionsAdapter(List<Position> positions, OnPositionClickListener onPositionClickListener) {
        this.positionList.addAll(positions);
        this.onPositionClickListener = onPositionClickListener;
    }

    @NonNull
    @Override
    public PositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PositionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.position_viewholder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PositionViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.number.setText(positionList.get(position).getSgTin());
    }

    public void removeItem(String code) {
        int index = -1;
        for (Position position : positionList) {
            if (position.getSgTin().equals(code)) {
                index = positionList.indexOf(position);
                break;
            }
        }
        if (index >= 0) {
            positionList.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void addItem(String code) {
        positionList.add(new Position(code));
        notifyItemInserted(positionList.size() - 1);
    }


    public List<Position> getItems() {
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

    public interface OnPositionClickListener {
        void onPositionClick(Position position);
    }
}
