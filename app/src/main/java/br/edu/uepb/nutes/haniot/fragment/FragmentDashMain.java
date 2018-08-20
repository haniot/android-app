package br.edu.uepb.nutes.haniot.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import br.edu.uepb.nutes.haniot.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDashMain.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDashMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDashMain extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.frame1)
    FrameLayout frame1;

    @BindView(R.id.frame2)
    FrameLayout frame2;

    private FragmentDash1 fragmentDash1;
    private FragmentDash2 fragmentDash2;

    private String dateFrag1;
    private final String TAG_FRAG1 = "fragment1";

    public FragmentDashMain() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentDashMain.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDashMain newInstance(String param1, String param2) {
        FragmentDashMain fragment = new FragmentDashMain();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        fragmentDash1 = new FragmentDash1();
        if (savedInstanceState != null){
            Bundle bundle = new Bundle();
            bundle.putString("date",savedInstanceState.getString("date"));
            System.out.println("====================fragment main passando a data: "+bundle.getString("date"));
            fragmentDash1.setArguments(bundle);
        }
        fragmentDash2 = new FragmentDash2();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String date;
        date = fragmentDash1.getData();
        if (date == null || date.equals("")){
            date = fragmentDash1.getToday();
        }
        outState.putString("date",date);
        fragmentDash1 = new FragmentDash1();
        fragmentDash1.setArguments(outState);
        if (fragmentDash1.isAdded()) {
            getFragmentManager().putFragment(outState, TAG_FRAG1, fragmentDash1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_dash_main, container, false);
        ButterKnife.bind(this,view);

        //Addicting the two main fragmants to dashboard

        if (savedInstanceState == null){
            getFragmentManager().beginTransaction().replace(R.id.frame1, this.fragmentDash1, this.TAG_FRAG1)
                    .commit();
        }

        getFragmentManager().beginTransaction().replace(R.id.frame2, this.fragmentDash2, fragmentDash2.getTag())
                 .commit();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
