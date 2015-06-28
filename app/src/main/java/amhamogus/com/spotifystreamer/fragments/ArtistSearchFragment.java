package amhamogus.com.spotifystreamer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import amhamogus.com.spotifystreamer.R;
import amhamogus.com.spotifystreamer.activities.TopTracks;
import amhamogus.com.spotifystreamer.model.MyArtist;
import amhamogus.com.spotifystreamer.adapters.MyArtistAdapter;
import amhamogus.com.spotifystreamer.net.SpotifyRequest;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArtistSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArtistSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistSearchFragment extends Fragment {

    protected ArrayList<MyArtist> artistList;

    private ListView listView;

    /**
     * Custom adapter that maps artist object to list view.
     */
    protected MyArtistAdapter myArtistAdapter;

    private ProgressBar progressBar;

    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ArtistSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArtistSearchFragment newInstance() {
        ArtistSearchFragment fragment = new ArtistSearchFragment();
        return fragment;
    }

    public ArtistSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_artist_search, container, false);
        listView = (ListView) fragmentView.findViewById(R.id.artistListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyArtist selectedArtist = (MyArtist) parent.getItemAtPosition(position);

                // Package selected artist's name and ID to send to top track activity.
                Bundle bundle = new Bundle();
                bundle.putString("ARTIST_NAME", selectedArtist.getName());
                bundle.putString("ARTIST_ID", selectedArtist.getId());

                Log.d("MAIN ACTIVITY", "ARTIST NAME = " + selectedArtist.getName());

                Intent intent =
                        new Intent(getActivity().getApplicationContext(), TopTracks.class);
                intent.putExtras(bundle);
                startActivity(intent, bundle);
            }
        });

        // Hide progress bar.
        progressBar = (ProgressBar) fragmentView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        // Get reference to search term.
        SearchView searchInput = (SearchView) fragmentView.findViewById(R.id.search_input);

        // Listener respond to user searches.
        searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.equals("")) {
                    // Show toast if search it submitted without a search term.
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please Enter Artist Name", Toast.LENGTH_SHORT).show();
                } else {

                    // Send search request to Spotify in the background.
                    new SpotifyWorkerTask().execute(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // Restore saved views if we're recreating the fragment for
        // a change in orientation.
        if (savedInstanceState != null) {
            artistList = savedInstanceState.getParcelableArrayList("artists");
            myArtistAdapter =
                    new MyArtistAdapter(getActivity().getApplicationContext(), 0, artistList);

        }
        listView.setAdapter(myArtistAdapter);
        return fragmentView;


    }

    // TODO: Rename method, update argument and hook method into UI event

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        outstate.putParcelableArrayList("artists", artistList);
        super.onSaveInstanceState(outstate);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * Helper class that requests a list or Artists from Spotify.
     */
    private class SpotifyWorkerTask extends AsyncTask<String, String, ArrayList<MyArtist>> {

        SpotifyRequest call;

        protected ArrayList<MyArtist> doInBackground(String... strings) {
            call = new SpotifyRequest();
            return call.searchArtist(strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Check if Progress Bar has been access
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MyArtist> returnedArtists) {
            super.onPostExecute(returnedArtists);

            progressBar.setVisibility(View.INVISIBLE);

            if (returnedArtists.size() == 0) {
                // Zero artists returned from Spotify api.
                // Inform the user by displaying toast message.
                Toast.makeText(getActivity().getApplicationContext(),
                        getResources().getString(R.string.no_artists), Toast.LENGTH_SHORT).show();
            } else {
                artistList = returnedArtists;

                myArtistAdapter =
                        new MyArtistAdapter(getActivity().getApplicationContext(), 0, returnedArtists);

                // Populate list view with artists returned from Spotify.
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MyArtist selectedArtist = (MyArtist) parent.getItemAtPosition(position);

                        // Package selected artist's name and ID to send to top track activity.
                        Bundle bundle = new Bundle();
                        bundle.putString("ARTIST_NAME", selectedArtist.getName());
                        bundle.putString("ARTIST_ID", selectedArtist.getId());

                        Log.d("MAIN ACTIVITY", "ARTIST NAME = " + selectedArtist.getName());

                        Intent intent =
                                new Intent(getActivity().getApplicationContext(), TopTracks.class);
                        intent.putExtras(bundle);
                        startActivity(intent, bundle);
                    }
                });
                listView.setAdapter(myArtistAdapter);
            }
        }
    }

}
