package com.anola.t_bank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anola.t_bank.R;
import com.anola.t_bank.TransactionHistory;
import com.anola.t_bank.model.TransactionModel;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    ArrayList<TransactionModel> myTransac;

    public TransactionAdapter(ArrayList<TransactionModel> myTransac) {
        this.myTransac = myTransac;
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.single_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        holder.mydate.setText(myTransac.get(position).getMydate());
        holder.amount.setText("₦"+myTransac.get(position).getAmount());
        holder.tranc_type.setText(myTransac.get(position).getTransactionType());
        //holder.imageView.setImageResource(listdata[position].getImgId());
    }

    @Override
    public int getItemCount() {
        return myTransac.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mydate,tranc_type,amount;
        //public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);

            this.mydate = itemView.findViewById(R.id.tDate);
            this.tranc_type = itemView.findViewById(R.id.t_type);
            this.amount = itemView.findViewById(R.id.tamount);
            //relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}
