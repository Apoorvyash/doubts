package com.subtlerr.singtico.Gandhaar.Gandhaar.Music.notes;
import static com.subtlerr.singtico.practice.instrument_fragments.TablaFragment.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentResultListener;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.subtlerr.singtico.R;
import com.subtlerr.singtico.Singtico;
import com.subtlerr.singtico.helpers.SharedPreferencesHelper;

public class GuruStudentNoteCollection extends Fragment {

    private final String[] itemsStudent = new String[]{"Self", "Guru", "Singtico"};
    private final String[] itemsGuru = new String[]{"Self", "Singtico"};
    private final int[] tabIcons = {R.drawable.ic_star_bullet_golden};

    private static String data;
    private static String category;
    private static String name;
    ViewPager2 viewPager;
    public static GuruStudentNoteCollection getInstance() {
        return new GuruStudentNoteCollection();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music, container, false);
    }
    public String userRole;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TabLayout tabLayout = view.findViewById(R.id.self_section_tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager = view.findViewById(R.id.viewpager_self_section);
         userRole= SharedPreferencesHelper.getUserRole(getContext());
        getParentFragmentManager().setFragmentResultListener("TagId", this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                         data = result.getString("id");
                         category=result.getString("category");
                         name=result.getString("name");
                    }
                });
//        GuruStudentNoteCollection.CommunityTabLayoutAdapter communityTabLayoutAdapter = new CommunityTabLayoutAdapter((FragmentActivity) requireContext());
        GuruStudentNoteCollection.CommunityTabLayoutAdapter communityTabLayoutAdapter;
        if (userRole.equals("Student")) {
            communityTabLayoutAdapter = new CommunityTabLayoutAdapter((FragmentActivity) requireContext(), true);
        } else {
            communityTabLayoutAdapter = new CommunityTabLayoutAdapter((FragmentActivity) requireContext(), false);
        }

        viewPager.setAdapter(communityTabLayoutAdapter);
        TextView nametext=view.findViewById(R.id.Name2);
        nametext.setText(name);
        if(userRole.equals("Guru")){
        new TabLayoutMediator(tabLayout, viewPager, ((tab, position) -> tab.setText(itemsGuru[position]))).attach();}
        else        {new TabLayoutMediator(tabLayout, viewPager, ((tab, position) -> tab.setText(itemsStudent[position]))).attach();}

}
    public class CommunityTabLayoutAdapter extends FragmentStateAdapter {

        private boolean isStudent;

        public CommunityTabLayoutAdapter(@NonNull FragmentActivity fragmentActivity, boolean isStudent) {
            super(fragmentActivity);
            this.isStudent = isStudent;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            NoteAudioPlayerFragment noteAudioPlayerFragment = NoteAudioPlayerFragment.newInstance();
            Bundle result = new Bundle();
            result.putString("data", data);
            result.putString("category", category);
            Toast.makeText(getContext(), "isStudent" + isStudent, Toast.LENGTH_SHORT).show();
            if (isStudent) {
                switch (position) {
                    case 0:
                        result.putInt("isGuru", 0);
                        result.putBoolean("isGuruScreen", false);
                        getParentFragmentManager().setFragmentResult("notesAudio", result);
                        return new NoteAudioPlayerFragment();
                    case 1:
                        result.putInt("isGuru", 1);
                        result.putBoolean("isGuruScreen", true);
                        getParentFragmentManager().setFragmentResult("notesAudio", result);
                        return new NoteAudioPlayerFragment();
                    case 2:
                        result.putInt("isGuru", 2);
                        getParentFragmentManager().setFragmentResult("notesAudio", result);
                        return new PublicNotesFragment();
                }
            } else {
                switch (position) {
                    case 0:
                        result.putInt("isGuru", 0);
                        result.putBoolean("isGuruScreen", false);
                        getParentFragmentManager().setFragmentResult("notesAudio", result);
                        return new NoteAudioPlayerFragment();
                    case 1:
                        result.putInt("isGuru", 2);
                        result.putBoolean("isGuruScreen", false);
                        getParentFragmentManager().setFragmentResult("notesAudio", result);
                        return new PublicNotesFragment();
                }
            }

            getParentFragmentManager().setFragmentResult("notesAudio", result);
            return new PublicNotesFragment();
        }


        @Override
        public int getItemCount() {
            if (isStudent) {
                return itemsStudent.length;
            } else {
                return itemsGuru.length;
            }
        }
    }

}


// Instances of this class are fragments representing a single
// object in our collection.
