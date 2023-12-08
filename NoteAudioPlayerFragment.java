package com.subtlerr.singtico.Gandhaar.Gandhaar.Music.notes;

import static com.subtlerr.singtico.practice.instrument_fragments.TablaFragment.TAG;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.subtlerr.singtico.Gandhaar.Gandhaar.Music.notes.NotesCategories.Response.DetailsResponse;
import com.subtlerr.singtico.Gandhaar.Gandhaar.Music.notes.adapter.NotesDetailsAdapter;
import com.subtlerr.singtico.Gandhaar.Gandhaar.Music.notes.data.MusicNoteModel;
import com.subtlerr.singtico.Gandhaar.Gandhaar.Music.notes.data.NotesModel;
import com.subtlerr.singtico.R;
import com.subtlerr.singtico.Gandhaar.Gandhaar.Api.ApiClient;
import com.subtlerr.singtico.Gandhaar.Gandhaar.Api.ApiInterface;
import com.subtlerr.singtico.helpers.SharedPreferencesHelper;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NoteAudioPlayerFragment extends Fragment {

    private static final int RETURN_CODE_SUCCESS = 1;
    private boolean checkPermission = false;
    Uri uri;
    String url;
    ListView listView;
    MediaRecorder mediaRecorder;


    JcPlayerView jcPlayerView;
    JcPlayerView jcPlayerView1;
    ArrayList<JcAudio> jcAudios;
    ArrayList<String> songName=new ArrayList<>();
    int isGuru;
    boolean isGuruScreen;
    AddNotesFragment addNotesFragment;
    private  String id;
    private  String category;
    public  NoteAudioPlayerFragment(int isGuru, boolean isGuruScreen,  String id, String category, boolean isEditable) {
        this.category=category;
        this.isGuru=isGuru;
        this.isGuruScreen=isGuruScreen;
        this.isEditable=isEditable;
        this.id=id;
    }
    public  NoteAudioPlayerFragment() {

    }
    public static NoteAudioPlayerFragment newInstance() {
        NoteAudioPlayerFragment fragment = new NoteAudioPlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            Toast.makeText(getContext(), "category: "+category+" tag: "+id, Toast.LENGTH_LONG).show();

        }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private boolean isFragmentVisible = false;

    private MediaPlayer mediaPlayer;
    private boolean isEditable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_music2, container, false);
        jcPlayerView =  view.findViewById(R.id.jcplayer);
        jcPlayerView.setVisibility(View.GONE);
        jcAudios = new ArrayList<>();
        String userRole=SharedPreferencesHelper.getUserRole(getContext());
        getParentFragmentManager().setFragmentResultListener("notesAudio", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                id = result.getString("data");
                isGuru = result.getInt("isGuru");
                category = result.getString("category");
                isEditable = false;
                makeApiCall(id, getView());
            }


        });

        return view;
    }
    public void onResume() {
        super.onResume();
    }
    private void setFragment(Fragment fragment, String fragmentTag) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment currentFragment = fragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }


        Fragment fragmentTemp = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragmentTemp == null) {
            fragmentTemp = fragment;
            fragmentTransaction.add(R.id.activity_main_fragment_container, fragmentTemp, fragmentTag);
        } else {
            fragmentTransaction.show(fragmentTemp);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
    }


    private void makeApiCall(String id, View view) {
            ApiInterface methods = ApiClient.getClient().create(ApiInterface.class);
            int userId= SharedPreferencesHelper.getUserId(getContext());
            Call<ArrayList<MusicNoteModel>> call;
            String token=SharedPreferencesHelper.getUserJwtToken(getContext());
        if(category!=null && id!=null){
            call=methods.getNotesDetailData("Bearer "+token, userId, Integer.parseInt(id), Integer.parseInt(category));
        }
        else if(id==null && category!=null){
            call=methods.getNotesDetailData("Bearer "+token, userId, Integer.parseInt(category));
        }
        else{
            call = methods.getNotesDetailData("Bearer "+token, userId, Integer.parseInt(id));
        }
            call.enqueue(new Callback<ArrayList<MusicNoteModel>>() {
                @Override
                public void onResponse(Call<ArrayList<MusicNoteModel>> call, Response<ArrayList<MusicNoteModel>> response) {
                    if (response.isSuccessful() && response.body()!=null) {
                        ArrayList<MusicNoteModel> notesModels = response.body();

                        setupRecyclerView(notesModels, view);
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<MusicNoteModel>> call, Throwable t) {

                }
            });


        }
    private void makePublicApiCall( View view) {
        ApiInterface methods = ApiClient.getClient().create(ApiInterface.class);
        int userId= SharedPreferencesHelper.getUserId(getContext());
//            Toast.makeText(getContext(), "Id is : "+id, Toast.LENGTH_SHORT).show();
        Call<ArrayList<MusicNoteModel>>call;
        String token=SharedPreferencesHelper.getUserJwtToken(getContext());
        if(category!=null && id!=null){
            call=methods.getPublicNotes("Bearer "+token, Integer.parseInt(id), Integer.parseInt(category));
        }
        else if(id==null && category!=null){
            call =methods.getPublicNotesByCategory("Bearer "+token, Integer.parseInt(category));
        }
        else {
            call = methods.getPublicNotes("Bearer "+token, Integer.parseInt(id));
        }

        call.enqueue(new Callback<ArrayList<MusicNoteModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MusicNoteModel>> call, Response<ArrayList<MusicNoteModel>> response) {
                if (response.isSuccessful() && response.body()!=null) {
                    ArrayList<MusicNoteModel> notesModels = response.body();
                    if(notesModels.isEmpty()){
                        TextView noNotesLabel=view.findViewById(R.id.noNotes);
                        noNotesLabel.setVisibility(View.VISIBLE);
                    }
                    else {

                        listView = view.findViewById(R.id.songsList);
                        MusicNoteAdapter adapter = new MusicNoteAdapter(requireContext(), notesModels);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                TextView textViewId = view.findViewById(R.id.textViewId);
                                int noteId = (int) textViewId.getTag();
                                Bundle result = new Bundle();
                                result.putString("id", String.valueOf(noteId));
                                getParentFragmentManager().setFragmentResult("idFromMusicFragment", result);
                                EditNotesFragment editNotesFragment = new EditNotesFragment(isEditable);
                                setFragment(editNotesFragment, "addNotes1");
                            }
                        });


                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MusicNoteModel>> call, Throwable t) {

            }
        });


    }
    private void setupRecyclerView(ArrayList<MusicNoteModel> dataList, View view) {
        listView = view.findViewById(R.id.songsList);
        ArrayList<MusicNoteModel> audioNamesToShow = new ArrayList<>();

        for (MusicNoteModel notesModel : dataList) {
            String self = "Self";
            String guru = "Guru";

            if (isGuru == 0 && self.equals(notesModel.getNoteSource())) {
                audioNamesToShow.add(notesModel);
            } else if (isGuru == 1 && guru.equals(notesModel.getNoteSource())) {
                audioNamesToShow.add(notesModel);
            }
        }

        // Set up your onItemClick logic...
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textViewId = view.findViewById(R.id.textViewId);
                int noteId = (int) textViewId.getTag();
                Bundle result = new Bundle();
                result.putString("id", String.valueOf(noteId));
                getParentFragmentManager().setFragmentResult("idFromMusicFragment", result);
                EditNotesFragment editNotesFragment = new EditNotesFragment(isEditable);
                setFragment(editNotesFragment, "addNotes1");
            }
        });
        if (audioNamesToShow.isEmpty()) {
            TextView noNotesLabel = view.findViewById(R.id.noNotes);
            noNotesLabel.setVisibility(View.VISIBLE);
        } else {
            MusicNoteAdapter adapter = new MusicNoteAdapter(requireContext(), audioNamesToShow);
            listView.setAdapter(adapter);

        }
    }







//    }

    public static class MusicNoteAdapter extends BaseAdapter {
        private ArrayList<MusicNoteModel> musicNoteList;
        private LayoutInflater inflater;

        public MusicNoteAdapter(Context context, ArrayList<MusicNoteModel> musicNoteList) {
            this.musicNoteList = musicNoteList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return musicNoteList.size();
        }

        @Override
        public Object getItem(int position) {
            return musicNoteList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_music_note, parent, false);
            }
            TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
            TextView textViewId = convertView.findViewById(R.id.textViewId);
            TextView desc=convertView.findViewById(R.id.description);
            MusicNoteModel noteModel = (MusicNoteModel) getItem(position);
            textViewTitle.setText(noteModel.getTitle());
            desc.setText(noteModel.getDescription());
            // Set the id as a tag in the textViewId
            textViewId.setTag(noteModel.getNoteId());

            return convertView;
        }
    }


}