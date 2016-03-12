package ch.projecthelin.droneonboardapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.dto.message.MissionProduct;

import java.util.ArrayList;

public class MissionProductAdapter extends ArrayAdapter<MissionProduct> {

    public MissionProductAdapter(Context context, ArrayList<MissionProduct> missionProducts) {
       super(context, 0, missionProducts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       MissionProduct missionProduct = getItem(position);

       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_mission_product, parent, false);
       }
       TextView productName = (TextView) convertView.findViewById(R.id.product_name);
       TextView amount = (TextView) convertView.findViewById(R.id.product_amount);

       productName.setText(missionProduct.getProduct().getName());
       amount.setText(String.valueOf(missionProduct.getAmount()));

       return convertView;
   }
}