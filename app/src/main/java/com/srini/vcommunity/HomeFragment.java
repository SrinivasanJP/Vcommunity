package com.srini.vcommunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class HomeFragment extends Fragment {


    //view variables
    private TextView vUser;
    private RecyclerView vRecyclerView;
    private LinearLayoutManager vLinearLayoutManager;

    private FloatingActionButton vCreatePost;

    //firebase variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter<TaskHelperClass,taskViewHolder> taskAdapter;
    private DocumentReference documentReference;

    //backend variables
    SharedPreferences sharedPreferences;
    String userName;
    TaskHelperClass swipedTask;
    FirestoreRecyclerOptions<TaskHelperClass> allTasks;
    View view;


    //Queries
    Query query;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        taskAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(taskAdapter!=null){
            taskAdapter.stopListening();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        //hooks
        vUser = view.findViewById(R.id.username);
        vCreatePost = view.findViewById(R.id.create_post);
        vCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),CreatePost.class));
            }
        });

        sharedPreferences = this.getActivity().getSharedPreferences(SharedPreferenceStore.SHARED_PREFERENCE_NAME,this.getActivity().MODE_PRIVATE);
        vUser.setText("Hello "+sharedPreferences.getString(SharedPreferenceStore.KEY_FIRST_NAME,"Hello"));


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        query = firebaseFirestore.collection("posts");

        allTasks = new FirestoreRecyclerOptions.Builder<TaskHelperClass>().setQuery(query,TaskHelperClass.class).build();

        taskAdapter = new FirestoreRecyclerAdapter<TaskHelperClass, taskViewHolder>(allTasks) {
            @NonNull
            @Override
            public taskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_recyclerview,parent,false);
                return new taskViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull taskViewHolder holder, int position, @NonNull TaskHelperClass model) {
                holder.vTitle.setText(model.getTitle());
                holder.vDescription.setText(model.getDescription());

                String docTitle = taskAdapter.getSnapshots().get(position).getTitle();
                TaskHelperClass deletedTask = allTasks.getSnapshots().get(position);
                holder.vPopUpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v, Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                //edit
                                return false;
                            }
                        });
                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                          @Override
                          public boolean onMenuItemClick(MenuItem item) {
                              documentReference = FirebaseFirestore.getInstance().collection("tasks").document(firebaseUser.getUid()).collection("allTasks").document(docTitle);
                                documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getActivity(),"Successfully Deleted",Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(getActivity(), "Falid to Delete", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                BottomNavigationView bnv = getActivity().findViewById(R.id.bottom_nav);
                                Snackbar snackbar = Snackbar.make(view,docTitle,Snackbar.LENGTH_LONG)
                                        .setAction("UNDO", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                documentReference.set(deletedTask);
                                            }
                                        });
                                snackbar.setAnchorView(bnv);
                                snackbar.show();
                              return false;
                          }
                      });
                      popupMenu.show();
                    }
                });
            }
        };

        vRecyclerView = view.findViewById(R.id.recyclerView_containor);
        vLinearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        vRecyclerView.setLayoutManager(vLinearLayoutManager);
        vRecyclerView.setAdapter(taskAdapter);



        return view;
    }

//    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
//        @Override
//        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            return false;
//        }
//        @Override
//        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//            int TaskDraged = viewHolder.getPosition();
//            Log.e("I",String.valueOf(TaskDraged));
//            switch (direction){
//                case (ItemTouchHelper.LEFT):{
//                    swipedTask = allTasks.getSnapshots().get(TaskDraged);
//                    documentReference = FirebaseFirestore.getInstance().collection("tasks").document(firebaseUser.getUid()).collection("allTasks").document(swipedTask.getTitle());
//                    documentReference.delete();
//                    taskAdapter.notifyItemRemoved(TaskDraged);
//                    BottomNavigationView bnv = getActivity().findViewById(R.id.bottom_nav);
//                    Snackbar snackbar = Snackbar.make(view, swipedTask.getTitle(),Snackbar.LENGTH_LONG)
//                            .setAction("UNDO", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    documentReference.set(swipedTask);
//                                    Log.e("tag",String.valueOf(TaskDraged));
//                                    taskAdapter.notifyItemInserted(TaskDraged);
//                                }
//                            });
//                    snackbar.setAnchorView(bnv);
//                    snackbar.show();
//                    break;
//
//                }
//                case (ItemTouchHelper.RIGHT):{
//                    swipedTask = allTasks.getSnapshots().get(TaskDraged);
//                    TaskHelperClass editTask = swipedTask;
//                    editTask.setDone(true);
//                    documentReference = FirebaseFirestore.getInstance().collection("tasks").document(firebaseUser.getUid()).collection("allTasks").document(swipedTask.getTitle());
//                    documentReference.set(editTask);
//                    taskAdapter.notifyItemRemoved(TaskDraged);
//                    break;
//                }
//
//            }
//
////        }
//
//        @Override
//        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//        }
//    };



    public class taskViewHolder extends RecyclerView.ViewHolder{

        private TextView vTitle;
        private TextView vDescription;
        private RelativeLayout vitemHolder;
        private ImageView vPopUpButton;

        public taskViewHolder(@NonNull View itemView) {
            super(itemView);
            vTitle = itemView.findViewById(R.id.task_title_show);
            vDescription = itemView.findViewById(R.id.task_description_show);
            vitemHolder = itemView.findViewById(R.id.taskHolders);
            vPopUpButton = itemView.findViewById(R.id.card_menu);


        }
    }
}