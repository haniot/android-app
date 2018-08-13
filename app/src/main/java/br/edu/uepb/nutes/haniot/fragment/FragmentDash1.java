package br.edu.uepb.nutes.haniot.fragment;

import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDash1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDash1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDash1 extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.textDate)
    TextView textDate;
    @BindView(R.id.buttonArrowLeft)
    ImageButton btnArrowLeft;
    @BindView(R.id.buttonArrowRight)
    ImageButton btnArrowRight;
    @BindView(R.id.stepsProgressBar)
    CircularProgressBar stepsProgressBar;
    @BindView(R.id.lightProgress1)
    CircularProgressBar caloriesProgressBar;
    @BindView(R.id.lightProgress2)
    CircularProgressBar distanceProgressBar;
    private int numberOfSteps;
    private int numberOfCalories;
    private float distance;

    public FragmentDash1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentDash1.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDash1 newInstance(String param1, String param2) {
        FragmentDash1 fragment = new FragmentDash1();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_dash1, container, false);
        ButterKnife.bind(this,view);

        String timeStamp = new SimpleDateFormat("EEEE / dd / yyyy", Locale.US).format(Calendar.getInstance().getTime());
        textDate.setText(timeStamp);

        btnArrowLeft.setOnClickListener(this);
        btnArrowRight.setOnClickListener(this);

        //Estes dados devem vim do servidor
        numberOfSteps = 70;
        numberOfCalories = 80;
        distance = 28.3f;

        //Seta o progresso
        stepsProgressBar.setProgress(0);
        caloriesProgressBar.setProgress(0);
        distanceProgressBar.setProgress(0);

        stepsProgressBar.setProgressWithAnimation(numberOfSteps,2500);
        caloriesProgressBar.setProgressWithAnimation(numberOfCalories,4000);
        distanceProgressBar.setProgressWithAnimation(distance,4000);


        stepsProgressBar.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        stepsProgressBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAlertDanger));
        caloriesProgressBar.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        caloriesProgressBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
        distanceProgressBar.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        distanceProgressBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAlertDanger));

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonArrowLeft:
                Toast.makeText(getActivity(), "leftBtn", Toast.LENGTH_SHORT).show();
                textDate.setText("Left Button Clicked");
                break;
            case R.id.buttonArrowRight:
                Toast.makeText(getActivity(), "rightBtn", Toast.LENGTH_SHORT).show();
                textDate.setText("Right Button Clicked");
                break;

        }
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
