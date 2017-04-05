package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lexlevi.sweapp.Models.Course;
import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.R;
import com.example.lexlevi.sweapp.Singletons.ChatServerClient;
import com.example.lexlevi.sweapp.Singletons.UserSession;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DashboardProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DashboardProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String _username;
    private String _firstName;
    private String _lastname;
    private String _major;
    private String _year;
    private String _email;

    private View _rootView;
    private TextView _usernameTextView;
    private TextView _nameTextView;
    private TextView _yearTextView;
    private TextView _majorTextView;
    private TextView _emailTextView;
    private TextView _coursesTextView;


    private OnFragmentInteractionListener mListener;

    public DashboardProfile() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DashboardProfile newInstance(String param1, String param2) {
        DashboardProfile fragment = new DashboardProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _rootView = inflater.inflate(R.layout.fragment_dashboard_profile, container, false);
        _username = UserSession.getInstance().getCurrentUser().getUserName();
        _firstName = UserSession.getInstance().getCurrentUser().getFirstName();
        _lastname = UserSession.getInstance().getCurrentUser().getLastName();
        _year = UserSession.getInstance().getCurrentUser().getYear();
        _email = UserSession.getInstance().getCurrentUser().getEmail();
        _major = UserSession.getInstance().getCurrentUser().getMajor();

        _usernameTextView = (TextView) _rootView.findViewById(R.id.username_profile);
        _nameTextView = (TextView) _rootView.findViewById(R.id.name_profile) ;
        _yearTextView = (TextView) _rootView.findViewById(R.id.profile_year);
        _emailTextView = (TextView) _rootView.findViewById(R.id.profile_email);
        _majorTextView = (TextView) _rootView.findViewById(R.id.profile_major) ;
        _coursesTextView = (TextView) _rootView.findViewById(R.id.profile_courses);

        String courses = "";
        _usernameTextView.setText(_username);
        _nameTextView.setText(_firstName + " " + _lastname);
        _yearTextView.setText("( "+_year+" )");
        _emailTextView.setText("E-mail: " +_email);
        _majorTextView.setText("Major: " + _major);

        for (Course c : UserSession.getInstance().getCurrentUser().getCourses()) {
            courses += c.getName();
            courses += "\r\n";
        }

        _coursesTextView.setText(courses);
        
        return _rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onProfileFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onProfileFragmentInteraction(Uri uri);
    }
}
