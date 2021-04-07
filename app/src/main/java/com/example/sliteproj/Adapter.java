package com.example.sliteproj;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.Holder>{

    private Context context;
    private ArrayList<Model> arrayList;

    DatabaseHelper databaseHelper;

    public Adapter(Context context, ArrayList<Model> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

        databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {

        // get for views
        Model model = arrayList.get(position);
        final String id = model.getId();
        final String image = model.getImage();
        final String name = model.getName();
        final String age = model.getAge();
        final String phone = model.getPhone();
        final String des = model.getDes();
        final String addTimeStamp = model.getAddTimeStamp();
        final String updateTimeStamp = model.getUpdateTimeStamp();

        // set data
        holder.profileIv.setImageURI(Uri.parse(image));
        holder.name.setText(name);
        holder.age.setText(age);
        holder.phone.setText(phone);
        holder.des.setText(des);

        // handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // handle when click on more button
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog(
                        ""+position,
                        ""+id,
                        ""+name,
                        ""+age,
                        ""+phone,
                        ""+des,
                        ""+image,
                        ""+addTimeStamp,
                        ""+updateTimeStamp
                );
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteDialog(""+id);
                return false;
            }
        });

    }

    private void deleteDialog(final String id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.ic_action_delete);
        builder.setCancelable(false);
        builder.setTitle("Delete");
        builder.setMessage("Are you want to delete?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHelper.deleteInfo(id);
                ((MainActivity)context).onResume();
                Toast.makeText(context, "Delete Successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    private void editDialog(String position, final String id, final String name, final String age, final String phone, final String des, final String image, final String addTimeStamp, final String updateTimeStamp) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.ic_action_edit);
        builder.setCancelable(false);
        builder.setTitle("Edit");
        builder.setMessage("Are you want to edit?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(context, EditRecordActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("NAME", name);
                intent.putExtra("AGE", age);
                intent.putExtra("PHONE", phone);
                intent.putExtra("IMAGE", image);
                intent.putExtra("DES", des);
                intent.putExtra("ADD_TIMESTAMP", addTimeStamp);
                intent.putExtra("UPDATE_TIMESTAMP", updateTimeStamp);
                intent.putExtra("editMode", true);
                context.startActivity(intent);

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView profileIv;
        TextView name, age, phone, des;
        ImageButton editButton;

        public Holder(@NonNull View itemView) {
            super(itemView);

            // initialize views
            profileIv = itemView.findViewById(R.id.profileIv);
            name = itemView.findViewById(R.id.name);
            age = itemView.findViewById(R.id.age);
            phone = itemView.findViewById(R.id.phone);
            des = itemView.findViewById(R.id.des);
            // find id of more button
            editButton = itemView.findViewById(R.id.editBtn);
        }
    }
}
