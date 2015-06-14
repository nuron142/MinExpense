package com.nuron.minexpense.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nuron.minexpense.R;

import java.util.List;

/**
 * Created by sunil on 12-Jun-15.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ArtImageViewHolder> {

    List<Integer> artImageID;
    int newSelectedItem=-1;
    public RecyclerAdapter(List<Integer> imageIDs){
        this.artImageID = imageIDs;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ArtImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.art_image_list, viewGroup, false);
        return new ArtImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ArtImageViewHolder personViewHolder, int i) {

        personViewHolder.artImageView.setImageResource(artImageID.get(i));
        personViewHolder.artImageLayout.setBackgroundColor(Color.parseColor("#3E4250"));

        if(i == newSelectedItem && newSelectedItem !=-1 )
            personViewHolder.artImageLayout.setBackgroundColor(Color.parseColor("#EF4836"));

    }

    @Override
    public int getItemCount() {
        return artImageID.size();
    }

    public int getSelectedItem(){
        return newSelectedItem;
    }

    public void setSelectedItem(int id){
        newSelectedItem=id;
        notifyDataSetChanged();
    }

    public class ArtImageViewHolder extends RecyclerView.ViewHolder{

        ImageView artImageView;
        RelativeLayout artImageLayout;

        ArtImageViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            artImageView = (ImageView)itemView.findViewById(R.id.art_image);
            artImageLayout = (RelativeLayout) itemView.findViewById(R.id.art_image_layout);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newSelectedItem = getLayoutPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }

}
