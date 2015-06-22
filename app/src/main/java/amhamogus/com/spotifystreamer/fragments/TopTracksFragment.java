package amhamogus.com.spotifystreamer.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import amhamogus.com.spotifystreamer.R;
import amhamogus.com.spotifystreamer.model.MyTracks;
import amhamogus.com.spotifystreamer.model.TrackListAdapter;
import amhamogus.com.spotifystreamer.net.SpotifyRequest;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TopTracksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TopTracksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopTracksFragment extends Fragment {

    String TAG = "TRACK-FRAGMENT";

    // Keys for arguments being passed to this fragment.
    private static final String ARG_PARAM1 = "ARTIST_ID";
    private static final String ARG_PARAM2 = "ARTIST_NAME";

    // Values that are passed to this fragment.
    private String artistID;
    private String artistName;

    // Data structure that will populate the listview.
    private ArrayList<MyTracks> topTracks;

    private TrackListAdapter adapter;

    // Listview that will be updated with data from instance variable topTracks.
    private ListView mList;

    // TODO: Part 2
    private OnFragmentInteractionListener mListener;

    Parcelable fragmentState;

    /**
     * Factory method used to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id   Parameter 1.
     * @param name Parameter 2.
     * @return A new instance of fragment TopTracksFragment.
     */
    public static TopTracksFragment newInstance(String id, String name) {
        TopTracksFragment fragment = new TopTracksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id);
        args.putString(ARG_PARAM2, name);
        fragment.setArguments(args);
        return fragment;
    }

    public TopTracksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            artistID = getArguments().getString(ARG_PARAM1);
            artistName = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentLayout =
                inflater.inflate(R.layout.fragment_top_tracks, container, false);
        mList = (ListView) fragmentLayout.findViewById(R.id.top_track_list);

        if (savedInstanceState == null) {
            // This is the first time running this fragment
            // so we retrieve the top tracks from Spotify.
            new TopTrackWorker().execute(artistID);
        } else {
            // Fragment has been run before, so we'll recreate
            // the fragment from saved instance data.
            topTracks = savedInstanceState.getParcelableArrayList("tracks");
            adapter =
                    new TrackListAdapter(getActivity().getApplicationContext(), 0, topTracks);
            mList.setAdapter(adapter);
        }
        return fragmentLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        outstate.putParcelableArrayList("tracks", topTracks);
        super.onSaveInstanceState(outstate);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onTrackSelected(String songName) {
        if (mListener != null) {
            mListener.onFragmentInteraction(songName);
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
        public void onFragmentInteraction(String songName);
    }

    /**
     * Helper class that requests top tracks from Spotify.
     */
    private class TopTrackWorker extends AsyncTask<String, String, ArrayList<MyTracks>> {

        SpotifyRequest topTrackRequest;

        @Override
        protected ArrayList<MyTracks> doInBackground(String... strings) {
            topTrackRequest = new SpotifyRequest();
            topTracks = topTrackRequest.searchTopTracks(strings[0]);
            return topTracks;
        }

        @Override
        protected void onPostExecute(ArrayList<MyTracks> tracks) {

            if (tracks != null) {
                if (tracks.size() == 0) {
                    // Zero tracks returned from Spotify api.
                    // Inform the user by displaying toast message.
                    Toast.makeText(getActivity().getApplicationContext(),
                            getResources().getString(R.string.no_tracks), Toast.LENGTH_SHORT).show();
                } else {
                    // Display the top tracks returned from from Spotify api.
                    adapter =
                            new TrackListAdapter(getActivity().getApplicationContext(), 0, tracks);
                    mList.setAdapter(adapter);
                }
            }
        }
    }
}
