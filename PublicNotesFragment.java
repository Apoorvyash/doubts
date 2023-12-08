package com.subtlerr.singtico.Gandhaar.Gandhaar.Music.notes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.subtlerr.singtico.Gandhaar.Gandhaar.Api.ApiClient;
import com.subtlerr.singtico.Gandhaar.Gandhaar.Api.ApiInterface;
import com.subtlerr.singtico.Gandhaar.Gandhaar.Music.notes.data.MusicNoteModel;
import com.subtlerr.singtico.R;
import com.subtlerr.singtico.helpers.SharedPreferencesHelper;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicNotesFragment extends Fragment {



    public PublicNotesFragment() {
        // Required empty public constructor
    }


    public static PublicNotesFragment newInstance(String param1, String param2) {
        PublicNotesFragment fragment = new PublicNotesFragment();
        return fragment;
    }
    ListView listView;
    String category, id;
    boolean isEditable;
    int isGuru;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_public_notes, container, false);
        getParentFragmentManager().setFragmentResultListener("notesAudio", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                id = result.getString("data");
                isGuru = result.getInt("isGuru");
                category = result.getString("category");
                isEditable = false;
                makePublicApiCall(view);
            }


        });


        return view;
    }
    private void makePublicApiCall( View view) {
        ApiInterface methods = ApiClient.getClient().create(ApiInterface.class);
        int userId= SharedPreferencesHelper.getUserId(getContext());
//            Toast.makeText(getContext(), "Id is : "+id, Toast.LENGTH_SHORT).show();
        Call<ArrayList<MusicNoteModel>> call;
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

                        listView = view.findViewById(R.id.songsListpublic);
                        NoteAudioPlayerFragment.MusicNoteAdapter adapter = new NoteAudioPlayerFragment.MusicNoteAdapter(requireContext(), notesModels);
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
            NoteAudioPlayerFragment.MusicNoteAdapter adapter = new NoteAudioPlayerFragment.MusicNoteAdapter(requireContext(), audioNamesToShow);
            listView.setAdapter(adapter);
        }

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

}